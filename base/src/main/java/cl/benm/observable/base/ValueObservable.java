package cl.benm.observable.base;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;

import cl.benm.observable.AbstractObservable;
import cl.benm.observable.AsyncTransformation;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;
import cl.benm.observable.Transformation;

/**
 * A basic raw implementation of the Observable. Emission is achieved by calling emit()
 * @param <T> The type of the Observable
 */
public abstract class ValueObservable<T> extends AbstractObservable<T> {

    ObserverManager<T> observerManager = new ObserverManager<>();

    private ExceptionOrValue<T> lastEmission = null;
    protected boolean emittedFirst = false;
    protected boolean active = false;

    /**
     * Emit a value to observers
     * @param value The value to emit
     */
    protected void emit(ExceptionOrValue<T> value) {
        lastEmission = value;
        emittedFirst = true;

        for (Map.Entry<LifecycleOwner, List<ObserverManager.ObserverWrapper<T>>> l: new HashMap<>(observerManager.getObservers()).entrySet()) {
            if (l.getKey() == null || inEmittableState(l.getKey())) {
                emitToList(value, new ArrayList<>(l.getValue()));
            }
        }
    }

    private void emitToList(ExceptionOrValue<T> value, List<ObserverManager.ObserverWrapper<T>> list) {
        for (ObserverManager.ObserverWrapper<T> o : list) {
            emit(value, o);
        }
    }

    private void emit(ExceptionOrValue<T> value, ObserverManager.ObserverWrapper<T> observer) {
        Observer<T> localObserver = observer.getObserver().get();
        if (localObserver == null) return;
        emit(value, localObserver, observer.getExecutor());
    }

    private void emit(ExceptionOrValue<T> value, Observer<T> observer, Executor executor) {
        executor.execute(() -> {
            if (value instanceof ExceptionOrValue.Value) {
                observer.onChanged(((ExceptionOrValue.Value<T>) value).getValue());
            } else if (value instanceof ExceptionOrValue.Exception) {
                observer.onException(((ExceptionOrValue.Exception<T>) value).getThrowable());
            }
        });
    }

    @Override
    public void observe(Observer<T> observer, Executor executor) {
        observerManager.add(observer, executor);
        if (emittedFirst) {
            emit(lastEmission, observer, executor);
        }
        updateActive();
    }

    @Override
    public void observe(Observer<T> observer, LifecycleOwner lifecycleOwner, Executor executor) {
        if (observerManager.add(observer, lifecycleOwner, executor)) lifecycleOwner.getLifecycle().addObserver(lifecycleObserver);

        if (emittedFirst && inEmittableState(lifecycleOwner)) {
            emit(lastEmission, observer, executor);
        }
        updateActive();
    }

    @Override
    public void removeObserver(Observer<T> observer) {
        observerManager.remove(observer);
        updateActive();
    }

    @Override
    public void removeObservers(LifecycleOwner lifecycleOwner) {
        observerManager.remove(lifecycleOwner);
        lifecycleOwner.getLifecycle().removeObserver(lifecycleObserver);
        updateActive();
    }

    /**
     * Check if there are active observers. Generally preferable to use the cached value "active"
     * @return If there are active observers
     */
    protected boolean hasActiveObservers() {
        Map<LifecycleOwner, List<ObserverManager.ObserverWrapper<T>>> observers = observerManager.getObservers();
        if (!observers.get(null).isEmpty()) {
            return true;
        }
        for (Map.Entry<LifecycleOwner, List<ObserverManager.ObserverWrapper<T>>> l: observers.entrySet()) {
            if (l.getKey() != null && inEmittableState(l.getKey()) && !l.getValue().isEmpty()) return true;
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

    @Override
    public boolean hasEmittedFirst() {
        return emittedFirst;
    }

    @Override
    public ExceptionOrValue<T> value() {
        return lastEmission;
    }

    private final LifecycleObserver lifecycleObserver = (LifecycleEventObserver) (source, event) -> {
        if (inEmittableState(source) && emittedFirst) {
            List<ObserverManager.ObserverWrapper<T>> os = observerManager.getObservers(source);
            if (os != null) {
                emitToList(lastEmission, os);
            }
        } else if (event == Lifecycle.Event.ON_DESTROY) {
            removeObservers(source);
        }
        updateActive();
    };
}
