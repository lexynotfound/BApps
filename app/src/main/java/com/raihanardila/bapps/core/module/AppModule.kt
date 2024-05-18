package com.raihanardila.bapps.core.module

import org.koin.dsl.module

val appModule = module {
    includes(
        viewModule,
        repoModule
    )
}