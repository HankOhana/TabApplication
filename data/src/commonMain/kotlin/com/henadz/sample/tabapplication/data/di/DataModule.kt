package com.henadz.sample.tabapplication.data.di

import com.henadz.sample.tabapplication.data.repository.InMemoryTableRepository
import com.henadz.sample.tabapplication.domain.repository.TableRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val dataModule: Module =
    module {
        single<TableRepository> { InMemoryTableRepository() }
    }
