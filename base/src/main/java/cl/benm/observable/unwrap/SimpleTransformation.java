package cl.benm.observable.unwrap;

import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Transformation;

/**
 * Unwraps the value provided to an Transformation and passes it to one of two callbacks
 * based on it's state
 * @param <T> The input of the transformation
 * @param <R> The type of the returned observable
 */
public abstract class SimpleTransformation<T,R> implements Transformation<T,R> {

    @Override
    public ExceptionOrValue<R> transform(ExceptionOrValue<T> in) {
        if (in instanceof ExceptionOrValue.Value) {
            try {
                return new ExceptionOrValue.Value<R>(transformSuccess(((ExceptionOrValue.Value<T>) in).getValue()));
            } catch (Throwable e) {
                return new ExceptionOrValue.Exception<R>(e);
            }
        } else if (in instanceof ExceptionOrValue.Exception) {
            return transformFailure(((ExceptionOrValue.Exception<T>) in).getThrowable());
        } else {
            return transformFailure(new IllegalArgumentException("Value provided to transform(ExceptionOrValue) must be an ExceptionOrValue.Value or ExceptionOrValue.Exception!"));
        }
    }

    /**
     * Called if a value was given
     * @param in The value
     * @return A transformed value
     * @throws Throwable Any thrown exceptions will be caught, wrapped in an ExceptionOrValue.Exception, and passed down the chain
     */
    protected abstract R transformSuccess(T in) throws Throwable;

    /**
     * Called if an exception was given, by default propagates the exception down the chain
     * @param in The exception
     * @return A transformed value
     */
    protected ExceptionOrValue<R> transformFailure(Throwable in) {
        return new ExceptionOrValue.Exception<>(in);
    }

}
