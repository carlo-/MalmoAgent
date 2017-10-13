package domain;

import com.microsoft.msr.malmo.AgentHost;
import domain.actions.*;
import domain.fluents.*;
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
        ArrayList<Action> possibleActions = new ArrayList<>();
        if (fluent instanceof IsAt) {
            possibleActions.add(new MoveTo(agentHost, (IsAt) fluent));
        } else if (fluent instanceof BlockAt) {
            BlockAt blockAt = (BlockAt) fluent;
            if (BlockType.air.equals(blockAt.getTypeOfBlock())) {
                possibleActions.add(new GatherBlock(agentHost, blockAt));
            } else {
                possibleActions.add(createPlaceBlockAction(blockAt));
            }
        } else if (fluent instanceof LookingAt) {
            possibleActions.add(new LookAt(agentHost, (LookingAt) fluent));
        } else if (fluent instanceof HaveSelected) {
            possibleActions.add(createSelectItemAction((HaveSelected) fluent));
        } else if (fluent instanceof Have) {
            Have have = (Have) fluent;
            if (!have.getItem().equals("air")) {
                possibleActions.addAll(createGatherOrCraftActions(have));
                possibleActions.addAll(createEntityMove(have));
            }
        }
        return possibleActions;
    }

    private List<Action> createEntityMove(Have have) {
        Observations observations = ObservationFactory.getObservations(agentHost);
        return observations.Entities.stream()
                .filter(entity -> entity.name != null && have.getItem().equals(entity.name.name()))
                .map(entity -> new MoveTo(agentHost, new IsAt(((int) entity.x) + 0.5f, ((int) entity.y) + 0.5f, ((int) entity.z) + 0.5f), entity))
                .collect(Collectors.toList());


    }

    private SelectItem createSelectItemAction(HaveSelected item) {
        return new SelectItem(agentHost, item);
    }

    private List<Action> createGatherOrCraftActions(Have nbItems) {
        String item = nbItems.getItem();
        //Check if the item is craftable, otherwise try to gather it
        if (Craft.CRAFTS.containsKey(item))
            return Arrays.asList(new Craft(agentHost, item));
        else {
            Observations obs = ObservationFactory.getObservations(agentHost);
            return obs.findBlockType(GatherBlock.ITEM_TO_BLOCK.get(nbItems.getItem()))
                    .stream()
                    .map(blockAt -> new GatherBlock(agentHost, blockAt))
                    .collect(Collectors.toList());
        }
    }


    private PlaceBlock createPlaceBlockAction(BlockAt blockAt) {
        return new PlaceBlock(agentHost, blockAt);
    }
}
