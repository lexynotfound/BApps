package com.raihanardila.bapps.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

@ExperimentalCoroutinesApi
class MainTestRule(
    val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestRule {
    override fun apply(base: Statement, description: Description?) = object : Statement() {
        @Throws(Throwable::class)
        override fun evaluate() {
            Dispatchers.setMain(dispatcher)

            try {
                base.evaluate()
            } finally {
                Dispatchers.resetMain() // Correctly resetting the main dispatcher
            }
        }
    }
}
