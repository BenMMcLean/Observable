package cl.benm.observable.concrete;

import cl.benm.observable.EmissionType;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.base.ValueObservable;

/**
 * Outputs a single value known at construction time
 * @param <T> The type of the value
 */
public class SingleValueObservable<T> extends ValueObservable<T> {

    /**
     * Instantiate the observable
     * @param value The value to emit
     */
    public SingleValueObservable(ExceptionOrValue<T> value) {
        emit(value);
    }

    @Override
    public EmissionType getEmissionType() {
        return EmissionType.SINGLE;
    }
}
