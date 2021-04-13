package cl.benm.observable

import cl.benm.observable.concrete.SingleValueObservable
import org.junit.Assert
import org.junit.Test

class AsyncTransformTest {

    @Test(timeout = 500)
    fun singleTransform() {
        SingleValueObservable<Int>(ExceptionOrValue.Value(0))
            .transformAsync(AsyncTransformation<Int, Int> {
                SingleValueObservable(ExceptionOrValue.Value(it!!+1))
            }, DirectExecutor.INSTANCE)
            .observe(object: Observer<Int> {
                override fun onChanged(value: Int?) {
                    Assert.assertEquals(value, 1)
                }

                override fun onException(exception: Throwable?) {
                    throw exception!!
                }
            }, DirectExecutor.INSTANCE)
    }

    @Test(timeout = 500)
    fun multipleTransform() {
        val emissions = listOf(
            0,
            1,
            5
        )
        var emissionIndex = 0

        EmissionOverTimeObservable<Int>(emissions.map { ExceptionOrValue.Value(it) }).apply {
            transformAsync(AsyncTransformation<Int, Int> {
                SingleValueObservable(ExceptionOrValue.Value(it!!+1))
            }, DirectExecutor.INSTANCE)
                .observe(object: Observer<Int> {
                    override fun onChanged(value: Int?) {
                        Assert.assertEquals(value, emissions[emissionIndex++]+1)
                    }

                    override fun onException(exception: Throwable?) {
                        throw exception!!
                    }
                }, DirectExecutor.INSTANCE)

            start()
        }
    }

}