package com.raihanardila.bapps.core.module

import com.raihanardila.bapps.core.data.local.repository.AuthRepository
import com.raihanardila.bapps.core.data.local.repository.BMainRepository
import com.raihanardila.bapps.core.data.local.repository.BStoriesRepository
import org.koin.dsl.module

val repoModule = module {
    single { AuthRepository(get()) }
    single { BMainRepository(get()) }
    single { BStoriesRepository(get()) }
}