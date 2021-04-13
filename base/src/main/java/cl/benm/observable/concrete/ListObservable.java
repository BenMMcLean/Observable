package cl.benm.observable.concrete;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.helpers.IllegalExceptionOrValueException;

public class ListObservable<T> extends AggregateObservable<T, List<T>> {

    public ListObservable(List<Observable<T>> delegates, Executor executor) {
        super(delegates, executor);
    }

    @Override
    protected void onAllUpdate(List<ExceptionOrValue<T>> value) {
        super.onAllUpdate(value);
        List<T> collection = new ArrayList<>();
        for (ExceptionOrValue<T> v: value) {
            if (v instanceof ExceptionOrValue.Value) {
                collection.add(((ExceptionOrValue.Value<T>) v).getValue());
            } else if (v instanceof ExceptionOrValue.Exception) {
                emit(new ExceptionOrValue.Exception<>(((ExceptionOrValue.Exception<T>) v).getThrowable()));
                return;
            } else {
                emit(new ExceptionOrValue.Exception<>(new IllegalExceptionOrValueException("onAllUpdate(List<ExceptionOrValue<T>>)")));
                return;
            }
        }
        emit(new ExceptionOrValue.Value<>(collection));
    }

}
