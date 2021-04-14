package cl.benm.observable

import cl.benm.observable.concrete.SingleValueObservable
import cl.benm.observable.helpers.Observables
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.Executor

class MultithreadingTest {

    @Test
    fun multithreadTransform() {
        Observables.immediateObservable(0)
                .transform(Transformation<Int, Int> {
                    it!!+1
                }, ThreadExecutor())
                .observe(object: Observer<Int> {
                    override fun onChanged(value: Int?) {
                        Assert.assertEquals(value, 1)
                    }

                    override fun onException(exception: Throwable?) {
                        throw exception!!
                    }
                }, ThreadExecutor())
    }

    @Test
    fun multithreadAsyncTransform() {
        Observables.immediateObservable(0)
                .transformAsync(AsyncTransformation<Int, Int> {
                    Observables.immediateObservable(it+1)
                }, ThreadExecutor())
                .observe(object: Observer<Int> {
                    override fun onChanged(value: Int?) {
                        Assert.assertEquals(value, 1)
                    }

                    override fun onException(exception: Throwable?) {
                        throw exception!!
                    }
                }, ThreadExecutor())
    }

    @Test
    fun multithreadMultipleAsyncTransform() {
        val emissions = listOf(
                0,
                2,
                5
        )
        var emissionIndex = 0

        EmissionOverTimeObservable(emissions.map { ExceptionOrValue.Value<Int>(it) })
                .transformAsync(AsyncTransformation<Int, Int> {
                    Observables.immediateObservable(it+1)
                }, ThreadExecutor())
                .observe(object: Observer<Int> {
                    override fun onChanged(value: Int?) {
                        Assert.assertEquals(emissions[emissionIndex++]+1, value)
                    }

                    override fun onException(exception: Throwable?) {
                        throw exception!!
                    }
                }, ThreadExecutor())
    }

}

class ThreadExecutor: Executor {

    override fun execute(command: Runnable?) {
        Thread {
            command?.run()
        }.start()
    }

}