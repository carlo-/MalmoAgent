package domain;

import com.microsoft.msr.malmo.AgentHost;
import domain.actions.*;
import domain.fluents.*;
import main.Observations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//This class is simply a mapping from Effect to Action. We use this in order to sneak past the fact that everything is an object and we can't currently instantiate without
//A constructor. The purpose is of this class is to provide a list of all possible actions that fulfill through their effects, a given Fluent.
public class ActionFactory {

    protected final AgentHost agentHost;

    public ActionFactory(AgentHost agentHost) {
        this.agentHost = agentHost;
    }

    public List<Action> createPossibleActions(AtomicFluent fluent, Observations observations) {
        ArrayList<Action> possibleActions = new ArrayList<>();
        if (fluent instanceof IsAt) {
            possibleActions.add(new MoveTo(agentHost, (IsAt) fluent));
        } else if (fluent instanceof BlockAt) {
            BlockAt blockAt = (BlockAt) fluent;
            if (BlockType.air.equals(blockAt.getTypeOfBlock()) && observations.blockAt(blockAt.getX(), blockAt.getY(), blockAt.getZ()).getTypeOfBlock().equals(BlockType.air)) {
                possibleActions.add(new GatherBlock(agentHost, blockAt));
            } else if (BlockType.Any.equals(blockAt.getTypeOfBlock())) {
                // Handles weird case, ask Nicolas!
                possibleActions.add(new PlaceBlock(agentHost, new BlockAt(blockAt.getX(), blockAt.getY(), blockAt.getZ(), BlockType.planks)));
            } else {
                possibleActions.add(new PlaceBlock(agentHost, blockAt));
            }
        } else if (fluent instanceof LookingAt) {
            possibleActions.add(new LookAt(agentHost, (LookingAt) fluent));
        } else if (fluent instanceof HaveSelected) {
            possibleActions.add(new SelectItem(agentHost, (HaveSelected) fluent));
        } else if (fluent instanceof Have) {
            Have have = (Have) fluent;
            if (!have.getItem().equals("air")) {
                possibleActions.addAll(createGatherOrCraftActions(have, observations));
                possibleActions.addAll(createEntityMove(have, observations));
            }
        }
        return possibleActions;
    }

    private List<Action> createEntityMove(Have have, Observations observations) {
        return observations.Entities.stream()
                .filter(entity -> entity.name != null && have.getItem().equals(entity.name.name()))
                .map(entity -> new MoveTo(agentHost, new IsAt(((int) entity.x) + 0.5f, ((int) entity.y) + 0.5f, ((int) entity.z) + 0.5f), entity))
                .collect(Collectors.toList());
    }

    private List<Action> createGatherOrCraftActions(Have nbItems, Observations observations) {
        String item = nbItems.getItem();
        //Check if the item is craftable, otherwise try to gather it. Two ways to have something.
        if (Craft.CRAFTS.containsKey(item))
            return Arrays.asList(new Craft(agentHost, item));
        else {

            return observations.findBlockType(GatherBlock.ITEM_TO_BLOCK.get(nbItems.getItem()))
                    .stream()
                    .map(blockAt -> new GatherBlock(agentHost, blockAt))
                    .collect(Collectors.toList());
        }
    }
}
