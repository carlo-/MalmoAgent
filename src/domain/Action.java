package domain;

import main.Observations;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Mart on 8.10.2017.
 */
public interface Action extends Consumer<Observations> {

    List<AtomicFluent> getPreconditions();
    List<AtomicFluent> getEffects();
}
