package cl.benm.observable.concrete;

import java.util.concurrent.Executor;

import cl.benm.observable.EmissionType;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;
import cl.benm.observable.Transformation;

/**
 * Transforms and emits the exception emissions of a given Observable
 * @param <T> The input/output type
 * @param <E> The exception to catch
 */
public class CatchingObservable<T,E extends Throwable> extends ValueObservable<T> {

    private final Observable<T> delegate;
    private final Executor executor;
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
        this.delegate = delegate;
        this.exception = exception;
        this.transformation = transformation;
        this.executor = executor;
    }

    private final Observer<T> observer = new Observer<T>() {
        @Override
        public void onChanged(T value) {
            emit(new ExceptionOrValue.Value<>(value));
        }

        @Override
        public void onException(Throwable exception) {
            doTransform(exception);
        }
    };

    private void doTransform(Throwable throwable) {
        try {
            if (throwable.getClass() == exception) {
                emit(new ExceptionOrValue.Value<T>(transformation.transform((E) throwable)));
            } else {
                emit(new ExceptionOrValue.Exception<>(throwable));
            }
        } catch (Throwable e) {
            emit(new ExceptionOrValue.Exception<>(throwable));
        }
    }

    @Override
    protected void onActive() {
        super.onActive();
        delegate.observe(observer, executor);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        delegate.removeObserver(observer);
    }

    @Override
    public EmissionType getEmissionType() {
        return delegate.getEmissionType();
    }
}
