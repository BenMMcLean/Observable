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

public class Observables {
    private Observables() {}

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

    public static <T> Observable<T> immediateObservable(T value) {
        return new SingleValueObservable<>(new ExceptionOrValue.Value<>(value));
    }

    public static <T> Observable<T> immediateFailedObservable(Throwable th) {
        return new SingleValueObservable<>(new ExceptionOrValue.Exception<>(th));
    }

    public static <T> Observable<List<T>> allAsList(List<Observable<T>> observables, Executor executor) {
        return new ListObservable<T>(observables, executor);
    }

}
