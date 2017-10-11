package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import domain.AbstractAction;
import domain.BlockType;
import domain.fluents.*;
import main.Observations;

import java.util.Arrays;

public class PlaceBlock extends AbstractAction {

    private final BlockAt mBlockAt;

    public PlaceBlock(AgentHost agentHost, BlockAt blockAt) {
        super(agentHost);
        float x = blockAt.getX();
        float y = blockAt.getY();
        float z = blockAt.getZ();
        mBlockAt = blockAt;
        this.effects = Arrays.asList(blockAt);
        BlockAt bestNearby = findBestNearbyBlock(x, y, z);
        this.preconditions = Arrays.asList(bestNearby, new IsLineOfSightFree(bestNearby.getX(), bestNearby.getY(), bestNearby.getZ()),
                new IsAt(x, y, z, 1),
                new IsAt(x, y ,z, 0).negate(),
                new BlockAt(x, y, z, BlockType.Any),
                new HasNumberOfItem(blockAt.getTypeOfBlockString(), 1),
                new HasItemSelected(blockAt.getTypeOfBlockString()));
    }

    private BlockAt findBestNearbyBlock(float x, float y, float z){
        float xPos = getObservations().XPos;
        float zPos = getObservations().ZPos;
        float xDis = x - xPos;
        float zDis = z - zPos;
        float xAbsDis = Math.abs(xDis);
        float zAbsDis = Math.abs(zDis);
        boolean isXBigger = xAbsDis >= zAbsDis;
        BlockAt output0 = new BlockAt(isXBigger ? x + Math.signum(xDis) : x, y, isXBigger ? z : z + Math.signum(zDis), BlockType.Any);
        if(output0.test(getObservations())) return output0;
        BlockAt output1 = new BlockAt(!isXBigger ? x + Math.signum(xDis) : x, y, !isXBigger ? z : z + Math.signum(zDis), BlockType.Any);
        if(output1.test(getObservations())) return output1;
        output1 = new BlockAt(x, y - 1, z, BlockType.Any);
        if(output1.test(getObservations())) return output1;
        output1 = new BlockAt(!isXBigger ? x - Math.signum(xDis) : x, y, !isXBigger ? z : z - Math.signum(zDis), BlockType.Any);
        if(output1.test(getObservations())) return output1;
        output1 = new BlockAt(isXBigger ? x - Math.signum(xDis) : x, y, isXBigger ? z : z - Math.signum(zDis), BlockType.Any);
        if(output1.test(getObservations())) return output1;
        else return output0;
    }

    @Override
    public void doAction(Observations observations) {
        agentHost.sendCommand("use 1");
    }
}
