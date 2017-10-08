package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import domain.AbstractAction;
import main.Observations;

/**
 * Created by Mart on 8.10.2017.
 */
public class Stop extends AbstractAction {
    public Stop(AgentHost agentHost) {
        super(agentHost);
    }

    public void accept(Observations observations) {
        agentHost.sendCommand("move 0");
        agentHost.sendCommand("strafe 0");
        agentHost.sendCommand("turn 0");
    }
}
