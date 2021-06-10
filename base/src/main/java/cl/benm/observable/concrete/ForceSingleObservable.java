package cl.benm.observable.concrete;

import java.util.concurrent.Executor;

import cl.benm.observable.EmissionType;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;
import cl.benm.observable.Transformation;
import cl.benm.observable.base.MediatedObservable;
import cl.benm.observable.base.ValueObservable;

public class ForceSingleObservable<T> extends MediatedObservable<T, T> {

    public ForceSingleObservable(Observable<T> delegate, Executor executor) {
        super(delegate, executor);

        observer = new Observer<T>() {
            @Override
            public void onChanged(T value) {
                emit(new ExceptionOrValue.Value<>(value));
                delegate.removeObserver(this);
            }

            @Override
            public void onException(Throwable exception) {
                emit(new ExceptionOrValue.Exception<>(exception));
            }
        };
    }

    @Override
    protected void onActive() {
        if (!emittedFirst) {
            super.onActive();
        }
    }

    @Override
    public EmissionType getEmissionType() {
        return EmissionType.SINGLE;
    }

}
