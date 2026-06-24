package com.henadz.sample.tabapplication.ui.di

import com.henadz.sample.tabapplication.ui.table.TableViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule: Module =
    module {
        viewModel { (rows: Int, cols: Int) ->
            TableViewModel(
                rows = rows,
                cols = cols,
                getTableUseCase = get(),
                toggleCellColorUseCase = get(),
                updateCellDataUseCase = get(),
                clearSessionUseCase = get(),
                resetTableUseCase = get(),
            )
        }
    }
