package cl.benm.observable.unwrap;

import cl.benm.observable.AsyncTransformation;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.Transformation;
import cl.benm.observable.concrete.SingleValueObservable;
import cl.benm.observable.concrete.ValueObservable;

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

    protected abstract Observable<R> transformSuccess(T in);
    protected Observable<R> transformFailure(Throwable in) {
        return new SingleValueObservable<>(new ExceptionOrValue.Exception<>(in));
    }

}
