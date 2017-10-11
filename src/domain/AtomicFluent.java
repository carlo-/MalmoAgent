package domain;

import main.Observations;

import java.util.function.Predicate;

/**
 * Created by Mart on 8.10.2017.
 */
public interface AtomicFluent extends Predicate<Observations> {

    default AtomicFluent negate() {
        return (t) -> !test(t);
    }
}
