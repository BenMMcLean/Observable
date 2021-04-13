package cl.benm.observable;

/**
 * Callback for an Observable
 * @param <T> The type of the value of the callback
 */
public interface Observer<T> {

    /**
     * When the Observable emits a value
     * @param value The value emitted
     */
    void onChanged(T value);

    void onException(Throwable exception);

}
