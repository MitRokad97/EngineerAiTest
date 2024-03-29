package com.test.engineerai.repository

import androidx.annotation.MainThread
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.test.engineerai.util.UserListing
import com.test.engineerai.api.UserListApi
import java.util.concurrent.Executor

class UserListRepository(
    private val redditListApi: UserListApi,
    private val networkExecutor: Executor
) {
    @MainThread
    fun postsOfSubreddit(subReddit: String, pageSize: Int): UserListing<UserListApi.ListingResponse.Data.Users> {
        val sourceFactory = UserListDataSourceFactory(
                redditListApi,
                subReddit,
                networkExecutor
        )

        // We use toLiveData Kotlin extension function here, you could also use LivePagedListBuilder
        val livePagedList = sourceFactory.toLiveData(
                pageSize = pageSize,
                // provide custom executor for network requests, otherwise it will default to
                // Arch Components' IO pool which is also used for disk access
                fetchExecutor = networkExecutor
        )

        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.initialLoad
        }
        return UserListing(
                pagedList = livePagedList,
                networkState = Transformations.switchMap(sourceFactory.sourceLiveData) {
                    it.networkState
                },
                retry = {
                    sourceFactory.sourceLiveData.value?.retryAllFailed()
                },
                refresh = {
                    sourceFactory.sourceLiveData.value?.invalidate()
                },
                refreshState = refreshState
        )
    }
}

