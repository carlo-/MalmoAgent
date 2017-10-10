package domain;

import com.microsoft.msr.malmo.AgentHost;
import domain.actions.LookAt;
import domain.actions.MoveTo;
import domain.actions.PlaceBlock;
import domain.fluents.BlockAt;
import domain.fluents.IsAt;
import domain.fluents.LookingAt;

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
            IsAt isAt = (IsAt) fluent;
            return Arrays.asList(createMoveToAction(isAt));
        } else if (fluent instanceof BlockAt) {
            BlockAt blockAt = (BlockAt) fluent;
            return Arrays.asList(createPlaceBlockAction(blockAt));
        } else if (fluent instanceof LookingAt) {
            LookingAt lookingAt = (LookingAt) fluent;
            return Arrays.asList(createLookAtAction(lookingAt));
        }
        return Arrays.asList();
    }

    private LookAt createLookAtAction(LookingAt lookingAt) {
        return new LookAt(agentHost,lookingAt);
    }

    private PlaceBlock createPlaceBlockAction(BlockAt blockAt) {
        return new PlaceBlock(agentHost, blockAt);
    }
}
