package cl.benm.observable.concrete;

import cl.benm.observable.EmissionType;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;
import cl.benm.observable.Transformation;

public class TransformObservable<T, R> extends ValueObservable<R> {

    private final Observable<T> delegate;
    private Transformation<T, R> transformation;

    public TransformObservable(Observable<T> delegate, Transformation<T, R> transformation) {
        this.delegate = delegate;
        this.transformation = transformation;
    }

    private Observer<T> observer = value -> emit(transformation.transform(value));

    @Override
    protected void onActive() {
        super.onActive();
        delegate.observe(observer);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        delegate.removeObserver(observer);
    }

    @Override
    public EmissionType getEmissionType() {
        return EmissionType.MULTIPLE;
    }
}
