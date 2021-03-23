package cl.benm.observable;

public interface ExceptionOrValue<T> {
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
