package domain;

import domain.actions.MoveTo;
import domain.actions.Stop;
import domain.fluents.IsAt;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Mart on 8.10.2017.
 */
public enum ActionEffects {
    MoveTo(MoveTo.class, Arrays.asList(IsAt.class)),
    Stop(Stop.class, Arrays.asList());

    private final Class clazz;
    private final List<Class> effects;

    ActionEffects(Class clazz, List<Class> effects) {
        this.clazz = clazz;
        this.effects = effects;
    }

    public Class getClazz() {
        return clazz;
    }

    public List<Class> getEffects() {
        return effects;
    }
}
