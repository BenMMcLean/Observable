package cl.benm.observable

import cl.benm.observable.concrete.SingleValueObservable
import org.junit.Assert
import org.junit.Test

class SimpleEmissionTest {

    @Test(timeout = 500)
    fun singleEmission() {
        val emitter = SingleValueObservable<String>(ExceptionOrValue.Value("Test"))
        emitter.observe(object: Observer<String> {
            override fun onChanged(value: String?) {
                Assert.assertEquals(value, "Test")
            }

            override fun onException(exception: Throwable?) {
                throw exception!!
            }
        }, DirectExecutor.INSTANCE)
    }

    @Test(timeout = 10000)
    fun multipleEmission() {
        val emissions = listOf(
            "Test",
            "Test 2",
            "Test 3"
        )
        val emitter = EmissionOverTimeObservable<String>(emissions.map { ExceptionOrValue.Value(it) }.toList())
        var emissionIndex = 0
        emitter.observe(object: Observer<String> {
            override fun onChanged(value: String?) {
                Assert.assertEquals(value, emissions[emissionIndex++])
            }

            override fun onException(exception: Throwable?) {
                throw exception!!
            }
        }, DirectExecutor.INSTANCE)
        emitter.start()
    }

}