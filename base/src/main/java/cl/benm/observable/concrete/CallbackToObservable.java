package cl.benm.observable.concrete;

import cl.benm.observable.EmissionType;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.base.ValueObservable;

/**
 * Provide a callback interface for an Observable
 */
public class CallbackToObservable {

    private CallbackToObservable() {}

    /**
     * Provide a callback interface for an Observable
     * @param resolver The object to receive the callback completer
     * @param <T> The Observable type
     * @return An Observable wrapping a callback
     */
    public static <T> Observable<T> toObservable(Resolver<T> resolver) {
        CallbackObservable<T> o = new CallbackObservable<>();
        resolver.resolve(new Completer<>(o));
        return o;
    }

    /**
     * Object to receive a callback completer
     * @param <T> The type of the callback
     */
    public interface Resolver<T> {

        /**
         * Receive a callback completer
         * @param completer The callback completer
         */
        void resolve(Completer<T> completer);

    }

    /**
     * Callback interface for an Observable
     * @param <T>
     */
    public static final class Completer<T> {

        private final CallbackObservable<T> callbackObservable;

        Completer(CallbackObservable<T> callbackObservable) {
            this.callbackObservable = callbackObservable;
        }

        /**
         * Set the value of the Observable
         * @param value The value
         */
        public void set(T value) {
            callbackObservable.emit(new ExceptionOrValue.Value<>(value));
        }

        /**
         * Set an exception
         * @param t The exception
         */
        public void setException(Throwable t) {
            callbackObservable.emit(new ExceptionOrValue.Exception<>(t));
        }

    }

    /**
     * A simple extension of ValueObservable that exposes the underlying {@link ValueObservable#emit(ExceptionOrValue)}}
     * @param <T> The type of the Observable
     */
    private static final class CallbackObservable<T> extends ValueObservable<T> {

        @Override
        public void emit(ExceptionOrValue<T> value) {
            super.emit(value);
        }

        @Override
        public EmissionType getEmissionType() {
            return EmissionType.MULTIPLE;
        }

    }

}
