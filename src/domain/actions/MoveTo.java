package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import domain.AbstractAction;
import domain.ObservationFactory;
import domain.fluents.Have;
import domain.fluents.IsAt;
import main.Entity;
import main.Observations;

import java.util.Arrays;

/**
 * Created by Mart on 8.10.2017.
 */
public class MoveTo extends AbstractAction {

    private final float z;
    private final float y;
    private final float x;
    private final float distance;

    public MoveTo(AgentHost agentHost, IsAt isAt){
        this(agentHost, isAt, null);
    }

    public MoveTo(AgentHost agentHost, IsAt isAt, Entity entity) {
        super(agentHost);
        this.x = isAt.getX();
        this.y = isAt.getY();
        this.z = isAt.getZ();
        this.distance = isAt.getDistance();
        this.effects.add(isAt);
        if(entity != null) {
            String item = GatherBlock.BLOCK_TO_ITEM.get(entity.name);
            this.effects.add(new Have(item, ObservationFactory.getObservations(agentHost).numberOf(item) + 1));
        }
    }


    public void doAction(Observations observations) {
        float xDifference = x - observations.XPos;
        float yDifference = y - observations.YPos;
        float zDifference = z - observations.ZPos;

        if (zDifference > distance) {
            agentHost.sendCommand("movesouth 1");
        } else if (zDifference < -distance) {
            agentHost.sendCommand("movenorth 1");
        }

        if (xDifference > distance) {
            agentHost.sendCommand("moveeast 1");
        } else if (xDifference < -distance) {
            agentHost.sendCommand("movewest 1");
        }
    }
    @Override
    public String toString(){
        return "MoveTo position : x = "+x+", y = "+y+", z = "+z+" within distance of "+distance;
    }
}
