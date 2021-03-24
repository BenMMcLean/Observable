package cl.benm.observable;

/**
 * An transformation
 * @param <T> The input of the transformation
 * @param <R> The returned type
 */
public interface Transformation<T,R> {

    /**
     * Transform the given value and return a value
     * @param in The given value
     * @return An transformed value
     */
    ExceptionOrValue<R> transform(ExceptionOrValue<T> in);

}
