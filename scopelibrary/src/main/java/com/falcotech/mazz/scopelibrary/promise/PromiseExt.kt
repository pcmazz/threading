package com.falcotech.mazz.scopelibrary.promise

import android.util.TypedValue
import com.falcotech.mazz.scopelibrary.factory.asyncDefault
import com.falcotech.mazz.scopelibrary.factory.defaultCoroutineScope
import com.falcotech.mazz.scopelibrary.factory.dispatcherProvider
import kotlinx.coroutines.*
import timber.log.Timber

typealias Promise<T> = Deferred<T>

/*fun <T, Y> Promise<T>.then(handler: ((T) -> Y)?, promHandler: ((T) -> Promise<Y>)? = null): Promise<Y> = defaultCoroutineScope()
    .asyncDefault(this.dispatcherProvider, CoroutineStart.LAZY) {
        val res = this@then.await()
        if(handler != null){
            handler.invoke(res)
        }else{
            promHandler!!.invoke(res).await()
        }
    }*/


fun <T, Y> Promise<T>.thenAsync(handler: ((T) -> Promise<Y>)): Promise<Y> = defaultCoroutineScope()
    .asyncDefault(this.dispatcherProvider, CoroutineStart.LAZY) {
        val res = this@thenAsync.await()
        handler.invoke(res).await()
    }


fun Job?.cancelIfActive() {
    if (this?.isActive == true) {
        cancel()
    }
}

fun <T> bgPromise(block: suspend CoroutineScope.() -> T): Promise<T> = defaultCoroutineScope()
    .asyncDefault(dispatcherProvider().default, CoroutineStart.LAZY){
        block.invoke(this)
    }

inline fun <T, reified Y: Any> Promise<T>.then(
    crossinline handler: (T) -> Y
): Promise<Y> = defaultCoroutineScope()
    .asyncDefault(this.dispatcherProvider, CoroutineStart.LAZY) {
       this@then.await()!!.let {initRez ->
           return@asyncDefault handler.invoke(initRez).let {possibleProm ->
               if(possibleProm::class.java == Promise::class.java){
                   (possibleProm as Promise<Y>).await()
               }else{
                   (possibleProm as Y)
               }
           }
       }
    }

/*
inline fun <T, reified Y: Any> Promise<T>.then(
    crossinline handler: (T) -> Y
): Promise<Y> = defaultCoroutineScope()
    .asyncDefault(this.dispatcherProvider, CoroutineStart.LAZY) {
        val initRez = this@then.await()!!
        return@asyncDefault handler.invoke(initRez).let {possibleProm ->
            if(possibleProm::class.java == Promise::class.java){
                (possibleProm as Promise<Y>).await()
            }else{
                (possibleProm as Y)
            }
        }
    }
*/

/*inline fun <T, reified Y: Any> Promise<T>.then(
    crossinline handler: (T) -> Y
): Promise<Y> = defaultCoroutineScope().asyncDefault(this.dispatcherProvider, CoroutineStart.LAZY) {
    this@then.await().let{initRez ->
        handler.invoke(initRez)
    }
        .let {y ->
            if(y::class.java == Promise::class.java){
                return@let (y as Promise<Y>).await()  as Y
            }else{
                return@let y as Y as Y
            }
        }
        .let {returnRez ->
            Timber.d("PromiseExt : .then : final let = %s", returnRez)
            returnRez as Y
        }
}*/

/*inline fun <T, reified Y> Promise<T>.then(
    crossinline handler: (T) -> Y
): Promise<Y> = defaultCoroutineScope()
    .asyncDefault(this.dispatcherProvider, CoroutineStart.LAZY) {
        handler.invoke(this@then.await()).let {blockRez ->
            when(Y::class){
                Promise::class -> {
                    (blockRez as Promise<Y>).await()
                }
                else -> {
                    return@let (blockRez as Y)
                }
            }
        }
    }*/


inline fun <T, reified Y> Promise<T>.toPromise(crossinline handler: (T) -> Any): Promise<Y> = defaultCoroutineScope()
    .asyncDefault(this.dispatcherProvider, CoroutineStart.LAZY) {
        handler.invoke(this@toPromise.await()).let {blockRez ->
            if(blockRez is Promise<*>){
                blockRez.await() as Y
            }else{
                blockRez as Y
            }
        }
    }
