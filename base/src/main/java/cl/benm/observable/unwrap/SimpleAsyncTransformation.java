package cl.benm.observable.unwrap;

import cl.benm.observable.AsyncTransformation;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.Transformation;
import cl.benm.observable.concrete.SingleValueObservable;
import cl.benm.observable.concrete.ValueObservable;

/**
 * Unwraps the value provided to an AsyncTransformation and passes it to one of two callbacks
 * based on it's state
 * @param <T> The input of the transformation
 * @param <R> The type of the returned observable
 */
public abstract class SimpleAsyncTransformation<T,R> implements AsyncTransformation<T,R> {

    @Override
    public Observable<R> transformAsync(ExceptionOrValue<T> in) {
        if (in instanceof ExceptionOrValue.Value) {
            return transformSuccess(((ExceptionOrValue.Value<T>) in).getValue());
        } else if (in instanceof ExceptionOrValue.Exception) {
            return transformFailure(((ExceptionOrValue.Exception<T>) in).getThrowable());
        } else {
            return transformFailure(new IllegalArgumentException("Value provided to transform(ExceptionOrValue) must be an ExceptionOrValue.Value or ExceptionOrValue.Exception!"));
        }
    }

    /**
     * Called if a value was given
     * @param in The value
     * @return An observable
     */
    protected abstract Observable<R> transformSuccess(T in);

    /**
     * Called if an exception was given, by default propagates the exception down the chain
     * @param in The exception
     * @return An observable
     */
    protected Observable<R> transformFailure(Throwable in) {
        return new SingleValueObservable<>(new ExceptionOrValue.Exception<>(in));
    }

}
