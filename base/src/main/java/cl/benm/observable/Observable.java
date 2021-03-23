package cl.benm.observable;

import androidx.lifecycle.LifecycleOwner;

public interface Observable<T> {

    void observe(Observer<T> observer);
    void observe(Observer<T> observer, LifecycleOwner lifecycleOwner);
    void observeOnce(Observer<T> observer);

    void removeObserver(Observer<T> observer);
    void removeObservers(LifecycleOwner lifecycleOwner);

    <R> Observable<R> transform(Transformation<T,R> transformation);
    <R> Observable<R> transformAsync(AsyncTransformation<T,R> transformation);

}
