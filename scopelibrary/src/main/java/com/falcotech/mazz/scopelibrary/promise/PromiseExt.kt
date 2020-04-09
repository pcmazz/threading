package com.falcotech.mazz.scopelibrary.promise

import com.falcotech.mazz.scopelibrary.factory.asyncDefault
import com.falcotech.mazz.scopelibrary.factory.defaultCoroutineScope
import kotlinx.coroutines.*

typealias Promise<T> = Deferred<T>

fun <T, Y> Promise<T>.then(handler: ((T) -> Y)?, promHandler: ((T) -> Promise<Y>)?): Promise<Y> = defaultCoroutineScope()
    .asyncDefault {
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