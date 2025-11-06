package com.angelomarzo.hackernews

import android.app.Application

class HackerNewsApplication: Application() {

    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(context = this)
    }
}