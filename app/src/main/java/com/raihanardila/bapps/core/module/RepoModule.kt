package com.raihanardila.bapps.core.module

import com.raihanardila.bapps.core.data.local.repository.AuthRepository
import org.koin.dsl.module

val repoModule = module {
    single { AuthRepository(get()) }
}