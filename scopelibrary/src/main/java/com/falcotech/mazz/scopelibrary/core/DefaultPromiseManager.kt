package com.falcotech.mazz.scopelibrary.core

import androidx.annotation.CallSuper
import com.falcotech.mazz.scopelibrary.promise.Promise
import timber.log.Timber

open class DefaultPromiseManager : PromiseManager{

    private val coldList: ArrayList<Promise<*>> = arrayListOf()
    private var debug = { _: String-> }

    @CallSuper
    @Synchronized
    override fun <T> controlAsync(promise: Promise<T>): Promise<T> {
        coldList.add(promise)
        promise.invokeOnCompletion {
            debug.invoke("DefaultPromiseManager : controlAsync : invokeOnCompletion : it = $it")
            coldList.remove(promise)
        }
        return promise
    }

    @CallSuper
    @Synchronized
    override suspend fun <T> controlAsyncAwait(promise: Promise<T>): T {
        return controlAsync(promise).await()
    }

    @CallSuper
    @Synchronized
    override fun controlCancelAllPromises() {
        if(coldList.isNotEmpty()){
            (coldList.lastIndex downTo 0).forEach {
                coldList[it].cancel()
            }.let {
                coldList.clear()
            }
        }
    }

    override fun controlSetDebug(block: (String) -> Unit) {
        debug = block
    }
}