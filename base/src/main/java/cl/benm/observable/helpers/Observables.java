package cl.benm.observable.helpers;

import java.util.List;
import java.util.concurrent.Executor;

import cl.benm.observable.Combiner;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.concrete.CombinedObservable;
import cl.benm.observable.concrete.ListObservable;
import cl.benm.observable.concrete.SingleValueObservable;
import cl.benm.observable.unwrap.SimpleCombiner;

/**
 * Helper function to create Observables
 */
public class Observables {

    private Observables() {}

    /**
     * Combines a list of Observable results based on a {@link Combiner}
     * @param observables The Observables to combine
     * @param combiner The combiner
     * @param executor The executor for the Observable
     * @param <T> The input type
     * @param <R> The output type
     * @return An Observable of type R
     */
    public static <T,R> Observable<R> whenAllComplete(List<Observable<T>> observables, Combiner<T,R> combiner, Executor executor) {
        return new CombinedObservable<>(observables, new SimpleCombiner<T, R>() {
            @Override
            public ExceptionOrValue<R> combineFiltered(List<T> in) {
                try {
                    return new ExceptionOrValue.Value<>(combiner.combine(in));
                } catch (Throwable t) {
                    return new ExceptionOrValue.Exception<>(t);
                }
            }
        }, executor);
    }

    /**
     * Returns an Observable that immediately emits a single value
     * @param value The value to emit
     * @param <T> The type of the Observable
     * @return An Observable that immediately emits a single value
     */
    public static <T> Observable<T> immediateObservable(T value) {
        return new SingleValueObservable<>(new ExceptionOrValue.Value<>(value));
    }

    /**
     * Returns an Observable that immediately emits a single exception
     * @param th The exception to emit
     * @param <T> The type of the Observable
     * @return An Observable that immediately emits a single exception
     */
    public static <T> Observable<T> immediateFailedObservable(Throwable th) {
        return new SingleValueObservable<>(new ExceptionOrValue.Exception<>(th));
    }

    /**
     * Aggregates a list of Observable into a list of their values. If an
     * Observable emits an exception, the exception will be passed down
     * the chain rather than the list of values.
     * @param observables The observables to aggregate
     * @param executor The executor to execute aggregation with
     * @param <T> The type of the Observables
     * @return An Observable aggregating it's delegated values
     */
    public static <T> Observable<List<T>> allAsList(List<Observable<T>> observables, Executor executor) {
        return new ListObservable<>(observables, executor);
    }

}
