package cl.benm.observable;

public interface AsyncTransformation<T,R> {

    Observable<R> transformAsync(ExceptionOrValue<T> in);

}
