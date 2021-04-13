package cl.benm.observable.unwrap;

import java.util.ArrayList;
import java.util.List;

import cl.benm.observable.Combiner;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observer;

/**
 * A combiner that discards inputs with an Exception
 * @param <IN> The input type
 * @param <OUT> The output type
 */
public abstract class SimpleCombiner<IN, OUT> implements Combiner<ExceptionOrValue<IN>, ExceptionOrValue<OUT>> {

    @Override
    public ExceptionOrValue<OUT> combine(List<ExceptionOrValue<IN>> in) {
        List<IN> filtered = new ArrayList<>();

        for (ExceptionOrValue<IN> i: in) {
            if (i instanceof ExceptionOrValue.Value) {
                filtered.add(((ExceptionOrValue.Value<IN>) i).getValue());
            }
        }

        return combineFiltered(filtered);
    }

    public abstract ExceptionOrValue<OUT> combineFiltered(List<IN> in);

}
