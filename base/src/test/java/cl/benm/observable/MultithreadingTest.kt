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

        val emitter = EmissionOverTimeObservable(emissions.map { ExceptionOrValue.Value<Int>(it) })

        emitter.transformAsync(AsyncTransformation<Int, Int> {
            Observables.immediateObservable(it+1)
        }, ThreadExecutor())
        .transformAsync(AsyncTransformation<Int, Int> {
            Observables.immediateObservable(it+1)
        }, ThreadExecutor())
        .observe(object: Observer<Int> {
            override fun onChanged(value: Int?) {
                Assert.assertTrue(emissions.contains((value ?: -1)  - 2))
            }

            override fun onException(exception: Throwable?) {
                throw exception!!
            }
        }, ThreadExecutor())

        emitter.start()
    }

}

class ThreadExecutor: Executor {

    override fun execute(command: Runnable?) {
        object: Thread() {
            override fun run() {
                command?.run()
            }
        }.start()
    }

}