package cl.benm.observable.concrete;

import java.util.List;
import java.util.concurrent.Executor;

import cl.benm.observable.Combiner;
import cl.benm.observable.EmissionType;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;

public class CombinedObservable<IN, T> extends AggregateObservable<IN, T> {

    Combiner<ExceptionOrValue<IN>, ExceptionOrValue<T>> combiner;

    public CombinedObservable(List<Observable<IN>> delegates, Combiner<ExceptionOrValue<IN>, ExceptionOrValue<T>> combiner, Executor executor) {
        super(delegates, executor);
        this.combiner = combiner;
    }

    @Override
    public EmissionType getEmissionType() {
        boolean allSingle = true;
        for (Observable<IN> o: delegates) {
            allSingle &= o.getEmissionType() == EmissionType.SINGLE;
        }
        return (allSingle) ? EmissionType.SINGLE : EmissionType.MULTIPLE;
    }

    @Override
    protected void onAllUpdate(List<ExceptionOrValue<IN>> value) {
        super.onAllUpdate(value);
        emit(combiner.combine(value));
    }
}
