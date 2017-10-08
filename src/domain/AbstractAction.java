package domain;

import com.microsoft.msr.malmo.AgentHost;
import domain.fluents.IsAt;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Mart on 8.10.2017.
 */
public abstract class AbstractAction implements Action {
    protected final AgentHost agentHost;
    protected List<AtomicFluent> effects;
    protected List<AtomicFluent> preconditions;

    public AbstractAction(AgentHost agentHost) {
        this.agentHost = agentHost;
        this.effects = Arrays.asList();
        this.preconditions = Arrays.asList();
    }

    @Override
    public List<AtomicFluent> getPreconditions() {
        return preconditions;
    }

    @Override
    public List<AtomicFluent> getEffects() {
        return effects;
    }

}
