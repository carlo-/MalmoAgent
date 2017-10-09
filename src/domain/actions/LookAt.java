package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import domain.AbstractAction;
import domain.fluents.IsAt;
import domain.fluents.LookingAt;
import main.Observations;

import java.util.Arrays;

/**
 * Created by Carlo on 9.10.2017.
 */
public class LookAt extends AbstractAction {

    private final float z;
    private final float y;
    private final float x;

    private boolean discrete = true;

    public LookAt(AgentHost agentHost, LookingAt lookingAt) {
        super(agentHost);
        this.x = lookingAt.getX();
        this.y = lookingAt.getY();
        this.z = lookingAt.getZ();
        this.effects = Arrays.asList(lookingAt);
    }

    @Override
    public void perform() {
        // TODO: Implement
    }
}
