package cl.benm.observable

import cl.benm.observable.concrete.SingleValueObservable
import org.junit.Assert
import org.junit.Test
import java.io.IOException

class ExceptionTest {

    @Test
    fun exceptionEmission() {
        SingleValueObservable<String>(ExceptionOrValue.Exception(Throwable()))
            .observeOnce(object: Observer<String> {
                override fun onChanged(value: String?) {
                    Assert.fail()
                }

                override fun onException(exception: Throwable?) {
                    // Pass
                }
            }, DirectExecutor.INSTANCE)
    }

    @Test
    fun exceptionTransform() {
        SingleValueObservable<String>(ExceptionOrValue.Exception(Throwable()))
                .transform(Transformation<String, String> {
                    Assert.fail()
                    it
                }, DirectExecutor.INSTANCE)
                .observeOnce(object: Observer<String> {
                    override fun onChanged(value: String?) {
                        Assert.fail()
                    }

                    override fun onException(exception: Throwable?) {
                        // Pass
                    }
                }, DirectExecutor.INSTANCE)
    }

    @Test
    fun exceptionCatching() {
        SingleValueObservable<String>(ExceptionOrValue.Exception(Throwable()))
                .transform(Transformation<String, String> {
                    Assert.fail()
                    it
                }, DirectExecutor.INSTANCE)
                .catching(Throwable::class.java, Transformation<Throwable, String> {
                    "Test"
                }, DirectExecutor.INSTANCE)
                .observeOnce(object: Observer<String> {
                    override fun onChanged(value: String?) {
                        Assert.assertEquals("Test", value)
                    }

                    override fun onException(exception: Throwable?) {
                        Assert.fail()
                    }
                }, DirectExecutor.INSTANCE)
    }

    @Test
    fun exceptionFiltering() {
        SingleValueObservable<String>(ExceptionOrValue.Exception(Throwable()))
                .catching(IOException::class.java, Transformation<IOException, String> {
                    ""
                }, DirectExecutor.INSTANCE)
                .observeOnce(object: Observer<String> {
                    override fun onChanged(value: String?) {
                        Assert.fail()
                    }

                    override fun onException(exception: Throwable?) {

                    }
                }, DirectExecutor.INSTANCE)
    }

}