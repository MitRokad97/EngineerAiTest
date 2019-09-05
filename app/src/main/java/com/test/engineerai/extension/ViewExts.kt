package com.test.engineerai.extension

import android.view.View

fun View.showViewWithCondition(isShow: Boolean) {
    if (isShow) {
        this.showView()
    } else {
        this.hideView()
    }
}

fun View.showView() {
    this.visibility = View.VISIBLE
}

fun View.hideView() {
    this.visibility = View.GONE
}