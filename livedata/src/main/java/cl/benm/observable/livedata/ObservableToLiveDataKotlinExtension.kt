package cl.benm.observable.livedata

import androidx.lifecycle.LiveData
import cl.benm.observable.ExceptionOrValue
import cl.benm.observable.Observable

fun <T> Observable<T>.toLiveData(): LiveData<ExceptionOrValue<T>> {
    return ObservableToLiveData.toLiveData(this)
}