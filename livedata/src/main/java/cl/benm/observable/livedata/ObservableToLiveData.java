package cl.benm.observable.livedata;

import androidx.lifecycle.LiveData;

import java.util.concurrent.Executor;

import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;

/**
 * Adapt an Observable to a LiveData
 */
public class ObservableToLiveData {

    /**
     * Adapt an Observable to a LiveData
     * @param observable The Observable to wrap
     * @param executor The thread to execute observation on
     * @param <T> The type of the Observable
     * @return A LiveData representing the Observable
     */
    public static <T> LiveData<ExceptionOrValue<T>> toLiveData(Observable<T> observable, Executor executor) {
        return new ObservableLiveDataAdapter<>(observable, executor);
    }

}
