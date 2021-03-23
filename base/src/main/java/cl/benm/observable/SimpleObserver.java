package cl.benm.observable;

public abstract class SimpleObserver<T> implements Observer<T> {

    @Override
    public void onChanged(ExceptionOrValue<T> value) {
        if (value instanceof ExceptionOrValue.Value) {
            onSuccess(((ExceptionOrValue.Value<T>) value).value);
        } else if (value instanceof ExceptionOrValue.Exception) {
            onFailure(((ExceptionOrValue.Exception<T>) value).throwable);
        } else {
            onFailure(new IllegalArgumentException("Value provided to onChanged(ExceptionOrValue) must be an ExceptionOrValue.Value or ExceptionOrValue.Exception!"));
        }
    }

    public abstract void onSuccess(T value);
    public abstract void onFailure(Throwable throwable);

}
