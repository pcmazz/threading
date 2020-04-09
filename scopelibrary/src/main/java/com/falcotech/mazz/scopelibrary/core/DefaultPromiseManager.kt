package com.falcotech.mazz.scopelibrary.core

import androidx.annotation.CallSuper
import com.falcotech.mazz.scopelibrary.factory.DispatcherProvider
import com.falcotech.mazz.scopelibrary.factory.dispatcherProvider
import com.falcotech.mazz.scopelibrary.factory.providerName
import com.falcotech.mazz.scopelibrary.promise.Promise
import com.falcotech.mazz.scopelibrary.promise.then
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import kotlin.coroutines.coroutineContext

open class DefaultPromiseManager : PromiseManager{

    private val coldList: ArrayList<Promise<*>> = arrayListOf()
    private var debug = { _: String-> }

    @CallSuper
    @Synchronized
    override fun <T> controlAsync(promise: Promise<T>): Promise<T> {
        coldList.add(promise)
        promise.invokeOnCompletion {
            debug.invoke("DefaultPromiseManager : controlAsync : invokeOnCompletion : promise providerName =  ${promise.providerName}, throwable = $it")
            coldList.remove(promise)
        }
        return promise
    }

    @CallSuper
    @Synchronized
    override suspend fun <T> controlAsyncAwait(promise: Promise<T>): T {
        return controlAsync(promise).await().let {
            debug.invoke("DefaultPromiseManager : controlAsyncAwait : rez = $it")
            debug.invoke("DefaultPromiseManager : controlAsyncAwait : then promise.providerName = ${promise.providerName}")
            if(promise.providerName != "DefaultDispatcherProvider"){
                return@let "BUTT STUFF" as T
                //controlCancelAllPromises()
            }
            return@let it
        }
       /* return with(controlAsync(promise).await()){
            coroutineContext.fold(this){promVal, cce ->
                val unconfinedCompare = promise.dispatcherProvider.unconfined
                debug.invoke("DefaultPromiseManager : controlAsyncAwait : promise = $promise")
                debug.invoke("DefaultPromiseManager : controlAsyncAwait : unconfinedCompare = $unconfinedCompare")
                debug.invoke("DefaultPromiseManager : controlAsyncAwait : promise promVal = $promVal")
                debug.invoke("DefaultPromiseManager : controlAsyncAwait : promise cce = $cce")
                if(cce == unconfinedCompare){
                    debug.invoke("DefaultPromiseManager : controlAsyncAwait : CANCELING")
                    controlCancelAllPromises()
                }
                this
            }
            this
        }*/
        /*return with(controlAsync(promise).await()){
            promise.dispatcherProvider.takeIf {dispatchProvider ->
                debug.invoke("DefaultPromiseManager : controlAsyncAwait : promise dispatchProvider = $dispatchProvider")
                debug.invoke("DefaultPromiseManager : controlAsyncAwait : promise dispatchProvider.key = ${dispatchProvider.key}")
                val dispatcherUndefined = dispatchProvider.unconfined
                debug.invoke("DefaultPromiseManager : controlAsyncAwait : promise dispatcherUndefined = $dispatcherUndefined")
                debug.invoke("DefaultPromiseManager : controlAsyncAwait : promise dispatcherUndefined.key = ${dispatcherUndefined.key}")
                val testKey = dispatchProvider[dispatcherUndefined.key]
                debug.invoke("DefaultPromiseManager : controlAsyncAwait : promise testKey = $testKey")
                testKey != null
            }.let {
                debug.invoke("DefaultPromiseManager : controlAsyncAwait : takeIf = $it")
                if(it != null){
                    debug.invoke("DefaultPromiseManager : controlAsyncAwait : CANCELING")
                    controlCancelAllPromises()
                }
            }
            this
        }*/
        /*val key = promise.key
        val dispatchProviderUncon = dispatcherProvider().unconfined
        val promDispatchPro = promise.dispatcherProvider
        val promDispacther = promDispatchPro.unconfined
        debug.invoke("DefaultPromiseManager : controlAsyncAwait : promise KEY = $key, dispatchProviderUncon = $dispatchProviderUncon, promDispatchPro = $promDispatchPro, promDispacther = $promDispacther")
        if(promDispacther == dispatchProviderUncon){
            debug.invoke("DefaultPromiseManager : controlAsyncAwait : CANCELING")
            controlCancelAllPromises()
        }
        return controlAsync(promise).await()*/
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