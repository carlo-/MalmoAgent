package domain;

import com.microsoft.msr.malmo.AgentHost;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Mart on 8.10.2017.
 */
public abstract class AbstractAction implements Action {
    protected final AgentHost agentHost;
    protected List<Atomic> effects;
    protected List<Atomic> preconditions;

    public AbstractAction(AgentHost agentHost) {
        this.agentHost = agentHost;
    }
}
