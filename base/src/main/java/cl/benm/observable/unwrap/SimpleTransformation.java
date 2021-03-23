package cl.benm.observable.unwrap;

import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Transformation;

public abstract class SimpleTransformation<T,R> implements Transformation<T,R> {

    @Override
    public ExceptionOrValue<R> transform(ExceptionOrValue<T> in) {
        if (in instanceof ExceptionOrValue.Value) {
            return transformSuccess(((ExceptionOrValue.Value<T>) in).getValue());
        } else if (in instanceof ExceptionOrValue.Exception) {
            return transformFailure(((ExceptionOrValue.Exception<T>) in).getThrowable());
        } else {
            return transformFailure(new IllegalArgumentException("Value provided to transform(ExceptionOrValue) must be an ExceptionOrValue.Value or ExceptionOrValue.Exception!"));
        }
    }

    protected abstract ExceptionOrValue<R> transformSuccess(T in);
    protected ExceptionOrValue<R> transformFailure(Throwable in) {
        return new ExceptionOrValue.Exception<R>(in);
    }

}
