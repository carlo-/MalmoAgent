package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import domain.AbstractAction;
import domain.BlockType;
import domain.ObservationFactory;
import domain.fluents.*;
import main.Observations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GatherBlock extends AbstractAction {
    public final static Map<BlockType, String> BLOCK_TO_ITEM;
    public static final Map<String, BlockType> ITEM_TO_BLOCK;

    static {
        Map<BlockType, String> temp = new HashMap<>();
        temp.put(BlockType.log, "log");
        temp.put(BlockType.stone, "cobblestone");
        BLOCK_TO_ITEM = Collections.unmodifiableMap(temp);
        Map<String, BlockType> temp1 = new HashMap<>();
        for(Map.Entry<BlockType, String> x : BLOCK_TO_ITEM.entrySet()){
            temp1.put(x.getValue(), x.getKey());
        }
        ITEM_TO_BLOCK = Collections.unmodifiableMap(temp1);
    }

    private BlockAt block;

    public GatherBlock(AgentHost agentHost, BlockAt targetBlock) {
        super(agentHost);
        block = targetBlock;
        float x = targetBlock.getX();
        float y = targetBlock.getY();
        float z = targetBlock.getZ();
        String tool = toolForTheJob(targetBlock.getTypeOfBlock());
        preconditions = Arrays.asList(targetBlock,
                new LookingAt(x, y, z),
                //new HaveLineOfSight(x, y, z),
                new IsAt(x, y, z, 1),
                new Have(tool, 1),
                new HaveSelected(tool));
        String item = BLOCK_TO_ITEM.get(targetBlock.getTypeOfBlock());
        effects = Arrays.asList(new BlockAt(x, y, z, BlockType.air),
                new Have(item, ObservationFactory.getObservations(agentHost).numberOf(item) + 1));
    }

    private static String toolForTheJob(BlockType blockType) {
        switch (blockType) {
            case log:
                return "diamond_axe";
            case planks:
                return "diamond_axe";
            case stone:
                return "diamond_pickaxe";
            case cobblestone:
                return "diamond_pickaxe";
        }
        return "diamond_sword";
    }

    @Override
    public void doAction(Observations observations) {
        agentHost.sendCommand("attack 1");
    }

    public boolean perform() {
        boolean perform = super.perform();
        agentHost.sendCommand("attack 0");
        return perform;
    }

    @Override
    public String toString() {
        return "Gathering blocktype: " + block.toString();
    }
}
