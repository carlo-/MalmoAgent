package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import domain.AbstractAction;
import domain.BlockType;
import domain.ObservationFactory;
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
        BlockAt bestNearby = findBestNearbyBlock(x, y, z, ObservationFactory.getObservations(agentHost));

        this.preconditions = Arrays.asList(
                new Have(blockAt.getTypeOfBlockString(), 1),
                new HaveSelected(blockAt.getTypeOfBlockString()),
                bestNearby,
              //  new HaveLineOfSight(bestNearby.getX(), bestNearby.getY(), bestNearby.getZ()),
                new IsAt(x, y, z, 1),
                new LookingAt(bestNearby.getX(), bestNearby.getY(), bestNearby.getZ()),
                new BlockAt(x, y, z, BlockType.air)
        );
    }

    private BlockAt findBestNearbyBlock(float x, float y, float z, Observations observations) {
        float xPos = observations.XPos;
        float zPos = observations.ZPos;
        float xDis = x - xPos;
        float zDis = z - zPos;
        float xAbsDis = Math.abs(xDis);
        float zAbsDis = Math.abs(zDis);
        boolean isXBigger = xAbsDis >= zAbsDis;

        BlockAt output1 = new BlockAt(isXBigger ? x + Math.signum(xDis) : x, y, isXBigger ? z : z + Math.signum(zDis), BlockType.Any);
        if (output1.test(observations)) return output1;

        output1 = new BlockAt(!isXBigger ? x + Math.signum(xDis) : x, y, !isXBigger ? z : z + Math.signum(zDis), BlockType.Any);
        if (output1.test(observations)) return output1;

        BlockAt output0 = new BlockAt(x, y - 1, z, BlockType.Any);
        if (output0.test(observations)) return output0;

        output1 = new BlockAt(!isXBigger ? x - Math.signum(xDis) : x, y, !isXBigger ? z : z - Math.signum(zDis), BlockType.Any);
        if (output1.test(observations)) return output1;

        output1 = new BlockAt(isXBigger ? x - Math.signum(xDis) : x, y, isXBigger ? z : z - Math.signum(zDis), BlockType.Any);
        if (output1.test(observations)) return output1;

        else return output0;
    }

    @Override
    public void doAction(Observations observations) {
        agentHost.sendCommand("use 1");
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        agentHost.sendCommand("use 0");

    }

    @Override
    public String toString() {
        return "PlaceBlock " + mBlockAt.getTypeOfBlockString() + " at position: x = " + mBlockAt.getX() + ", y = " + mBlockAt.getY() + ", z = " + mBlockAt.getZ();
    }
}
