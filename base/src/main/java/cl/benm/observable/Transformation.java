package cl.benm.observable;

public interface Transformation<T,R> {

    R transform(T in);

}
