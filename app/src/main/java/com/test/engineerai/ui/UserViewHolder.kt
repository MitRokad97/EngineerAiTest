package com.test.engineerai.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.engineerai.R
import com.test.engineerai.api.UserListApi
import com.test.engineerai.util.GlideApp

class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val tvUserTitle: TextView = view.findViewById(R.id.tvUserTitle)
    private val ivUser: ImageView = view.findViewById(R.id.ivUser)
    private val rvUserSubList: RecyclerView = view.findViewById(R.id.rvUserSubList)

    companion object {
        fun create(parent: ViewGroup): UserViewHolder {
            return UserViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_user_list, parent, false)
            )
        }
    }

    fun bind(userList: UserListApi.ListingResponse.Data.Users?) {

        tvUserTitle.text = "${userList?.name}"
        GlideApp.with(itemView)
                .load(userList?.image)
                .placeholder(android.R.color.darker_gray)
                .circleCrop()
                .into(ivUser)

        rvUserSubList.layoutManager = GridLayoutManager(itemView.context, 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when {
                        userList?.items.orEmpty().count() % 2 == 0 -> 1
                        else -> when (position) {
                            0 -> 2
                            else -> 1
                        }
                    }
                }
            }
        }
        rvUserSubList.isNestedScrollingEnabled = false
        rvUserSubList.adapter = UsersPostAdapter(userList?.items)
    }
}