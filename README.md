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
    implementation 'com.github.BenMMcLean.Observable:base:1.0.3'
    // The LiveData Observable wrappers
    implementation 'com.github.BenMMcLean.Observable:livedata:1.0.3'
    // The ListenableFuture Observable wrappers
    implementation 'com.github.BenMMcLean.Observable:future:1.0.3'
}
```

## Usage

To get an Observable, either implement the `Observable` interface, extend `ValueObservable`, or wrap a `LiveData` or `ListenableFuture` as such:
```kotlin
val observable = LiveDataObservable<String>(liveData);
val observable = FutureObservable<String>(future);
```
The output of this observable can then be observed
```kotlin
observable.observe(object: Observer<String>() {
    override fun onChanged(value: String?) {
        Log.d("Observable", "$value")
    }

    override fun onException(exception: Throwable?) {
        Log.e("Observable", "${exception?.message}", exception)
    }
}, MoreExecutors.directExecutor())
```

### Transformation
Observables can be synchronously transformed
```kotlin
val emitter = SingleValueObservable<Int>(ExceptionOrValue.Value(1))
    .transform(Transformation<Int, Int> {
        it!!+1
    }, MoreExecutors.directExecutor())
```
Exceptions will automatically be propagated down the chain of Observables, to catch one you can do such:
```kotlin
emitter.catching(IOException::class.java, Transformation<IOException, Int> {
  Log.e("Observable", "${it?.message}", it)
  0
}, MoreExecutors.directExecutor())
```
### Async Transformation
If you need to perform an asynchronous operation on the emission of an Observable (such as a database read), this can be achieved through an AsyncTransform
```kotlin
emitter.transformAsync(AsyncTransformation<Query, List<Item>> {
  FutureObservable(database.readData(it))
}, MoreExecutors.directExecutor())
```
Exceptions can likewise be caught using a similar interface.
