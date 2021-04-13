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
 * Transform the emissions of an Observable into another Observable.
 * This class essentially mediates between two Observables, the delegate
 * and the last Observable output by the transformation. Upon the emission
 * of a value by the delegate, the value is passed to the transformation.
 * The output of that transformation is stored and observed while this
 * Observable has active observers. Upon the emission of a value by the
 * transformation Observable, said value is emitted to the subscribers
 * of this Observable.
 * @param <T> The input type of the transformation
 * @param <R> The output type of the transformation
 */
public class AsyncTransformObservable<T,R> extends AsyncMediatedObservable<T, R> {

    /**
     * Instantiate the observable
     * @param delegate The Observable this Observable will be chained to
     * @param transformation The transformation to apply
     * @param executor The thread to execute the transformation on
     */
    public AsyncTransformObservable(Observable<T> delegate, AsyncTransformation<T, R> transformation, Executor executor) {
        super(delegate, executor);

        observer = new Observer<T>() {
            @Override
            public void onChanged(T value) {
                try {
                    emit(transformation.transformAsync(value));
                } catch (Throwable t) {
                    emit(Observables.immediateFailedObservable(t));
                }
            }

            @Override
            public void onException(Throwable exception) {
                emit(Observables.immediateFailedObservable(exception));
            }
        };
    }
}
