package com.test.engineerai.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.test.engineerai.R
import com.test.engineerai.api.UserListApi
import kotlinx.android.synthetic.main.activity_user_list.*

class UserListActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()

    private lateinit var listAdapter: UserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        initAdapter()
        userViewModel.showUserList("story")
    }

    private fun initAdapter() {
        listAdapter = UserListAdapter { userViewModel.retry() }
        rvUser.adapter = listAdapter
        userViewModel.posts.observe(this, Observer<PagedList<UserListApi.ListingResponse.Data.Users>> {
            listAdapter.submitList(it)
        })
        userViewModel.networkState.observe(this, Observer {
            listAdapter.setNetworkState(it)
        })
    }
}
