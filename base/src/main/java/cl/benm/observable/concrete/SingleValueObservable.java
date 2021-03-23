package cl.benm.observable.concrete;

import cl.benm.observable.EmissionType;
import cl.benm.observable.ExceptionOrValue;

public class SingleValueObservable<T> extends ValueObservable<T> {

    public SingleValueObservable(ExceptionOrValue<T> value) {
        emit(value);
    }

    @Override
    public EmissionType getEmissionType() {
        return EmissionType.SINGLE;
    }
}
