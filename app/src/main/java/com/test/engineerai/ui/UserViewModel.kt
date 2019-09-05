package com.test.engineerai.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import com.test.engineerai.repository.UserListRepository
import com.test.engineerai.api.UserListApi
import java.util.concurrent.Executors

class UserViewModel : ViewModel() {
    private val repository =
            UserListRepository(
                    UserListApi.create(),
                    Executors.newFixedThreadPool(5)
            )

    companion object{
        private const val PAGE_SIZE = 10
    }

    private val userName = MutableLiveData<String>()
    private val repositoryResult = map(userName) {
        repository.postsOfSubreddit(it, PAGE_SIZE)
    }
    val posts = switchMap(repositoryResult) { it.pagedList }!!
    val networkState = switchMap(repositoryResult) { it.networkState }!!


    fun showUserList(userName: String): Boolean {
        if (this.userName.value == userName) {
            return false
        }
        this.userName.value = userName
        return true
    }

    fun retry() {
        val list = repositoryResult?.value
        list?.retry?.invoke()
    }
}
