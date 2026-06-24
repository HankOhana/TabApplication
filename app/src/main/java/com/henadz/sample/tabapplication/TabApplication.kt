package com.henadz.sample.tabapplication

import android.app.Application
import com.henadz.sample.tabapplication.data.di.dataModule
import com.henadz.sample.tabapplication.domain.di.domainModule
import com.henadz.sample.tabapplication.ui.di.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TabApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TabApplication)
            modules(domainModule, dataModule, uiModule)
        }
    }
}
