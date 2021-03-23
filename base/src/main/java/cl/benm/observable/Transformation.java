package cl.benm.observable;

public interface Transformation<T,R> {

    ExceptionOrValue<R> transform(ExceptionOrValue<T> in);

}
