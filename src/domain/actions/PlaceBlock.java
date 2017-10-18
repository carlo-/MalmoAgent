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
    private boolean needsToJump = false;

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

        float placeDistance = 1;
        LookingAt lookingAt = bestNearby.getValue();

        if (bestNearby.getKey().getY() == observations.YPos) {
            needsToJump = true;
            // Look down instead of up (because we'll jump)
            lookingAt = new LookingAt(lookingAt.getX(), lookingAt.getY()-1.5f, lookingAt.getZ());

        } else if (bestNearby.getKey().getY() > observations.YPos) {
            // We don't need to jump, but we need to be exactly below the block we want to place
            placeDistance = 0;
        }

        this.preconditions = Arrays.asList(
                new Have(typeOfBlockString, 1),
                new HaveSelected(typeOfBlockString),
                bestNearby.getKey(),
                lookingAt,
                new IsAt(x, y, z, placeDistance, true), // Distance must be exact in this case (not less or equal)
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

        if (needsToJump) {
            // We jump and keep trying to place
            agentHost.sendCommand("jump 1");
            agentHost.sendCommand("use 1");

        } else {

            agentHost.sendCommand("jump 0"); // Probably unnecessary, should be handled correctly by finalizeAction()
            agentHost.sendCommand("use 1");
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            agentHost.sendCommand("use 0");
        }
    }

    @Override
    public void finalizeAction() {

        if (needsToJump) {
            // We were jumping, so we need to stop
            agentHost.sendCommand("use 0");
            agentHost.sendCommand("jump 0");
            try {
                // Time required to fall down (overestimate just to be sure)
                // If this is too small, the planner will throw an IllegalStateException!
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return "PlaceBlock " + mBlockAt.getTypeOfBlockString() + " at position: x = " + mBlockAt.getX() + ", y = " + mBlockAt.getY() + ", z = " + mBlockAt.getZ();
    }
}
