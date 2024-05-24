package com.raihanardila.bapps.core.module

import com.raihanardila.bapps.core.data.viewmodel.AuthViewModel
import com.raihanardila.bapps.core.data.viewmodel.BMainViewModel
import com.raihanardila.bapps.core.data.viewmodel.BStoriesViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModule = module {
    viewModelOf(::AuthViewModel)
    viewModelOf(::BMainViewModel)
    viewModelOf(::BStoriesViewModel)

}