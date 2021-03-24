package cl.benm.observable.concrete;

import java.util.concurrent.Executor;

import cl.benm.observable.AsyncTransformation;
import cl.benm.observable.EmissionType;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;

public class AsyncTransformObservable<T,R> extends ValueObservable<R> {

    private final Observable<T> delegate;
    private AsyncTransformation<T,R> transformation;
    private Observable<R> transformationDelegate;
    private final Executor executor;

    public AsyncTransformObservable(Observable<T> delegate, AsyncTransformation<T, R> transformation, Executor executor) {
        this.delegate = delegate;
        this.transformation = transformation;
        this.executor = executor;
    }

    private final Observer<R> transformationObserver = this::emit;
    private final Observer<T> observer = value -> {
        if (transformationDelegate != null) {
            transformationDelegate.removeObserver(transformationObserver);
        }
        transformationDelegate = transformation.transformAsync(value);
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

    @Override
    public EmissionType getEmissionType() {
        return EmissionType.MULTIPLE;
    }
}
