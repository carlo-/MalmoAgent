package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import domain.AbstractAction;
import domain.BlockType;
import domain.fluents.BlockAt;
import domain.fluents.IsAt;
import main.Observations;

import java.util.List;

import static main.JavaAgent.*;


//TODO: Might be ok to remove this now  that it s moved to Observation?
public class FindBlock extends AbstractAction {

    private final BlockType targetBlock;
    private final int xObservationSize = Math.abs(X_START_OBSERVATION - X_STOP_OBSERVATION) + 1;
    private final int yObservationSize = Math.abs(Y_START_OBSERVATION - Y_STOP_OBSERVATION) + 1;
    private final int zObservationSize = Math.abs(Z_START_OBSERVATION - Z_STOP_OBSERVATION) + 1;
    private BlockType[][][] blocks = new BlockType[xObservationSize][yObservationSize][zObservationSize];

    public FindBlock(AgentHost agentHost, BlockType blockType) {
        super(agentHost);
        targetBlock = blockType;
    }

    @Override
    public void doAction(Observations observations) {
        BlockAt dirt = findClosestBlockOf(observations.XPos, observations.YPos, observations.ZPos, observations.CellObs);
    }

    private void parseBlockMatrix(List<String> cellObs) {
        int i = 0;
        for (String blockType : cellObs) {
            blocks[i % xObservationSize][(i % (xObservationSize * yObservationSize)) / xObservationSize]
                    [i / (xObservationSize * yObservationSize)] = BlockType.valueOf(blockType);
            i++;
        }
    }

    private BlockAt findClosestBlockOf(float xPos, float yPos, float zPos, List<String> cellObs) {
        System.out.println(xPos + "  " + yPos + "  " + zPos);
        int xRelative = Integer.MAX_VALUE;
        int yRelative = Integer.MAX_VALUE;
        int zRelative = Integer.MAX_VALUE;
        int distance = Integer.MAX_VALUE;
        int i = 0;
        for (String block : cellObs) {
            if (targetBlock.compareTo(BlockType.valueOf(block)) == 0) {
                // calculate position in the grid
                int nextXRelative = i % xObservationSize;
                int nextYRelative = (i % (xObservationSize * yObservationSize)) / xObservationSize;
                int nextZRelative = i / (xObservationSize * yObservationSize);
                // calculate position relative to us
                nextXRelative += X_START_OBSERVATION;
                nextYRelative += Y_START_OBSERVATION;
                nextZRelative += Z_START_OBSERVATION;
                // calculate distance to us
                int nextDistance = nextXRelative * nextXRelative + nextYRelative * nextYRelative + nextZRelative * nextZRelative;
                // check if block is closer than previous best result
                if (nextDistance < distance) {
                    xRelative = nextXRelative;
                    yRelative = nextYRelative;
                    zRelative = nextZRelative;
                    distance = nextDistance;
                }
            }
            i++;
        }
        if (xRelative == yRelative && yRelative == zRelative && zRelative == Integer.MAX_VALUE){
            // no blocks of given type found
            return null;
        }
        // return absolute position
        BlockAt blockAt = new BlockAt(xPos + xRelative, yPos + yRelative, zPos + zRelative, targetBlock);
        return blockAt;
    }
}
