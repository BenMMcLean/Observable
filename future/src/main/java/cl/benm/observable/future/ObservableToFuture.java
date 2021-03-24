package cl.benm.observable.future;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.util.concurrent.Executor;

import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;

/**
 * Convert an Observable to a ListenableFuture
 */
public class ObservableToFuture {

    /**
     * Convert an Observable to a ListenableFuture
     * @param observable The Observable to convert
     * @param executor The thread to execute conversion on
     * @param <T> The type of the Observable
     * @return A ListenableFuture representing the Observable
     */
    public static <T> ListenableFuture<T> toFuture(Observable<T> observable, Executor executor) {
        SettableFuture<T> future = SettableFuture.create();

        observable.observeOnce(value -> {
            if (value instanceof ExceptionOrValue.Value) {
                future.set(((ExceptionOrValue.Value<T>) value).getValue());
            } else if (value instanceof ExceptionOrValue.Exception) {
                future.setException(((ExceptionOrValue.Exception<T>) value).getThrowable());
            }
        }, executor);

        return future;
    }

}
