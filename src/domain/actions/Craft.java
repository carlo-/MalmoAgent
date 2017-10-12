package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import domain.AbstractAction;
import domain.AtomicFluent;
import domain.ObservationFactory;
import domain.fluents.HasNumberOfItem;
import javafx.util.Pair;
import main.Observations;

import java.util.*;

public class Craft extends AbstractAction {
    public final static Map<String, Pair<Integer, List<Pair<String, Integer>>>> CRAFTS;//name of item -> (numberOfInstanceOfTheItemYouWillGet, list of ingredients and # times needed
    static {
        Map<String, Pair<Integer, List<Pair<String, Integer>>>> crafts = new HashMap<>();
        crafts.put("diamond_pixaxe", new Pair<>(1, Collections.unmodifiableList(Arrays.asList(new Pair<>("diamond", 3), new Pair<>("stick", 2)))));
        crafts.put("oak_wood_planks", new Pair<>(4, Collections.unmodifiableList(Arrays.asList(new Pair<>("oak_wood", 1)))));
        //TODO fill the necessary crafts
        CRAFTS = Collections.unmodifiableMap(crafts);
    }

    private final String mItem;

    public Craft(AgentHost agentHost, String item) {
        super(agentHost);
        mItem = item;
        if (!CRAFTS.containsKey(item))
            throw new IllegalArgumentException("The item " + item + " cannot be crafted! (or isn't in the list of crafts");
        String name;
        int quantity;
        for(Pair<String, Integer> pair : CRAFTS.get(item).getValue()){
            name = pair.getKey();
            quantity = pair.getValue();
            this.preconditions.add(new HasNumberOfItem(name, quantity));
            this.effects.add(new HasNumberOfItem(name, ObservationFactory.getObservations(agentHost).numberOf(name) - quantity));
        }
        this.effects.add(new HasNumberOfItem(item, ObservationFactory.getObservations(agentHost).numberOf(item) + CRAFTS.get(item).getKey()));
    }

    @Override
    public void doAction(Observations observations) {
        agentHost.sendCommand("craft "+mItem);
    }
}
