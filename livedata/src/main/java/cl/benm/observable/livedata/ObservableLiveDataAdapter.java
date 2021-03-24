package cl.benm.observable.livedata;

import androidx.lifecycle.LiveData;

import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;

public class ObservableLiveDataAdapter<T> extends LiveData<ExceptionOrValue<T>> {

    private final Observable<T> delegate;

    public ObservableLiveDataAdapter(Observable<T> delegate) {
        this.delegate = delegate;
    }

    private Observer<T> observer = this::setValue;

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

}
