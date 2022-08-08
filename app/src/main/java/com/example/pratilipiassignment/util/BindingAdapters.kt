package com.example.pratilipiassignment.util

import android.webkit.WebView

import androidx.databinding.BindingAdapter




class BindingAdapters {
    @BindingAdapter("loadUrl")
    fun loadUrl(view: WebView, url: String?) {
        view.loadUrl(url!!)
    }
}