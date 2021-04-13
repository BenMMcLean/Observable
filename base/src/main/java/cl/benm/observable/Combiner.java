package cl.benm.observable;

import java.util.List;

/**
 * Combine a list of inputs into a single output
 * @param <IN> The input type
 * @param <OUT> The output type
 */
public interface Combiner<IN, OUT> {

    OUT combine(List<IN> in);

}
