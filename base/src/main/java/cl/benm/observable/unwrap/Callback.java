package cl.benm.observable.unwrap;

public interface Callback<T> {

    void onSuccess(T value);
    void onFailure(Throwable throwable);

}
