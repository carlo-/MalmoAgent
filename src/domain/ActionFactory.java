package domain;

import com.microsoft.msr.malmo.AgentHost;
import domain.actions.*;
import domain.fluents.*;
import main.Observations;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Mart on 8.10.2017.
 */
public class ActionFactory {

    protected final AgentHost agentHost;

    public ActionFactory(AgentHost agentHost) {
        this.agentHost = agentHost;
    }

    public MoveTo createMoveToAction(IsAt isAt) {
        return new MoveTo(agentHost, isAt);
    }

    public List<Action> createPossibleActions(AtomicFluent fluent) {
        if (fluent instanceof IsAt) {
            return Arrays.asList(createMoveToAction((IsAt) fluent));
        } else if (fluent instanceof BlockAt) {
            return Arrays.asList(createPlaceBlockAction((BlockAt) fluent));
        } else if (fluent instanceof LookingAt) {
            return Arrays.asList(createLookAtAction((LookingAt) fluent));
        } else if (fluent instanceof HaveSelected) {
            return Arrays.asList(createSelectItemAction((HaveSelected) fluent));
        } else if (fluent instanceof Have) {
            return Arrays.asList(createGatherOrCraftAction((Have) fluent));
        }
        return Arrays.asList();
    }

    private SelectItem createSelectItemAction(HaveSelected item) {
        return new SelectItem(agentHost, item);
    }

    private AbstractAction createGatherOrCraftAction(Have nbItems) {
        String item = nbItems.getItem();
        //Check if the item is craftable, otherwise try to gather it
        if (Craft.CRAFTS.containsKey(item))
            return new Craft(agentHost, item);
        else {
            Observations obs = ObservationFactory.getObservations(agentHost);
            BlockAt blockType = findClosest(obs.findBlockType(BlockType.log), obs);
            return new GatherBlock(agentHost, blockType);
        }
    }

    private BlockAt findClosest(List<BlockAt> blocks, Observations obs) {
        return blocks.get(0);
        /*
        float minD = Float.MAX_VALUE;
        BlockAt closest = null;
        for (BlockAt b : blocks) {
            float d = b.distanceFrom(obs.XPos, obs.YPos, obs.ZPos);
            if (d <= minD) {
                closest = b;
                minD = d;
            }
        }
        return closest;
        */
    }

    private LookAt createLookAtAction(LookingAt lookingAt) {
        return new LookAt(agentHost, lookingAt);
    }

    private PlaceBlock createPlaceBlockAction(BlockAt blockAt) {
        return new PlaceBlock(agentHost, blockAt);
    }
}
