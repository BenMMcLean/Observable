package cl.benm.observable;

import androidx.lifecycle.LifecycleOwner;

import java.util.concurrent.Executor;

public interface Observable<T> {

    EmissionType getEmissionType();

    void observe(Observer<T> observer, Executor executor);
    void observe(Observer<T> observer, LifecycleOwner lifecycleOwner, Executor executor);
    void observeOnce(Observer<T> observer, Executor executor);

    void removeObserver(Observer<T> observer);
    void removeObservers(LifecycleOwner lifecycleOwner);

    <R> Observable<R> transform(Transformation<T,R> transformation, Executor executor);
    <R> Observable<R> transformAsync(AsyncTransformation<T,R> transformation, Executor executor);

}
