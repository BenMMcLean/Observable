package cl.benm.observable.future

import cl.benm.observable.ExceptionOrValue
import cl.benm.observable.Observable
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executor

fun <T> Observable<T>.toFuture(executor: Executor): ListenableFuture<T> {
    return ObservableToFuture.toFuture(this, executor)
}