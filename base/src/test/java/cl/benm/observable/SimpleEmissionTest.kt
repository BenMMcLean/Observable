package cl.benm.observable

import cl.benm.observable.concrete.SingleValueObservable
import cl.benm.observable.unwrap.SimpleObserver
import org.junit.Assert
import org.junit.Test
import org.junit.runners.JUnit4

class SimpleEmissionTest {

    @Test(timeout = 500)
    fun singleEmission() {
        val emitter = SingleValueObservable<String>(ExceptionOrValue.Value("Test"))
        emitter.observe(object: SimpleObserver<String>() {
            override fun onSuccess(value: String?) {
                Assert.assertEquals(value, "Test")
            }

            override fun onFailure(throwable: Throwable?) {
                throw throwable!!
            }
        }, DirectExecutor.INSTANCE)
    }

    @Test(timeout = 500)
    fun multipleEmission() {
        val emissions = listOf(
            "Test",
            "Test 2",
            "Test 3"
        )
        val emitter = EmissionOverTimeObservable<String>(emissions.map { ExceptionOrValue.Value(it) }, 10)
        var emissionIndex = 0
        emitter.observe(object: SimpleObserver<String>() {
            override fun onSuccess(value: String?) {
                Assert.assertEquals(value, emissions[emissionIndex++])
            }

            override fun onFailure(throwable: Throwable?) {
                throw throwable!!
            }
        }, DirectExecutor.INSTANCE)
    }

}