package cl.benm.observable.livedata;

import androidx.lifecycle.LiveData;
import cl.benm.observable.EmissionType;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.concrete.ValueObservable;

public class LiveDataObservable<T> extends ValueObservable<T> {

    private final LiveData<T> delegate;

    public LiveDataObservable(LiveData<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public EmissionType getEmissionType() {
        return EmissionType.MULTIPLE;
    }

    private androidx.lifecycle.Observer<T> observer = (v) -> {
        emit(new ExceptionOrValue.Value<>(v));
    };

    @Override
    protected void onActive() {
        super.onActive();
        delegate.observeForever(observer);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        delegate.removeObserver(observer);
    }
}
