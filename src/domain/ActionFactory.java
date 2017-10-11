package domain;

import com.microsoft.msr.malmo.AgentHost;
import domain.actions.*;
import domain.fluents.*;

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

    public FindBlock createFindBlockAction(BlockType blockType) {
        return new FindBlock(agentHost, blockType);
    }

    public List<Action> createPossibleActions(AtomicFluent fluent) {
        if (fluent instanceof IsAt) {
            return Arrays.asList(createMoveToAction((IsAt) fluent));
        } else if (fluent instanceof BlockAt) {
            return Arrays.asList(createPlaceBlockAction((BlockAt) fluent));
        } else if (fluent instanceof LookingAt) {
            return Arrays.asList(createLookAtAction((LookingAt) fluent));
        } else if (fluent instanceof HasItemSelected) {
            return Arrays.asList(createSelectItemAction((HasItemSelected) fluent));
        } else if (fluent instanceof HasNumberOfItem){
            return Arrays.asList(createGatherOrCraftAction((HasNumberOfItem) fluent));
        }
        return Arrays.asList();
    }

    private SelectItem createSelectItemAction(HasItemSelected item) {
        return new SelectItem(agentHost, item);
    }

    private AbstractAction createGatherOrCraftAction(HasNumberOfItem nbItems){
        String item = nbItems.getItem();
        //Check if the item is craftable, otherwise try to gather it
        if(Craft.CRAFTS.containsKey(item))return new Craft(agentHost, item);
        else return null; //TODO will be a gather action
    }

    private LookAt createLookAtAction(LookingAt lookingAt) {
        return new LookAt(agentHost, lookingAt);
    }

    private PlaceBlock createPlaceBlockAction(BlockAt blockAt) {
        return new PlaceBlock(agentHost, blockAt);
    }
}
