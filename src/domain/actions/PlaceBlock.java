package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import domain.AbstractAction;
import domain.BlockType;
import main.ObservationFactory;
import domain.fluents.*;
import javafx.util.Pair;
import main.Observations;

import java.util.Arrays;

public class PlaceBlock extends AbstractAction {
    private final static int COST = 5;

    private final BlockAt mBlockAt;

    public PlaceBlock(AgentHost agentHost, BlockAt blockAt) {
        super(agentHost);
        float x = blockAt.getX();
        float y = blockAt.getY();
        float z = blockAt.getZ();
        mBlockAt = blockAt;

        Observations observations = ObservationFactory.getObservations(agentHost);
        String typeOfBlockString = blockAt.getTypeOfBlockString();
        Have have = new Have(typeOfBlockString, observations.numberOf(typeOfBlockString) - 1);
        effects.add(blockAt);
        effects.add(have);
        Pair<BlockAt, LookingAt> bestNearby = findBestNearbyBlock(x, y, z, observations);

        this.preconditions = Arrays.asList(
                new Have(typeOfBlockString, 1),
                new HaveSelected(typeOfBlockString),
                bestNearby.getKey(), bestNearby.getValue(),
                //  new HaveLineOfSight(bestNearby.getX(), bestNearby.getY(), bestNearby.getZ()),
                new IsAt(x, y, z, 1),
                new BlockAt(x, y, z, BlockType.air)
        );
    }

    public int cost() {
        Observations obs = ObservationFactory.getObservations(agentHost);
        return (int) (Math.abs(mBlockAt.getX() - obs.XPos) + Math.abs(mBlockAt.getZ() - obs.ZPos));
    }

    private Pair<BlockAt, LookingAt> findBestNearbyBlock(float x, float y, float z, Observations observations) {
        float xPos = observations.XPos;
        float zPos = observations.ZPos;
        float xDis = x - xPos;
        float zDis = z - zPos;
        float xAbsDis = Math.abs(xDis);
        float zAbsDis = Math.abs(zDis);
        boolean isXBigger = xAbsDis >= zAbsDis;

        BlockAt output1 = new BlockAt(isXBigger ? x + Math.signum(xDis) : x, y, isXBigger ? z : z + Math.signum(zDis), BlockType.Any);
        if (output1.test(observations)) return new Pair<>(output1, new LookingAt(isXBigger ? x + Math.signum(xDis) / 2 : x, y, isXBigger ? z : z + Math.signum(zDis) / 2));

        output1 = new BlockAt(!isXBigger ? x + Math.signum(xDis) : x, y, !isXBigger ? z : z + Math.signum(zDis), BlockType.Any);
        if (output1.test(observations)) return new Pair<>(output1, new LookingAt(!isXBigger ? x + Math.signum(xDis) / 2 : x, y, !isXBigger ? z : z + Math.signum(zDis) / 2));

        BlockAt output0 = new BlockAt(x, y - 1, z, BlockType.Any);
        if (output0.test(observations)) return new Pair<>(output0, new LookingAt(x, y - 0.5f, z));

        output1 = new BlockAt(!isXBigger ? x - Math.signum(xDis) : x, y, !isXBigger ? z : z - Math.signum(zDis), BlockType.Any);
        if (output1.test(observations)) return new Pair<>(output1, new LookingAt(!isXBigger ? x - Math.signum(xDis) / 2 : x, y, !isXBigger ? z : z - Math.signum(zDis) / 2));

        output1 = new BlockAt(isXBigger ? x - Math.signum(xDis) : x, y, isXBigger ? z : z - Math.signum(zDis), BlockType.Any);
        if (output1.test(observations)) return new Pair<>(output1, new LookingAt(isXBigger ? x - Math.signum(xDis) / 2 : x, y, isXBigger ? z : z - Math.signum(zDis) / 2));

        else return new Pair<>(output0, new LookingAt(x, y - 0.5f, z));
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
