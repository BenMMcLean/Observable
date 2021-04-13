package cl.benm.observable.concrete;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import cl.benm.observable.EmissionType;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;

public class CollectorObservable<T> extends ValueObservable<T> {

    Observable<T> delegate;
    boolean emitHistory = true;
    int maxHistory = -1;

    private List<ExceptionOrValue<T>> history = new ArrayList<>();

    public CollectorObservable(Observable<T> delegate) {
        this.delegate = delegate;
    }

    public CollectorObservable(Observable<T> delegate, boolean emitHistory) {
        this.delegate = delegate;
        this.emitHistory = emitHistory;
    }

    public CollectorObservable(Observable<T> delegate, boolean emitHistory, int maxHistory) {
        this.delegate = delegate;
        this.emitHistory = emitHistory;
        this.maxHistory = maxHistory;
    }

    @Override
    protected void emit(ExceptionOrValue<T> value) {
        super.emit(value);
        history.add(value);
        if (maxHistory > 0 && history.size() > maxHistory) {
            history.remove(0);
        }
    }

    @Override
    protected void emit(ExceptionOrValue<T> value, Observer<T> observer) {
        if (hasEmitted.containsKey(observer) && hasEmitted.get(observer)) {
            super.emit(value, observer);
        } else {
            hasEmitted.put(observer, true);
            Executor executor = executorMap.get(observer);
            executor.execute(() -> {
                for (ExceptionOrValue<T> e : history) {
                    emitValue(e, observer);
                }
                if (history.get(history.size()-1) != value) {
                    emitValue(value, observer);
                }
            });
        }
    }

    @Override
    public EmissionType getEmissionType() {
        return delegate.getEmissionType();
    }

    public List<ExceptionOrValue<T>> getHistory() {
        return new ArrayList<>(history);
    }

}
