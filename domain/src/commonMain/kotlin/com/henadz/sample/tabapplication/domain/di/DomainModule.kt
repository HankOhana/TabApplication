package com.henadz.sample.tabapplication.domain.di

import com.henadz.sample.tabapplication.domain.usecase.ClearSessionUseCase
import com.henadz.sample.tabapplication.domain.usecase.GetTableUseCase
import com.henadz.sample.tabapplication.domain.usecase.ResetTableUseCase
import com.henadz.sample.tabapplication.domain.usecase.ToggleCellColorUseCase
import com.henadz.sample.tabapplication.domain.usecase.UpdateCellDataUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val domainModule: Module =
    module {
        factory { GetTableUseCase(get()) }
        factory { UpdateCellDataUseCase(get()) }
        factory { ToggleCellColorUseCase(get()) }
        factory { ClearSessionUseCase(get()) }
        factory { ResetTableUseCase(get()) }
    }
