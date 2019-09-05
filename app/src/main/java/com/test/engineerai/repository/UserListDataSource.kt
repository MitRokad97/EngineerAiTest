/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.test.engineerai.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.test.engineerai.util.NetworkState
import com.test.engineerai.api.UserListApi
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executor

class UserListDataSource(
    private val redditListApi: UserListApi,
    private val retryExecutor: Executor
) : PageKeyedDataSource<Int, UserListApi.ListingResponse.Data.Users>() {

    companion object {
        const val OFFSET = 10
        const val LIMIT = 10
    }


    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null

    /**
     * There is no sync on the state because paging will always call loadInitial first then wait
     * for it to return some success value before calling loadAfter.
     */
    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, UserListApi.ListingResponse.Data.Users>
    ) {
        // ignored, since we only ever append to our initial load
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, UserListApi.ListingResponse.Data.Users>
    ) {
        networkState.postValue(NetworkState.LOADING)
        redditListApi.getSearchByDate(
            params.key, LIMIT
        ).enqueue(
            object : retrofit2.Callback<UserListApi.ListingResponse> {
                override fun onFailure(call: Call<UserListApi.ListingResponse>, t: Throwable) {
                    retry = {
                        loadAfter(params, callback)
                    }
                    networkState.postValue(NetworkState.error(t.message ?: "unknown err"))
                }

                override fun onResponse(
                    call: Call<UserListApi.ListingResponse>,
                    response: Response<UserListApi.ListingResponse>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()?.data
                        val items = data?.users ?: emptyList()
                        retry = null
                        callback.onResult(items, params.key + OFFSET)
                        networkState.postValue(NetworkState.LOADED)
                    } else {
                        retry = {
                            loadAfter(params, callback)
                        }
                        networkState.postValue(
                            NetworkState.error("error code: ${response.code()}")
                        )
                    }
                }
            }
        )
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, UserListApi.ListingResponse.Data.Users>
    ) {
        val request = redditListApi.getSearchByDate(
            0,
            LIMIT
        )
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        // triggered by a refresh, we better execute sync
        try {
            val response = request.execute()
            val data = response.body()?.data
            val items = data?.users ?: emptyList()
            retry = null
            networkState.postValue(NetworkState.LOADED)
            initialLoad.postValue(NetworkState.LOADED)
            callback.onResult(items, OFFSET, 0 + OFFSET)
        } catch (ioException: IOException) {
            retry = {
                loadInitial(params, callback)
            }
            val error = NetworkState.error(ioException.message ?: "unknown error")
            networkState.postValue(error)
            initialLoad.postValue(error)
        }
    }
}