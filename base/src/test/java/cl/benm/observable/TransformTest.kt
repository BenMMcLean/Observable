package cl.benm.observable

import cl.benm.observable.concrete.SingleValueObservable
import org.junit.Assert
import org.junit.Test

class TransformTest {

    @Test(timeout = 500)
    fun singleTransform() {
        val emitter = SingleValueObservable<Int>(ExceptionOrValue.Value(0))
            .transform(Transformation<Int, Int> {
                it!!+1
            }, DirectExecutor.INSTANCE)
        emitter.observe(object: Observer<Int> {
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

        val emitter = EmissionOverTimeObservable<Int>(emissions.map { ExceptionOrValue.Value(it) })
        val transformed = emitter.transform(Transformation<Int, Int> {
            it!!+1
        }, DirectExecutor.INSTANCE)
        transformed.observe(object: Observer<Int> {
            override fun onChanged(value: Int?) {
                Assert.assertEquals(value, emissions[emissionIndex++]+1)
            }

            override fun onException(exception: Throwable?) {
                throw exception!!
            }
        }, DirectExecutor.INSTANCE)
        emitter.start()
    }

}