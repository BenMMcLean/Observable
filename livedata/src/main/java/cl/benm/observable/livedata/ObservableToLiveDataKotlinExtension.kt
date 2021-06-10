package cl.benm.observable.livedata

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import cl.benm.androidlogger.Logger
import cl.benm.observable.ExceptionOrValue
import cl.benm.observable.Observable
import cl.benm.observable.helpers.IllegalExceptionOrValueException
import cl.benm.observable.livedata.from.ObservableToLiveData
import cl.benm.observable.livedata.to.LiveDataObservable
import cl.benm.observable.livedata.to.UnwrappedLiveDataObservable
import java.util.concurrent.Executor

fun <T> Observable<T>.toLiveData(executor: Executor): LiveData<ExceptionOrValue<T>> {
    return ObservableToLiveData.toLiveData(this, executor)
}

fun <T> LiveData<ExceptionOrValue<T>>.discardExceptions(log: Boolean = true): LiveData<T> {
    return Transformations.map(this) {
        when(it) {
            is ExceptionOrValue.Exception -> {
                if (log) {
                    Log.e("LiveData", "${it.throwable.message}", it.throwable)
                }
                null
            }
            is ExceptionOrValue.Value -> it.value
            else -> throw IllegalExceptionOrValueException("discardExceptions(Boolean)")
        }
    }
}

fun <T> LiveData<ExceptionOrValue<T>>.discardExceptions(logger: Logger?): LiveData<T> {
    return Transformations.map(this) {
        when(it) {
            is ExceptionOrValue.Exception -> {
                logger?.let { logger ->
                    logger.e("LiveData", "${it.throwable.message}", it.throwable)
                }
                null
            }
            is ExceptionOrValue.Value -> it.value
            else -> throw IllegalExceptionOrValueException("discardExceptions(Boolean)")
        }
    }
}

fun <T> LiveData<T>.toObservable(): Observable<T> {
    return LiveDataObservable(this)
}

fun <T> LiveData<ExceptionOrValue<T>>.toUnwrappedObservable(): Observable<T> {
    return UnwrappedLiveDataObservable(this)
}