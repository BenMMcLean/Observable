package cl.benm.observable.livedata;

import androidx.lifecycle.LiveData;

import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;

public class ObservableToLiveData {

    public static <T> LiveData<ExceptionOrValue<T>> toLiveData(Observable<T> observable) {
        return new ObservableLiveDataAdapter<>(observable);
    }

}
