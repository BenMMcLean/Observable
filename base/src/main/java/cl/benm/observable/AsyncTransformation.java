package cl.benm.observable;

public interface AsyncTransformation<T,R> {

    Observable<R> transformAsync(T in);

}
