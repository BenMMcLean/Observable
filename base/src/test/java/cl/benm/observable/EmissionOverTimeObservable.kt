package cl.benm.observable

import cl.benm.observable.base.ValueObservable

class EmissionOverTimeObservable<T>(
    private val emissions: List<ExceptionOrValue<T>>
): ValueObservable<T>() {

    override fun getEmissionType(): EmissionType {
        return EmissionType.MULTIPLE
    }

    fun start() {
        Thread {
            for (emission in emissions) {
                emit(emission)
            }
        }.start()
    }

}