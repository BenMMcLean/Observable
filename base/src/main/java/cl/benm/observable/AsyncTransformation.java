package cl.benm.observable;

/**
 * An asynchronous transformation
 * @param <T> The input of the transformation
 * @param <R> The type of the returned observable
 */
public interface AsyncTransformation<T,R> {

    /**
     * Transform the given value and return an Observable
     * @param in The given value
     * @return An Observable
     */
    Observable<R> transformAsync(ExceptionOrValue<T> in);

}
