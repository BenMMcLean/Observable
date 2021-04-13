package cl.benm.observable;

import androidx.lifecycle.LifecycleOwner;

import java.util.concurrent.Executor;

/**
 * A simple wrapper for single and multiple asynchronous value emitters
 * @param <T> The type of value emitted
 */
public interface Observable<T> {

    /**
     * Whether the Observable returns a single or multiple values
     * @return If the Observable returns a single or multiple values
     */
    EmissionType getEmissionType();

    /**
     * Observe the observable
     * @param observer The callback
     * @param executor The thread to execute the callback on
     */
    void observe(Observer<T> observer, Executor executor);

    /**
     * Observe the observable in a lifecycle aware fashion
     * @param observer The callback
     * @param executor The thread to execute the callback on
     * @param lifecycleOwner The lifecycle to attach the callback to
     */
    void observe(Observer<T> observer, LifecycleOwner lifecycleOwner, Executor executor);

    /**
     * Observe the observable for a single emission
     * @param observer The callback
     * @param executor The thread to execute the callback on
     */
    void observeOnce(Observer<T> observer, Executor executor);

    /**
     * Remove an observer
     * @param observer The observer to remove
     */
    void removeObserver(Observer<T> observer);

    /**
     * Remove all observers attached to a lifecycle
     * @param lifecycleOwner The lifecycle who's observers will be removed
     */
    void removeObservers(LifecycleOwner lifecycleOwner);

    /**
     * Transform the emissions of an Observable
     * @param transformation The transformation to apply
     * @param executor The thread to execute the transformation on
     * @param <R> The output type of the transformation
     * @return An observer emitting the transformed output
     */
    <R> Observable<R> transform(Transformation<T,R> transformation, Executor executor);

    /**
     * Transform the emissions of an Observable into another Observable
     * @param transformation The transformation to apply
     * @param executor The thread to execute the transformation on
     * @param <R> The output type of the transformation
     * @return An observer emitting the output of Observable created as the result of the transformation
     */
    <R> Observable<R> transformAsync(AsyncTransformation<T,R> transformation, Executor executor);


    <E extends Throwable> Observable<T> catching(Class<E> exception, Transformation<E, T> catching, Executor executor);

    <E extends Throwable> Observable<T> catchingAsync(Class<E> exception, AsyncTransformation<E, T> catchingAsync, Executor executor);

    /**
     * Return if the first element has been emitted
     * @return If the first element has been emitted
     */
    boolean hasEmittedFirst();

    /**
     * Return the last emitted value
     * @return The last emitted value
     */
    ExceptionOrValue<T> value();

}
