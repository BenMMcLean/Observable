package cl.benm.observable.livedata;

import androidx.lifecycle.LiveData;

import java.util.concurrent.Executor;

import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;

public class ObservableLiveDataAdapter<T> extends LiveData<ExceptionOrValue<T>> {

    private final Observable<T> delegate;
    private final Executor executor;

    public ObservableLiveDataAdapter(Observable<T> delegate, Executor executor) {
        this.delegate = delegate;
        this.executor = executor;
    }

    private Observer<T> observer = this::setValue;

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

}
