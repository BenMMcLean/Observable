package cl.benm.observable

import cl.benm.observable.concrete.CombinedObservable
import cl.benm.observable.concrete.SingleValueObservable
import cl.benm.observable.helpers.Observables
import cl.benm.observable.unwrap.SimpleCombiner
import org.junit.Assert
import org.junit.Test

class ListObservableTest {

    @Test(timeout = 500)
    fun combined() {
        val results = listOf(
                listOf("Test", "Test2"),
                listOf("Test", "Test3")
        )
        var resultIndex = 0

        val emitter1 = SingleValueObservable<String>(ExceptionOrValue.Value("Test"))
        val emitter3 = EmissionOverTimeObservable<String>(listOf(ExceptionOrValue.Value("Test2"), ExceptionOrValue.Value("Test3")))

        val combined = Observables.allAsList(listOf(emitter1, emitter3), DirectExecutor.INSTANCE)

        combined.observe(object: Observer<List<String>> {
            override fun onChanged(value: List<String>?) {
                Assert.assertEquals(value, results[resultIndex++])
            }

            override fun onException(exception: Throwable?) {
                throw exception!!
            }
        }, DirectExecutor.INSTANCE)

        emitter3.start()
    }

}