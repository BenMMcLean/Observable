package cl.benm.observable;

import java.util.concurrent.Executor;

/**
 * Copied from com.google.common.util.concurrent
 */
enum DirectExecutor implements Executor {
    INSTANCE;

    @Override
    public void execute(Runnable command) {
        command.run();
    }

    @Override
    public String toString() {
        return "MoreExecutors.directExecutor()";
    }
}