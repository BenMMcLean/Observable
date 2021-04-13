package cl.benm.observable

import cl.benm.observable.concrete.CallbackToObservable
import cl.benm.observable.concrete.SingleValueObservable
import org.junit.Assert
import org.junit.Test

class CallbackToObservableTest {

    @Test(timeout = 500)
    fun callback() {
        CallbackToObservable.toObservable<String> { completer ->
            completer.set("Test")
        }.observeOnce(object: Observer<String> {
            override fun onChanged(value: String?) {
                Assert.assertEquals("Test", value)
            }

            override fun onException(exception: Throwable?) {
                throw exception!!
            }
        }, DirectExecutor.INSTANCE)
    }

    @Test(timeout = 500)
    fun callbackException() {
        CallbackToObservable.toObservable<String> { completer ->
            completer.setException(Throwable())
        }.observeOnce(object: Observer<String> {
            override fun onChanged(value: String?) {
                Assert.fail()
            }

            override fun onException(exception: Throwable?) {}
        }, DirectExecutor.INSTANCE)
    }

}