package cl.benm.observable

import cl.benm.observable.concrete.ValueObservable

class EmissionOverTimeObservable<T>(
    private val emissions: List<ExceptionOrValue<T>>,
    private val interval: Long
): ValueObservable<T>() {

    init {
        Thread {
            for (emission in emissions) {
                emit(emission)
                Thread.sleep(interval)
            }
        }.start()
    }

    override fun getEmissionType(): EmissionType {
        return EmissionType.MULTIPLE
    }


}