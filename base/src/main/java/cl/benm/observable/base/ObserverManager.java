package cl.benm.observable.base;

import androidx.lifecycle.LifecycleOwner;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import cl.benm.observable.Observer;

/**
 * Manages a collection of Observers
 * @param <T> The type of the Observers
 */
public class ObserverManager<T> {

    private HashMap<LifecycleOwner, List<ObserverWrapper<T>>> observers = new HashMap<>();

    public ObserverManager() {
        observers.put(null, new ArrayList<>());
    }

    /**
     * Add an Observer to the manager
     * @param o The Observer to add
     * @param executor The executor of the Observer
     * @return If the Observer was the first of it's LifecycleOwner (in this case, null)
     */
    public boolean add(Observer<T> o, Executor executor) {
        return add(o, null, executor);
    }

    /**
     * Add an Observer to the manager
     * @param o The Observer to add
     * @param lifecycleOwner The lifecycle to manage this Observer
     * @param executor The executor of the Observer
     * @return If the Observer was the first of it's LifecycleOwner
     */
    public boolean add(Observer<T> o, LifecycleOwner lifecycleOwner, Executor executor) {
        List<ObserverWrapper<T>> obs = observers.get(lifecycleOwner);

        if (obs == null) {
            obs = new ArrayList<>();
            observers.put(lifecycleOwner, obs);
        }

        boolean empty = obs.isEmpty();

        obs.add(new ObserverWrapper<>(o, executor));

        return empty;
    }

    /**
     * Remove an Observer
     * @param o The Observer to remove
     */
    public void remove(Observer<T> o) {
        Iterator<Map.Entry<LifecycleOwner, List<ObserverWrapper<T>>>> owsI = observers.entrySet().iterator();
        while (owsI.hasNext()) {
            boolean exit = false;
            Map.Entry<LifecycleOwner, List<ObserverWrapper<T>>> ows = owsI.next();

            Iterator<ObserverWrapper<T>> owI = ows.getValue().iterator();
            while (owI.hasNext()) {
                ObserverWrapper<T> ow = owI.next();
                if (ow.observer.get() == o) {
                    owI.remove();
                    exit = true;
                    break;
                }
            }

            if (ows.getValue().isEmpty() && ows.getKey() != null) owsI.remove();
            if (exit) return;
        }
    }

    /**
     * Remove all Observers attached to a lifecycle
     * @param owner The lifecycle to remove Observers from
     */
    public void remove(LifecycleOwner owner) {
        if (owner == null) return;

        observers.remove(owner);
    }

    /**
     * Get all Observers
     * @return All Observers
     */
    public Map<LifecycleOwner, List<ObserverWrapper<T>>> getObservers() {
        return new HashMap<>(observers);
    }

    /**
     * Get Observers attached to a specific lifecycle
     * @param lifecycleOwner The lifecycle to fetch Observers
     * @return Observers attached to a specific lifecycle
     */
    public List<ObserverWrapper<T>> getObservers(LifecycleOwner lifecycleOwner) {
        return new ArrayList<>(observers.get(lifecycleOwner));
    }

    /**
     * Get the executor of an Observer
     * @param observer The Observer to get an executor of
     * @return The executor of an Observer
     */
    public Executor getExecutor(Observer<T> observer) {
        for (List<ObserverWrapper<T>> ows: observers.values()) {
            for (ObserverWrapper<T> ow: ows) {
                if (observer == ow.observer.get()) {
                    return ow.executor;
                }
            }
        }
        return null;
    }

    /**
     * Wraps an Observer with the data required to execute it, in this case
     * an Executor instance
     * @param <T> The type of the Observer
     */
    public static class ObserverWrapper<T> {

        private final WeakReference<Observer<T>> observer;
        private final Executor executor;

        public ObserverWrapper(Observer<T> observer, Executor executor) {
            this.observer = new WeakReference<>(observer);
            this.executor = executor;
        }

        /**
         * Returns a weak reference to an Observer
         * @return A weak reference to an Observer
         */
        public WeakReference<Observer<T>> getObserver() {
            return observer;
        }

        /**
         * Returns an executor
         * @return An executor
         */
        public Executor getExecutor() {
            return executor;
        }
    }

}
