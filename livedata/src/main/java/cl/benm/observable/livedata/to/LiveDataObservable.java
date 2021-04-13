package cl.benm.observable.livedata.to;

import androidx.lifecycle.LiveData;

import cl.benm.observable.ExceptionOrValue;

/**
 * Wrap a LiveData in an Observable
 * @param <T> The type of the Observable
 */
public class LiveDataObservable<T> extends AbstractLiveDataObservable<T,T> {

    public LiveDataObservable(LiveData<T> delegate) {
        super(delegate);
        super.observer = observer;
    }

    protected final androidx.lifecycle.Observer<T> observer = (v) -> emit(new ExceptionOrValue.Value<>(v));

}
