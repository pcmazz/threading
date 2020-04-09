package com.falcotech.mazz.scopelibrary

import com.falcotech.mazz.scopelibrary.core.BaseViewModel
import com.falcotech.mazz.scopelibrary.core.PromiseManager
import androidx.lifecycle.viewModelScope
import com.falcotech.mazz.scopelibrary.factory.asyncDefault
import com.falcotech.mazz.scopelibrary.factory.defaultDispatcher
import com.falcotech.mazz.scopelibrary.promise.Promise
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber


class MainViewModel(promiseManager: PromiseManager, val repository: SomeRepository): BaseViewModel(promiseManager) {

    init {
        controlSetDebug {
            Timber.d("MainViewModel : init : controlSetDebug = %s", it)
        }
    }

    val repoProm = controlAsync(repository.getSomethingExpensiveUnstructured())

    val repoWrapperProm: Promise<String> by lazy {
        CompletableDeferred<String>().also {completable ->
            viewModelScope.launch {
                val repoResult = repository.getSomethingExpensive()
                completable.complete(repoResult)
            }
        }
    }

    val messageFlow = flow{
        emit("Get ready for some coroutine stuff...")
        emit(repoProm.await())
        emit(controlAsyncAwait(repoWrapperProm))
        delay(5000)
    }.flowOn(viewModelScope.defaultDispatcher)


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}