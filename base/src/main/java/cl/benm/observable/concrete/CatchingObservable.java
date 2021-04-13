package cl.benm.observable.concrete;

import java.util.concurrent.Executor;

import cl.benm.observable.EmissionType;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;
import cl.benm.observable.Transformation;
import cl.benm.observable.base.MediatedObservable;
import cl.benm.observable.base.ValueObservable;

/**
 * Transforms and emits the exception emissions of a given Observable
 * @param <T> The input/output type
 * @param <E> The exception to catch
 */
public class CatchingObservable<T,E extends Throwable> extends MediatedObservable<T,T> {

    private final Transformation<E, T> transformation;
    private final Class<E> exception;

    /**
     * Instantiate the observable
     * @param delegate The Observable this Observable will be chained to
     * @param transformation The transformation to apply
     * @param executor The thread to execute the transformation on
     * @param exception The type of exception to catch
     */
    public CatchingObservable(Observable<T> delegate, Class<E> exception, Transformation<E, T> transformation, Executor executor) {
        super(delegate, executor);
        this.exception = exception;
        this.transformation = transformation;

        observer = new Observer<T>() {
            @Override
            public void onChanged(T value) {
                emit(new ExceptionOrValue.Value<>(value));
            }

            @Override
            public void onException(Throwable exception1) {
                doTransform(exception1);
            }
        };
    }

    private void doTransform(Throwable throwable) {
        try {
            if (throwable.getClass() == exception) {
                emit(new ExceptionOrValue.Value<>(transformation.transform((E) throwable)));
            } else {
                emit(new ExceptionOrValue.Exception<>(throwable));
            }
        } catch (Throwable e) {
            emit(new ExceptionOrValue.Exception<>(throwable));
        }
    }

}
