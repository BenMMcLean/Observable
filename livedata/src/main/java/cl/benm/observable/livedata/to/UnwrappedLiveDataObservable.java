package cl.benm.observable.livedata.to;

import androidx.lifecycle.LiveData;
import cl.benm.observable.ExceptionOrValue;

public class UnwrappedLiveDataObservable<T> extends AbstractLiveDataObservable<T, ExceptionOrValue<T>> {

    public UnwrappedLiveDataObservable(LiveData<ExceptionOrValue<T>> delegate) {
        super(delegate);
        super.observer = observer;
    }

    protected final androidx.lifecycle.Observer<ExceptionOrValue<T>> observer = this::emit;

}
