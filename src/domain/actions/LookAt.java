package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import domain.AbstractAction;
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
    public void doAction(Observations observations) {

        float xRel = x - observations.XPos;
        float yRel = y - observations.YPos;
        float zRel = z - observations.ZPos;

        float d = (float) Math.sqrt(Math.pow(xRel, 2) + Math.pow(zRel, 2) + Math.pow(yRel, 2));

        float d_xz = (float) Math.sqrt(Math.pow(xRel, 2) + Math.pow(zRel, 2));
        float phi = (float) Math.toDegrees(Math.acos(zRel / d_xz));
        if (xRel < 0 && zRel < 0) phi += (180 - phi) * 2;
        if (xRel < 0 && zRel > 0) phi = 360 - phi;
        phi = 360 - phi;

        // float d_yz = d; //(float)Math.sqrt(Math.pow(yRel, 2)+Math.pow(zRel, 2));
        float theta = (float) Math.toDegrees(-Math.asin(yRel / d));

        agentHost.sendCommand("setYaw " + phi);
        agentHost.sendCommand("setPitch " + theta);
    }
    @Override
    public String toString(){
        return "LookAt position : x = "+x+", y = "+y+", z = "+z;
    }
}
