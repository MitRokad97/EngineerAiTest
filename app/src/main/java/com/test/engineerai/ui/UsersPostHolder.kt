package com.test.engineerai.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.test.engineerai.R
import com.test.engineerai.util.GlideApp

class UsersPostHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val ivImageUser: ImageView = view.findViewById(R.id.ivImageUser)

    companion object {
        fun create(parent: ViewGroup): UsersPostHolder {
            return UsersPostHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_user_sub_list, parent, false)
            )
        }
    }

    fun bind(imageUrl: String?) {
        GlideApp.with(itemView)
                .load(imageUrl)
                .placeholder(android.R.color.darker_gray)
                .into(ivImageUser)
    }
}