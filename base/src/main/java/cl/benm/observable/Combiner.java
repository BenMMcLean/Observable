package cl.benm.observable;

import java.util.List;

public interface Combiner<IN, OUT> {

    OUT combine(List<IN> in);

}
