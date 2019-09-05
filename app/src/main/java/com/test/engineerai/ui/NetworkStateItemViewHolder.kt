package com.test.engineerai.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.test.engineerai.util.NetworkState
import com.test.engineerai.util.Status.FAILED
import com.test.engineerai.util.Status.RUNNING
import com.test.engineerai.R
import com.test.engineerai.extension.showViewWithCondition

class NetworkStateItemViewHolder(view: View,
                                 private val retryCallback: () -> Unit)
    : RecyclerView.ViewHolder(view) {

    private val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
    private val bnRetry = view.findViewById<Button>(R.id.bnRetry)
    private val errorMessage = view.findViewById<TextView>(R.id.errorMessage)
    init {
        bnRetry.setOnClickListener {
            retryCallback()
        }
    }

    companion object {
        fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.network_state_item, parent, false)
            return NetworkStateItemViewHolder(view, retryCallback)
        }
    }

    fun bindTo(networkState: NetworkState?) {
        progressBar.showViewWithCondition(networkState?.status == RUNNING)
        bnRetry.showViewWithCondition(networkState?.status == FAILED)
        errorMessage.showViewWithCondition(networkState?.message != null)
        errorMessage.text = networkState?.message
    }
}
