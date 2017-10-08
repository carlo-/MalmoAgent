package domain;

import com.microsoft.msr.malmo.AgentHost;
import domain.actions.MoveTo;

/**
 * Created by Mart on 8.10.2017.
 */
public class ActionFactory {

    protected final AgentHost agentHost;

    public ActionFactory(AgentHost agentHost) {
        this.agentHost = agentHost;
    }

    public MoveTo createMoveToAction(float x, float y, float z) {
        return new MoveTo(agentHost, x, y, z);
    }
}
