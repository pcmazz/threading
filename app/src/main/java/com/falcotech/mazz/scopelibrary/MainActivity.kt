package com.falcotech.mazz.scopelibrary

import android.annotation.*
import android.os.*
import androidx.activity.*
import androidx.appcompat.app.*
import androidx.lifecycle.*
import com.falcotech.mazz.scopelibrary.core.DefaultPromiseManager
import com.falcotech.mazz.scopelibrary.factory.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.*

class MainActivity : AppCompatActivity() {

    val scope = mainCoroutineScope()


    private val factory = viewModelFactory {
        MainViewModel(DefaultPromiseManager(), SomeRepository(iOCoroutineScope()))
    }

    val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, factory).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.plant(Timber.DebugTree())

        // share the same Job as the `scope` property above, but use the IO dispatcher as default
        val ioDefaultScope = scope + Dispatchers.IO

        viewModel.messageFlow
            .onEach { Timber.v("I'm using the default dispatcher!") }
            // extract the default dispatcher from the CoroutineScope and apply it upstream
            .flowOn(scope.defaultDispatcher)
            .onEach { message ->
                Timber.v("I'm using the main dispatcher!")
                tvMessage.text = message
            }
            .onCompletion { tvMessage.text = "All done!" }
            // the .flowOn____() operator pulls the desired dispatcher out of the CoroutineScope
            // and applies it.  So in this case .flowOnMain() is pulling
            // the dispatcher assigned to "main" out of `ioDefaultScope`
            // and dispatching upstream execution
            .flowOnMain()
            .onEach { Timber.v("I'm using the IO dispatcher!") }
            // the default dispatcher in this scope is now Dispatchers.IO
            .launchIn(ioDefaultScope)
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified VM : ViewModel> viewModelFactory(crossinline f: () -> VM):
        ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(aClass: Class<T>): T = f() as T
    }
