package cl.benm.observable.concrete;

import cl.benm.observable.AsyncTransformation;
import cl.benm.observable.EmissionType;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;

public class AsyncTransformObservable<T,R> extends ValueObservable<R> {

    private final Observable<T> delegate;
    private AsyncTransformation<T,R> transformation;
    private Observable<R> transformationDelegate;

    public AsyncTransformObservable(Observable<T> delegate, AsyncTransformation<T, R> transformation) {
        this.delegate = delegate;
        this.transformation = transformation;
    }

    private Observer<R> transformationObserver = this::emit;
    private Observer<T> observer = value -> {
        if (transformationDelegate != null) {
            transformationDelegate.removeObserver(transformationObserver);
        }
        transformationDelegate = transformation.transformAsync(value);
    };

    @Override
    protected void onActive() {
        super.onActive();
        if (transformationDelegate != null) transformationDelegate.observe(transformationObserver);
        delegate.observe(observer);
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
