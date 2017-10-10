package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import domain.AbstractAction;
import domain.BlockType;
import domain.fluents.BlockAt;
import main.Observations;

import java.util.List;

import static main.JavaAgent.*;

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
            BlockAt position = findBlock(observations.XPos, observations.YPos, observations.ZPos, observations.CellObs);
    }

    private BlockAt findBlock(float xPos, float yPos, float zPos, List<String> cellObs) {
        //TODO special case any
        int xRelative = 0;
        int yRelative = 0;
        int zRelative = 0;
        int i = 0;
        for (String block : cellObs) {
            if (targetBlock.compareTo(BlockType.valueOf(block)) == 0) {
                // calculate position in the grid
                xRelative = i % xObservationSize;
                yRelative = (i % (xObservationSize * yObservationSize)) / xObservationSize;
                zRelative = i / (xObservationSize * yObservationSize);
                // calculate position relative to us
                xRelative += X_START_OBSERVATION + 1;
                yRelative += Y_START_OBSERVATION;
                zRelative += Z_START_OBSERVATION + 1;
                break;
                //TODO maybe find nearest Block isntead?
            }
            i++;
        }
        if (xRelative == yRelative && yRelative == zRelative && zRelative == 0){
            // no blocks of given type found
            return null;
        }
        BlockAt blockAt = new BlockAt(xPos + xRelative, yPos + yRelative, zPos + zRelative, targetBlock);
        return blockAt;
    }
}
