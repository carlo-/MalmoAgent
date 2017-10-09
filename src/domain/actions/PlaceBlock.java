package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import domain.AbstractAction;
import domain.fluents.BlockAt;
import domain.fluents.CanPlaceBlock;
import main.Observations;

import java.util.Arrays;

public class PlaceBlock extends AbstractAction {

    private final BlockAt mBlockAt;

    public PlaceBlock(AgentHost agentHost, BlockAt blockAt) {
        super(agentHost);
        mBlockAt = blockAt;
        this.effects = Arrays.asList(blockAt);
        float x = blockAt.getX();
        float y = blockAt.getY();
        float z = blockAt.getZ();
        this.preconditions = Arrays.asList(new CanPlaceBlock(x,y,z)/*, HaveNumberOf(mBlockAt.getTypeOfBlock(), 1), hasItemInHand(mBlockAt.getTypeOfBlock())*/);//waiting for skeleton to implement
    }


    @Override
    protected void doAction(Observations observations) {
        agentHost.sendCommand("use 1");
    }
}
