package cl.benm.observable.concrete;

import java.util.concurrent.Executor;

import cl.benm.observable.AsyncTransformation;
import cl.benm.observable.EmissionType;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;
import cl.benm.observable.base.AsyncMediatedObservable;
import cl.benm.observable.base.ValueObservable;
import cl.benm.observable.helpers.Observables;

/**
 * Catch an exception passed down the Observable chain and transform it asynchronously
 * @param <T> The input/output type of the Observable
 * @param <E> The exception to catch
 * @see cl.benm.observable.concrete.AsyncTransformObservable
 */
public class AsyncCatchingObservable<T, E extends Throwable> extends AsyncMediatedObservable<T,T> {

    /**
     * Instantiate the observable
     * @param delegate The Observable this Observable will be chained to
     * @param transformation The transformation to apply
     * @param executor The thread to execute the transformation on
     * @param exception The type of exception to catch
     */
    public AsyncCatchingObservable(Observable<T> delegate, Class<E> exception, AsyncTransformation<E, T> transformation, Executor executor) {
        super(delegate, executor);

        observer = new Observer<T>() {
            @Override
            public void onChanged(T value) {
                emit(Observables.immediateObservable(value));
            }

            @Override
            public void onException(Throwable ex) {
                try {
                    if (exception.isAssignableFrom(ex.getClass())) {
                        emit(transformation.transformAsync((E) ex));
                    } else {
                        emit(Observables.immediateFailedObservable(ex));
                    }
                } catch (Throwable t) {
                    emit(Observables.immediateFailedObservable(t));
                }
            }
        };
    }
}
