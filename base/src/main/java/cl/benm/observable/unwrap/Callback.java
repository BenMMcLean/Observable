package cl.benm.observable.unwrap;

public interface Callback<T> {

    /**
     * On a success
     * @param value The given value
     */
    void onSuccess(T value);

    /**
     * On a failure
     * @param throwable The given exception
     */
    void onFailure(Throwable throwable);

}
