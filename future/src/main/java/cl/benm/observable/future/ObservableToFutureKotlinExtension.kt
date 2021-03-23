package cl.benm.observable.future

import cl.benm.observable.ExceptionOrValue
import cl.benm.observable.Observable
import com.google.common.util.concurrent.ListenableFuture

fun <T> Observable<T>.toFuture(): ListenableFuture<T> {
    return ObservableToFuture.toFuture(this)
}