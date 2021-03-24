package cl.benm.observable.livedata

import androidx.lifecycle.LiveData
import cl.benm.observable.ExceptionOrValue
import cl.benm.observable.Observable
import java.util.concurrent.Executor

fun <T> Observable<T>.toLiveData(executor: Executor): LiveData<ExceptionOrValue<T>> {
    return ObservableToLiveData.toLiveData(this, executor)
}