package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import domain.AbstractAction;
import domain.fluents.HasItemSelected;
import domain.fluents.HasNumberOfItem;
import main.Observations;

public class SelectItem extends AbstractAction {
    private final String mItem;

    public SelectItem(AgentHost agentHost, String item) {
        super(agentHost);
        preconditions.add(new HasNumberOfItem(item, 1));
        mItem = item;
    }

    public SelectItem(AgentHost agentHost, HasItemSelected hasItemSelected){
        this(agentHost, hasItemSelected.getItem());
    }

    @Override
    public void doAction(Observations observations) {
        agentHost.sendCommand("swapInventoryItems 0 "+observations.items.indexOf(mItem));
    }
}
