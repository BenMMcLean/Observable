package cl.benm.observable;

public interface Observer<T> {

    void onChanged(ExceptionOrValue<T> value);

}
