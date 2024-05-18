package com.raihanardila.bapps

import android.app.Application
import com.raihanardila.bapps.core.module.appModule
import com.raihanardila.bapps.core.module.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.logger.Level

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalContext.startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MyApplication)
            modules(
                listOf(
                    appModule,
                    networkModule
                )
            )
        }
    }
}