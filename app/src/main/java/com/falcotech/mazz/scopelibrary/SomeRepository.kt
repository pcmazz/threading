package com.falcotech.mazz.scopelibrary

import com.falcotech.mazz.scopelibrary.factory.IOCoroutineScope
import com.falcotech.mazz.scopelibrary.factory.asyncIO
import com.falcotech.mazz.scopelibrary.factory.withIO
import kotlinx.coroutines.delay

class SomeRepository(private val coroutineScope: IOCoroutineScope) {

    suspend fun getSomethingExpensive() = withIO {
        delay(5000)
        "suspend function is complete!"
    }

    fun getSomethingExpensiveUnstructured() = coroutineScope.asyncIO {
        delay(5000)
        "deferred function is complete!"
    }
}