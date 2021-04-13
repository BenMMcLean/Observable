package cl.benm.observable.concrete;

import java.util.concurrent.Executor;

import cl.benm.observable.AsyncTransformation;
import cl.benm.observable.EmissionType;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;

/**
 * Catch an exception passed down the Observable chain and transform it asynchronously
 * @param <T> The input/output type of the Observable
 * @param <E> The exception to catch
 * @see cl.benm.observable.concrete.AsyncTransformObservable
 */
public class AsyncCatchingObservable<T, E extends Throwable> extends ValueObservable<T> {

    private final Observable<T> delegate;
    private final AsyncTransformation<E,T> transformation;
    private Observable<T> transformationDelegate;
    private final Class<E> exception;
    private final Executor executor;

    /**
     * Instantiate the observable
     * @param delegate The Observable this Observable will be chained to
     * @param transformation The transformation to apply
     * @param executor The thread to execute the transformation on
     * @param exception The type of exception to catch
     */
    public AsyncCatchingObservable(Observable<T> delegate, Class<E> exception, AsyncTransformation<E, T> transformation, Executor executor) {
        this.delegate = delegate;
        this.transformation = transformation;
        this.exception = exception;
        this.executor = executor;
    }

    private final Observer<T> transformationObserver = new Observer<T>() {
        @Override
        public void onChanged(T value) {
            emit(new ExceptionOrValue.Value<>(value));
        }

        @Override
        public void onException(Throwable exception) {
            emit(new ExceptionOrValue.Exception<>(exception));
        }
    };
    private final Observer<T> observer = new Observer<T>() {
        @Override
        public void onChanged(T value) {
            emit(new ExceptionOrValue.Value<>(value));
        }

        @Override
        public void onException(Throwable ex) {
            if (transformationDelegate != null) {
                transformationDelegate.removeObserver(transformationObserver);
            }
            try {
                if (ex.getClass() == exception) {
                    transformationDelegate = transformation.transformAsync((E) ex);
                    if (active) {
                        transformationDelegate.observe(transformationObserver, executor);
                    }
                } else {
                    emit(new ExceptionOrValue.Exception<>(ex));
                }
            } catch (Throwable t) {
                emit(new ExceptionOrValue.Exception<>(t));
            }
        }
    };

    @Override
    protected void onActive() {
        super.onActive();
        if (transformationDelegate != null) transformationDelegate.observe(transformationObserver, executor);
        delegate.observe(observer, executor);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (transformationDelegate != null) transformationDelegate.removeObserver(transformationObserver);
        delegate.removeObserver(observer);
    }

    // We don't know the type of the returned Observable, so always return MULTIPLE for consistency
    @Override
    public EmissionType getEmissionType() {
        return EmissionType.MULTIPLE;
    }
}
