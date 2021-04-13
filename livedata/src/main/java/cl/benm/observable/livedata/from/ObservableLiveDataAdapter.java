package cl.benm.observable.livedata.from;

import androidx.lifecycle.LiveData;

import java.util.concurrent.Executor;

import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;

/**
 * Adapt an Observable to a LiveData
 * @param <T> The type of the Observable
 */
public class ObservableLiveDataAdapter<T> extends LiveData<ExceptionOrValue<T>> {

    private final Observable<T> delegate;
    private final Executor executor;

    /**
     * Instantiate the LiveData
     * @param delegate The Observable to wrap
     * @param executor The thread to execute observation on
     */
    public ObservableLiveDataAdapter(Observable<T> delegate, Executor executor) {
        this.delegate = delegate;
        this.executor = executor;
    }

    private final Observer<T> observer = new Observer<T>() {
        @Override
        public void onChanged(T value) {
            setValue(new ExceptionOrValue.Value<>(value));
        }

        @Override
        public void onException(Throwable exception) {
            setValue(new ExceptionOrValue.Exception<>(exception));
        }
    };

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
