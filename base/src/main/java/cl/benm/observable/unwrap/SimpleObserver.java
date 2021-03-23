package cl.benm.observable.unwrap;

import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observer;

public abstract class SimpleObserver<T> implements Observer<T>, Callback<T> {

    @Override
    public void onChanged(ExceptionOrValue<T> value) {
        if (value instanceof ExceptionOrValue.Value) {
            onSuccess(((ExceptionOrValue.Value<T>) value).getValue());
        } else if (value instanceof ExceptionOrValue.Exception) {
            onFailure(((ExceptionOrValue.Exception<T>) value).getThrowable());
        } else {
            onFailure(new IllegalArgumentException("Value provided to onChanged(ExceptionOrValue) must be an ExceptionOrValue.Value or ExceptionOrValue.Exception!"));
        }
    }

}
