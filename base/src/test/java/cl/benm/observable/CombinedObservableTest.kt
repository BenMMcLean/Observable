package cl.benm.observable

import cl.benm.observable.concrete.CombinedObservable
import cl.benm.observable.concrete.SingleValueObservable
import cl.benm.observable.unwrap.SimpleCombiner
import cl.benm.observable.unwrap.SimpleObserver
import org.junit.Assert
import org.junit.Test

class CombinedObservableTest {

    @Test(timeout = 500)
    fun combined() {
        val results = listOf(
                listOf("Test", "Test2"),
                listOf("Test", "Test3")
        )
        var resultIndex = 0

        val emitter1 = SingleValueObservable<String>(ExceptionOrValue.Value("Test"))
        val emitter3 = EmissionOverTimeObservable<String>(listOf(ExceptionOrValue.Value("Test2"), ExceptionOrValue.Value("Test3")))

        val combined = CombinedObservable<String, List<String>>(
                listOf(emitter1, emitter3),
                object: SimpleCombiner<String, List<String>>() {
                    override fun combineFiltered(`in`: MutableList<String>?): ExceptionOrValue<List<String>> {
                        return ExceptionOrValue.Value(`in`)
                    }
                },
                DirectExecutor.INSTANCE
        )

        combined.observe(object: SimpleObserver<List<String>>() {
            override fun onSuccess(value: List<String>?) {
                Assert.assertEquals(value, results[resultIndex++])
            }

            override fun onFailure(throwable: Throwable?) {
                throw throwable!!
            }
        }, DirectExecutor.INSTANCE)

        emitter3.start()
    }

}