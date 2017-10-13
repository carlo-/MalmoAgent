package domain;

import com.microsoft.msr.malmo.AgentHost;
import domain.actions.*;
import domain.fluents.*;
import main.Entity;
import main.Observations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mart on 8.10.2017.
 */
public class ActionFactory {

    protected final AgentHost agentHost;

    public ActionFactory(AgentHost agentHost) {
        this.agentHost = agentHost;
    }

    public List<Action> createPossibleActions(AtomicFluent fluent) {
        if (fluent instanceof IsAt) {
            return Arrays.asList(new MoveTo(agentHost, (IsAt)fluent));
        } else if (fluent instanceof BlockAt) {
            return Arrays.asList(createPlaceBlockAction((BlockAt) fluent));
        } else if (fluent instanceof LookingAt) {
            return Arrays.asList(new LookAt(agentHost, (LookingAt) fluent));
        } else if (fluent instanceof HaveSelected) {
            return Arrays.asList(createSelectItemAction((HaveSelected) fluent));
        } else if (fluent instanceof Have) {
            ArrayList<Action> fluents = new ArrayList<>();
            Have have = (Have) fluent;
            fluents.add(createGatherOrCraftAction(have));
            Action entityMove = createEntityMove(have);
            if (entityMove != null) {
                fluents.add(entityMove);
            }
            return fluents;
        }
        return Arrays.asList();
    }

    private Action createEntityMove(Have have) {
        Observations observations = ObservationFactory.getObservations(agentHost);
        if (observations.Entities.size() > 1) {
            List<Entity> matching = observations.Entities.stream()
                    .filter(entity -> have.getItem().equals(entity.name))
                    .collect(Collectors.toList());
            if (matching.size() > 0) {
                Entity entity = matching.get(0);
                return new MoveTo(agentHost, new IsAt((int) entity.x + 0.5f, (int) entity.y + 0.5f, (int) entity.z + 0.5f));
            }
        }
        return null;
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
        //return blocks.get(0);
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

    }

    private PlaceBlock createPlaceBlockAction(BlockAt blockAt) {
        return new PlaceBlock(agentHost, blockAt);
    }
}
