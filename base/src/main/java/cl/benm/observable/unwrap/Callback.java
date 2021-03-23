package cl.benm.observable.unwrap;

public interface Callback<T> {

    public abstract void onSuccess(T value);
    public abstract void onFailure(Throwable throwable);

}
