package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import com.microsoft.msr.malmo.TimestampedStringVector;
import domain.AbstractAction;
import domain.fluents.IsAt;
import main.Observations;

import java.util.Arrays;

/**
 * Created by Mart on 8.10.2017.
 */
public class MoveTo extends AbstractAction {

    private final float z;
    private final float y;
    private final float x;

    public MoveTo(AgentHost agentHost, IsAt isAt) {
        super(agentHost);
        this.x = isAt.getX();
        this.y = isAt.getY();
        this.z = isAt.getZ();
        this.effects = Arrays.asList(isAt);
    }

    @Override
    public void perform() {
        Observations observations = null;
        do {
            TimestampedStringVector obs = agentHost.getWorldState().getObservations();
            if (obs.size() > 0) {
                observations = builder.create().fromJson(obs.get(0).getText(), Observations.class);

                float xDifference = x - observations.XPos;
                float yDifference = y - observations.YPos;
                float zDifference = z - observations.ZPos;

                if (zDifference > 0) {
                    agentHost.sendCommand("movesouth 1");
                } else if (zDifference < 0) {
                    agentHost.sendCommand("movenorth 1");
                }

                if (xDifference > 0) {
                    agentHost.sendCommand("moveeast 1");
                } else if (xDifference < 0) {
                    agentHost.sendCommand("movewest 1");
                }
            }
        } while (!effectsCompleted(observations));
    }

//        float yawDifference = (360 - targetYaw) - observations.Yaw;
    // float targetYaw = (float) Math.toDegrees(Math.atan((double) (zDifference / xDifference)));
//        if (xDifference > 0 && zDifference < 0) {
//            targetYaw = 360 - targetYaw;
//        }
//        if (xDifference < 0 && zDifference > 0) {
//            targetYaw = 180 + targetYaw;
//        }
//        if (xDifference < 0 && zDifference < 0) {
//            targetYaw = 180 + targetYaw;
//        }

//} else {
//            if (Math.abs(xDifference) > 1 || Math.abs(zDifference) > 1)
//                agentHost.sendCommand("move 1");
//            else
//                agentHost.sendCommand("move 0");
//
//            if (yawDifference > 3) {
//                agentHost.sendCommand("turn 0.5");
//            } else if (yawDifference < 3) {
//                agentHost.sendCommand("turn -0.5");
//            } else {
//                agentHost.sendCommand("turn 0");
//            }
//        }
}
