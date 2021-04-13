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

public class ObserverManager<T> {

    private HashMap<LifecycleOwner, List<ObserverWrapper<T>>> observers = new HashMap<>();

    public ObserverManager() {
        observers.put(null, new ArrayList<>());
    }

    boolean add(Observer<T> o, Executor executor) {
        return add(o, null, executor);
    }

    boolean add(Observer<T> o, LifecycleOwner lifecycleOwner, Executor executor) {
        List<ObserverWrapper<T>> obs = observers.get(lifecycleOwner);

        if (obs == null) {
            obs = new ArrayList<>();
            observers.put(lifecycleOwner, obs);
        }

        boolean empty = obs.isEmpty();

        obs.add(new ObserverWrapper<>(o, executor));

        return empty;
    }

    void remove(Observer<T> o) {
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

    void remove(LifecycleOwner owner) {
        if (owner == null) return;

        observers.remove(owner);
    }

    Map<LifecycleOwner, List<ObserverWrapper<T>>> getObservers() {
        return new HashMap<>(observers);
    }

    List<ObserverWrapper<T>> getObservers(LifecycleOwner lifecycleOwner) {
        return new ArrayList<>(observers.get(lifecycleOwner));
    }

    Executor getExecutor(Observer<T> observer) {
        for (List<ObserverWrapper<T>> ows: observers.values()) {
            for (ObserverWrapper<T> ow: ows) {
                if (observer == ow.observer.get()) {
                    return ow.executor;
                }
            }
        }
        return null;
    }

    public static class ObserverWrapper<T> {

        private final WeakReference<Observer<T>> observer;
        private final Executor executor;

        public ObserverWrapper(Observer<T> observer, Executor executor) {
            this.observer = new WeakReference<>(observer);
            this.executor = executor;
        }

        public WeakReference<Observer<T>> getObserver() {
            return observer;
        }

        public Executor getExecutor() {
            return executor;
        }
    }

}
