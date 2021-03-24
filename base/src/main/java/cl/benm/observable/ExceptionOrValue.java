package cl.benm.observable;

/**
 * A "sealed" class that can either represent a value or an exception
 * @param <T> The type of the value
 */
public interface ExceptionOrValue<T> {
    /**
     * A value
     * @param <T> The type of the value
     */
    class Value<T> implements ExceptionOrValue<T> {
        T value;

        public Value(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }

    /**
     * An exception
     * @param <T> The type of the possible value
     */
    class Exception<T> implements ExceptionOrValue<T> {
        Throwable throwable;

        public Exception(Throwable throwable) {
            this.throwable = throwable;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public void setThrowable(Throwable throwable) {
            this.throwable = throwable;
        }
    }
}
