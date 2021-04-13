package cl.benm.observable.concrete;

import java.util.List;
import java.util.concurrent.Executor;

import cl.benm.observable.Combiner;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.base.AggregateObservable;

/**
 * Combines a list of Observable results based on a {@link Combiner}
 * @param <IN> The input type
 * @param <T> The output type
 */
public class CombinedObservable<IN, T> extends AggregateObservable<IN, T> {

    Combiner<ExceptionOrValue<IN>, ExceptionOrValue<T>> combiner;

    public CombinedObservable(List<Observable<IN>> delegates, Combiner<ExceptionOrValue<IN>, ExceptionOrValue<T>> combiner, Executor executor) {
        super(delegates, executor);
        this.combiner = combiner;
    }

    @Override
    protected void onAllUpdate(List<ExceptionOrValue<IN>> value) {
        super.onAllUpdate(value);
        emit(combiner.combine(value));
    }
}
