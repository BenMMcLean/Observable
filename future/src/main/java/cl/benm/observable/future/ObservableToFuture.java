package cl.benm.observable.future;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;

public class ObservableToFuture {

    public static <T> ListenableFuture<T> toFuture(Observable<T> observable) {
        SettableFuture<T> future = SettableFuture.create();

        observable.observeOnce(value -> {
            if (value instanceof ExceptionOrValue.Value) {
                future.set(((ExceptionOrValue.Value<T>) value).getValue());
            } else if (value instanceof ExceptionOrValue.Exception) {
                future.setException(((ExceptionOrValue.Exception<T>) value).getThrowable());
            }
        });

        return future;
    }

}
