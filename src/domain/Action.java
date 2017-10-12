package domain;

import java.util.List;

/**
 * Created by Mart on 8.10.2017.
 */
public interface Action {

    List<AtomicFluent> getPreconditions();

    List<AtomicFluent> getEffects();

    boolean perform();

    boolean preconditionsMet();

    boolean effectsCompleted();
}
