package cl.benm.observable.base;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import cl.benm.observable.EmissionType;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;

/**
 * Aggregates a list of Observers for an implementer to transform and return
 * @param <IN> The type of the input Observables
 * @param <T> The output type of the Observable
 */
public abstract class AggregateObservable<IN, T> extends ValueObservable<T> {

    protected List<Observable<IN>> delegates;
    protected Executor executor;

    public AggregateObservable(List<Observable<IN>> delegates, Executor executor) {
        this.delegates = delegates;
        this.executor = executor;
    }

    private final Observer<IN> delegateObserver = new Observer<IN>() {
        @Override
        public void onChanged(IN value) {
            onObservationChange();
        }

        @Override
        public void onException(Throwable exception) {
            onObservationChange();
        }
    };

    private void onObservationChange() {
        boolean allEmitted = true;
        List<ExceptionOrValue<IN>> values = new ArrayList<>();

        for (Observable<IN> observable: delegates) {
            if (observable.hasEmittedFirst()) {
                values.add(observable.value());
            } else {
                allEmitted = false;
                values.add(null);
            }
        }

        onUpdate(values);
        if (allEmitted) {
            onAllUpdate(values);
        }
    }

    /**
     * When any Observer emits
     * @param value The collected values
     */
    protected void onUpdate(List<ExceptionOrValue<IN>> value) {

    }

    /**
     * Once all Observers have emitted at least once, begin emitting all changes
     * @param value The collected values
     */
    protected void onAllUpdate(List<ExceptionOrValue<IN>> value) {

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
    protected void onActive() {
        super.onActive();
        for (Observable<IN> delegate: delegates) {
            delegate.observe(delegateObserver, executor);
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        for (Observable<IN> delegate: delegates) {
            delegate.removeObserver(delegateObserver);
        }
    }
}
