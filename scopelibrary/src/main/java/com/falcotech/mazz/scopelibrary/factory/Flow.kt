package com.falcotech.mazz.scopelibrary.factory

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.coroutineContext

/**
 * Extracts the [dispatcherProvider] from the `coroutineContext` of the **collector** coroutine,
 * then uses its **default** [CoroutineDispatcher] property to call `flowOn(theDispatcher)`,
 * and returns the result.
 *
 * @see flowOn
 */
@ExperimentalCoroutinesApi
fun <T> Flow<T>.flowOnDefault(): Flow<T> = flow {
    flowOn(coroutineContext.dispatcherProvider.default)
        .collect { emit(it) }
}

/**
 * Extracts the [dispatcherProvider] from the `coroutineContext` of the **collector** coroutine,
 * then uses its **io** [CoroutineDispatcher] property to call `flowOn(theDispatcher)`,
 * and returns the result.
 *
 * @see flowOn
 */
@ExperimentalCoroutinesApi
fun <T> Flow<T>.flowOnIO(): Flow<T> = flow {
    flowOn(coroutineContext.dispatcherProvider.io)
        .collect { emit(it) }
}

/**
 * Extracts the [dispatcherProvider] from the `coroutineContext` of the **collector** coroutine,
 * then uses its **main** [CoroutineDispatcher] property to call `flowOn(theDispatcher)`,
 * and returns the result.
 *
 * @see flowOn
 */
@ExperimentalCoroutinesApi
fun <T> Flow<T>.flowOnMain(): Flow<T> = flow {
    flowOn(coroutineContext.dispatcherProvider.main)
        .collect { emit(it) }
}

/**
 * Extracts the [dispatcherProvider] from the `coroutineContext` of the **collector** coroutine,
 * then uses its **mainImmediate** [CoroutineDispatcher] property to call `flowOn(theDispatcher)`,
 * and returns the result.
 *
 * @see flowOn
 */
@ExperimentalCoroutinesApi
fun <T> Flow<T>.flowOnMainImmediate(): Flow<T> = flow {
    flowOn(coroutineContext.dispatcherProvider.mainImmediate)
        .collect { emit(it) }
}

/**
 * Extracts the [dispatcherProvider] from the `coroutineContext` of the **collector** coroutine,
 * then uses its **unconfined** [CoroutineDispatcher] property to call `flowOn(theDispatcher)`,
 * and returns the result.
 *
 * @see flowOn
 */
@ExperimentalCoroutinesApi
fun <T> Flow<T>.flowOnUnconfined(): Flow<T> = flow {
    flowOn(coroutineContext.dispatcherProvider.unconfined)
        .collect { emit(it) }
}