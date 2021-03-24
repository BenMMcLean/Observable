package cl.benm.observable.livedata

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import cl.benm.observable.ExceptionOrValue
import cl.benm.observable.Observable
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
            else -> throw IllegalArgumentException("Value provided to discardExceptions(Boolean) must be an ExceptionOrValue.Value or ExceptionOrValue.Exception!")
        }
    }
}