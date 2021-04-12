package cl.benm.observable

import cl.benm.observable.concrete.SingleValueObservable
import cl.benm.observable.unwrap.SimpleObserver
import cl.benm.observable.unwrap.SimpleTransformation
import org.junit.Assert
import org.junit.Test

class TransformTest {

    @Test(timeout = 500)
    fun singleTransform() {
        val emitter = SingleValueObservable<Int>(ExceptionOrValue.Value(0))
            .transform(object: SimpleTransformation<Int, Int>() {
                override fun transformSuccess(`in`: Int?): Int {
                    return `in`!!+1
                }
            }, DirectExecutor.INSTANCE)
        emitter.observe(object: SimpleObserver<Int>() {
            override fun onSuccess(value: Int?) {
                Assert.assertEquals(value, 1)
            }

            override fun onFailure(throwable: Throwable?) {
                throw throwable!!
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
        val transformed = emitter.transform(object: SimpleTransformation<Int, Int>() {
            override fun transformSuccess(`in`: Int?): Int {
                return `in`!!+1
            }
        }, DirectExecutor.INSTANCE)
        transformed.observe(object: SimpleObserver<Int>() {
            override fun onSuccess(value: Int?) {
                Assert.assertEquals(value, emissions[emissionIndex++]+1)
            }

            override fun onFailure(throwable: Throwable?) {
                throw throwable!!
            }
        }, DirectExecutor.INSTANCE)
        emitter.start()
    }

}