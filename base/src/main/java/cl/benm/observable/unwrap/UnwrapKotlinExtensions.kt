package cl.benm.observable.unwrap

import cl.benm.observable.Observable
import java.util.concurrent.Executor

fun <T,R> Observable<T>.transformOrPropagate(transformation: (T) -> R, executor: Executor): Observable<R> {
    return transform(object : SimpleTransformation<T,R>() {
        override fun transformSuccess(`in`: T): R {
            return transformation(`in`)
        }
    }, executor)
}

fun <T,R> Observable<T>.transformAsyncOrPropagate(transformation: (T) -> Observable<R>, executor: Executor): Observable<R> {
    return transformAsync(object: SimpleAsyncTransformation<T,R>() {
        override fun transformSuccess(`in`: T): Observable<R> {
            return transformation(`in`)
        }
    }, executor)
}