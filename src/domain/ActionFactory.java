package domain;

import com.microsoft.msr.malmo.AgentHost;
import domain.actions.MoveTo;
import domain.actions.Stop;
import domain.fluents.IsAt;

/**
 * Created by Mart on 8.10.2017.
 */
public class ActionFactory {

    protected final AgentHost agentHost;

    public ActionFactory(AgentHost agentHost) {
        this.agentHost = agentHost;
    }

    public MoveTo createMoveToAction(IsAt isAt) {
        return new MoveTo(agentHost, isAt);
    }

    public Stop createStop() {
        return new Stop(agentHost);
    }
}
