# Observable

A basic abstraction of LiveData and ListenableFuture for Android.

## Installation

Add JitPack to your project level `build.gradle` file
```groovy
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```
Then, include the modules as required
```groovy
dependencies {
    // The basic Observable interface and helper classes
    implementation 'com.github.BenMMcLean:Observable:base:1.0.0'
    // The LiveData Observable wrappers
    implementation 'com.github.BenMMcLean:Observable:livedata:1.0.0'
    // The ListenableFuture Observable wrappers
    implementation 'com.github.BenMMcLean:Observable:future:1.0.0'
}
```

## Usage

To get an Observable, either implement the `Observable` interface, extend `ValueObservable`, or wrap a `LiveData` or `ListenableFuture` as such:
```kotlin
val observable = LiveDataObservable<String>(liveData);
val observable = FutureObservable<String>(liveData);
```
The output of this observable can then be observed
```kotlin
observable.observe(object: SimpleObserver<String>() {
    override fun onSuccess(value: String?) {
        Log.d("Observable Output", "$value")
    }

    override fun onFailure(throwable: Throwable?) {
        throw throwable!!
    }
}, MoreExecutors.directExecutor())
```

### Transformation
Observables can be synchronously transformed
```kotlin
val emitter = SingleValueObservable<Int>(1)
    .transform(object: SimpleTransformation<Int, Int>() {
        override fun transformSuccess(input: Int?): Int {
            return input!!+1
        }
    }, MoreExecutors.directExecutor())
```
if kotlin is being used, this can be further simplified as
```kotlin
emitter.transformOrPropagate({
  it+1
}, MoreExecutors.directExecutor())
```
Both of these will skip the transform if an exception appears and pass it down the chain of Observables, if you want to intercept an exception you can do the following:
```kotlin
emitter.transform(object: SimpleTransformation<Int, Int>() {
    override fun transformSuccess(input: Int?): Int {
      return input!!+1
    }

    override fun transformFailure(throwable: Throwable?): ExceptionOrValue<R> {
      return ExceptionOrValue.Exception(throwable)
    }
}, MoreExecutors.directExecutor())
```
### Async Transformation
If you need to perform an asynchronous operation on the emission of an Observable (such as a database read), this can be achieved through an AsyncTransform (which uses a similar interface to a synchronous transform)
```kotlin
emitter.transformAsyncOrPropagate({
  FutureObservable(database.readData(it))
}, MoreExecutors.directExecutor())
```
