package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import domain.AbstractAction;
import domain.fluents.HaveSelected;
import domain.fluents.Have;
import main.Observations;

import java.util.Arrays;

public class SelectItem extends AbstractAction {
    private final String mItem;

    public SelectItem(AgentHost agentHost, String item) {
        super(agentHost);
        preconditions = Arrays.asList(new Have(item, 1));
        mItem = item;
    }

    public SelectItem(AgentHost agentHost, HaveSelected haveSelected){
        this(agentHost, haveSelected.getItem());
    }

    @Override
    public void doAction(Observations observations) {
        agentHost.sendCommand("swapInventoryItems 0 "+observations.items.indexOf(mItem));
    }
}
