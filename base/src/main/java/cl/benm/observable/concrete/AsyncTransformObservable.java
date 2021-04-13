package cl.benm.observable.concrete;

import java.util.concurrent.Executor;

import cl.benm.observable.AsyncTransformation;
import cl.benm.observable.EmissionType;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;
import cl.benm.observable.base.ValueObservable;

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
public class AsyncTransformObservable<T,R> extends ValueObservable<R> {

    private final Observable<T> delegate;
    private AsyncTransformation<T,R> transformation;
    private Observable<R> transformationDelegate;
    private Executor executor;

    /**
     * Instantiate the observable
     * @param delegate The Observable this Observable will be chained to
     * @param transformation The transformation to apply
     * @param executor The thread to execute the transformation on
     */
    public AsyncTransformObservable(Observable<T> delegate, AsyncTransformation<T, R> transformation, Executor executor) {
        this.delegate = delegate;
        this.transformation = transformation;
        this.executor = executor;
    }

    private final Observer<R> transformationObserver = new Observer<R>() {
        @Override
        public void onChanged(R value) {
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
            if (transformationDelegate != null) {
                transformationDelegate.removeObserver(transformationObserver);
            }
            try {
                transformationDelegate = transformation.transformAsync(value);
                if (active) {
                    transformationDelegate.observe(transformationObserver, executor);
                }
            } catch (Throwable t) {
                emit(new ExceptionOrValue.Exception<>(t));
            }
        }

        @Override
        public void onException(Throwable exception) {
            emit(new ExceptionOrValue.Exception<>(exception));
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
