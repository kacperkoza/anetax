package com.nostra.koza.anetax

import android.app.Application

/**
 * Created by kacper.koza on 21/10/2017.
 */
class App : Application() {

    companion object {
        lateinit var instance: App
    }

    init {
        instance = this
    }

}