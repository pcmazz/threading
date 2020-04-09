package com.falcotech.mazz.scopelibrary.promise

import com.falcotech.mazz.scopelibrary.factory.asyncDefault
import com.falcotech.mazz.scopelibrary.factory.defaultCoroutineScope
import com.falcotech.mazz.scopelibrary.factory.dispatcherProvider
import kotlinx.coroutines.*

typealias Promise<T> = Deferred<T>

fun <T, Y> Promise<T>.then(handler: ((T) -> Y)?, promHandler: ((T) -> Promise<Y>)? = null): Promise<Y> = defaultCoroutineScope()
    .asyncDefault(this.dispatcherProvider, CoroutineStart.LAZY) {
        val res = this@then.await()
        if(handler != null){
            handler.invoke(res)
        }else{
            promHandler!!.invoke(res).await()
        }
    }

fun Job?.cancelIfActive() {
    if (this?.isActive == true) {
        cancel()
    }
}