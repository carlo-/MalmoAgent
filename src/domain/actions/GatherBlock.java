package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import domain.AbstractAction;
import domain.BlockType;
import domain.fluents.*;
import main.Observations;

import java.util.ArrayList;
import java.util.Arrays;

public class GatherBlock extends AbstractAction{

    public GatherBlock (AgentHost agentHost, BlockAt targetBlock) {
        super(agentHost);
        float x = targetBlock.getX();
        float y = targetBlock.getY();
        float z = targetBlock.getZ();
        String tool = toolForTheJob(targetBlock.getTypeOfBlock());
        preconditions = Arrays.asList(targetBlock,
                new LookingAt(x, y, z),
              //  new IsLineOfSightFree(x, y, z),
                new IsAt(x, y, z, 1),
                new HasNumberOfItem(tool, 1),
                new HasItemSelected(tool));
        effects = Arrays.asList(new BlockAt(x, y, z, BlockType.air));
    }

    @Override
    public void doAction (Observations observations) {
        agentHost.sendCommand("attack 1");
        agentHost.sendCommand("attack 0");
    }

    private String toolForTheJob(BlockType blockType) {
        switch (blockType) {
            case log:
                return "diamond_axe";
            case plank:
                return "diamond_axe";
            case stone:
                return "diamond_pickaxe";
            case cobblestone:
                return "diamond_pickaxe";
        }
        return "diamond_sword";
    }
}
