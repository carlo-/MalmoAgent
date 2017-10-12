package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import domain.AbstractAction;
import domain.ObservationFactory;
import domain.fluents.Have;
import javafx.util.Pair;
import main.Observations;

import java.util.*;

public class Craft extends AbstractAction {
    public final static Map<String, Pair<Integer, List<Pair<String, Integer>>>> CRAFTS;//name of item -> (numberOfInstanceOfTheItemYouWillGet, list of ingredients and # times needed

    static {
        Map<String, Pair<Integer, List<Pair<String, Integer>>>> crafts = new HashMap<>();
        crafts.put("diamond_pickaxe", new Pair<>(1, Collections.unmodifiableList(Arrays.asList(new Pair<>("diamond", 3), new Pair<>("stick", 2)))));
        crafts.put("planks", new Pair<>(4, Collections.unmodifiableList(Arrays.asList(new Pair<>("log", 1)))));
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
        Observations obs = ObservationFactory.getObservations(agentHost);
        for (Pair<String, Integer> pair : CRAFTS.get(item).getValue()) {
            name = pair.getKey();
            quantity = pair.getValue();
            this.preconditions.add(new Have(name, quantity));
            this.effects.add(new Have(name, obs.numberOf(name) - quantity));
        }
        this.effects.add(new Have(item, obs.numberOf(item) + CRAFTS.get(item).getKey()));
    }

    @Override
    public void doAction(Observations observations) {
        agentHost.sendCommand("craft " + mItem);
    }

    @Override
    public String toString(){
        return "Craft item "+mItem;
    }
}
