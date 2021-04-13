package cl.benm.observable.livedata.to;

import androidx.lifecycle.LiveData;

import cl.benm.observable.EmissionType;
import cl.benm.observable.base.ValueObservable;

public class AbstractLiveDataObservable<T,R> extends ValueObservable<T> {

    private final LiveData<R> delegate;

    /**
     * Instantiate the Observable
     * @param delegate The internal LiveData
     */
    public AbstractLiveDataObservable(LiveData<R> delegate) {
        this.delegate = delegate;
    }

    @Override
    public EmissionType getEmissionType() {
        return EmissionType.MULTIPLE;
    }

    protected androidx.lifecycle.Observer<R> observer;

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
