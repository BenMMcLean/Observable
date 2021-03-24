package cl.benm.observable.concrete;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import cl.benm.observable.AbstractObservable;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observer;

/**
 * A basic raw implementation of the Observable. Emission is achieved by calling emit()
 * @param <T> The type of the Observable
 */
public abstract class ValueObservable<T> extends AbstractObservable<T> {

    // List of non lifecycle observers
    private final List<Observer<T>> observerList = new ArrayList<>();
    // Map of lifecycle observers
    private final Map<LifecycleOwner, List<Observer<T>>> lifecycleOwnerListMap = new HashMap<>();
    // The executors to use for a given observer
    private final Map<Observer<T>, Executor> executorMap = new HashMap<>();

    private ExceptionOrValue<T> lastValue = null;
    private boolean emittedFirst = false;
    boolean active = false;

    /**
     * Emit a value to observers
     * @param value The value to emit
     */
    @Override
    protected void emit(ExceptionOrValue<T> value) {
        lastValue = value;
        emittedFirst = true;

        emitToList(value, observerList);
        for (Map.Entry<LifecycleOwner, List<Observer<T>>> l: lifecycleOwnerListMap.entrySet()) {
            if (inEmittableState(l.getKey())) {
                emitToList(value, l.getValue());
            }
        }
    }

    private void emitToList(ExceptionOrValue<T> value, List<Observer<T>> list) {
        for (Observer<T> o: list) {
            emit(value, o);
        }
    }

    private void emit(ExceptionOrValue<T> value, Observer<T> observer) {
        Executor executor = executorMap.get(observer);
        executor.execute(() -> observer.onChanged(value));
    }

    @Override
    public void observe(Observer<T> observer, Executor executor) {
        observerList.add(observer);
        executorMap.put(observer, executor);
        if (emittedFirst) {
            emit(lastValue, observer);
        }
        updateActive();
    }

    @Override
    public void observe(Observer<T> observer, LifecycleOwner lifecycleOwner, Executor executor) {
        List<Observer<T>> observers = lifecycleOwnerListMap.get(lifecycleOwner);

        if (observers == null) {
            observers = new ArrayList<>();
            lifecycleOwnerListMap.put(lifecycleOwner, observers);
        }

        if (observers.isEmpty()) lifecycleOwner.getLifecycle().addObserver(lifecycleObserver);

        observers.add(observer);

        if (emittedFirst && inEmittableState(lifecycleOwner)) {
            emit(lastValue, observer);
        }
        updateActive();
    }

    @Override
    public void removeObserver(Observer<T> observer) {
        executorMap.remove(observer);
        if (observerList.remove(observer)) {
            return;
        }

        for (Map.Entry<LifecycleOwner, List<Observer<T>>> l: lifecycleOwnerListMap.entrySet()) {
            List<Observer<T>> v = l.getValue();
            if (v.remove(observer)) {
                if (v.isEmpty()) l.getKey().getLifecycle().removeObserver(lifecycleObserver);
                return;
            }
        }
        updateActive();
    }

    @Override
    public void removeObservers(LifecycleOwner lifecycleOwner) {
        List<Observer<T>> l = lifecycleOwnerListMap.get(lifecycleOwner);
        if (l != null) {
            for (Observer<T> o: l) executorMap.remove(o);
        }

        lifecycleOwnerListMap.remove(lifecycleOwner);
        lifecycleOwner.getLifecycle().removeObserver(lifecycleObserver);
        updateActive();
    }

    /**
     * Check if there are active observers. Generally preferable to use the cached value "active"
     * @return If there are active observers
     */
    protected boolean hasActiveObservers() {
        if (!observerList.isEmpty()) {
            return true;
        }
        for (Map.Entry<LifecycleOwner, List<Observer<T>>> l: lifecycleOwnerListMap.entrySet()) {
            if (inEmittableState(l.getKey()) && !l.getValue().isEmpty()) return true;
        }
        return false;
    }

    private void updateActive() {
        boolean currentlyActive = hasActiveObservers();
        if (currentlyActive && !active) {
            onActive();
            active = true;
        } else if (!currentlyActive && active) {
            onInactive();
            active = false;
        }
    }

    /**
     * Called once when state changes from no active Observers to 1 or more active Observers
     */
    protected void onActive() {}
    /**
     * Called once when state changes from 1 or more active Observers to no active Observers
     */
    protected void onInactive() {}

    private final LifecycleObserver lifecycleObserver = (LifecycleEventObserver) (source, event) -> {
        if (inEmittableState(source) && emittedFirst) {
            List<Observer<T>> os = lifecycleOwnerListMap.get(source);
            if (os != null) {
                for(Observer<T> o: os) {
                    o.onChanged(lastValue);
                }
            }
        } else if (event == Lifecycle.Event.ON_DESTROY) {
            removeObservers(source);
        }
        updateActive();
    };
}
