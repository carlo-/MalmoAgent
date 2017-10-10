package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import domain.AbstractAction;
import domain.fluents.HasNumberOfItem;
import main.Observations;

public class SelectItem extends AbstractAction {
    private final String mItem;

    public SelectItem(AgentHost agentHost, String item) {
        super(agentHost);
        preconditions.add(new HasNumberOfItem(item, 1));
        mItem = item;
    }

    @Override
    public void doAction(Observations observations) {
        String item = observations.items.get(0);
        if (!item.equals(mItem)) agentHost.sendCommand("swapInventoryItems "+mItem+" "+item);
    }
}
