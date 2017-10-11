package domain;

import com.google.gson.GsonBuilder;
import com.microsoft.msr.malmo.AgentHost;
import com.microsoft.msr.malmo.TimestampedStringVector;
import domain.fluents.IsAt;
import javafx.util.Pair;
import main.JavaAgent;
import main.Observations;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Mart on 8.10.2017.
 */
public abstract class AbstractAction implements Action {
    protected final AgentHost agentHost;
    protected List<AtomicFluent> effects;
    protected List<AtomicFluent> preconditions;
    protected GsonBuilder builder = new GsonBuilder();

    public AbstractAction(AgentHost agentHost) {
        this.agentHost = agentHost;
        this.effects = Arrays.asList();
        this.preconditions = Arrays.asList();
    }

    @Override
    public List<AtomicFluent> getPreconditions() {
        return preconditions;
    }

    @Override
    public List<AtomicFluent> getEffects() {
        return effects;
    }

    public boolean preconditionsMet(){
        return preconditions.size() == 0 || preconditions.stream().allMatch(predicate->predicate.test(getObservations()));
    }

    public boolean effectsCompleted() {
        Observations observations = getObservations();
        return observations != null && effects.stream().allMatch(predicate -> predicate.test(observations));
    }

    @Override
    public void perform() {
        Observations observations = null;
        while (!effectsCompleted()) {
            observations = getObservations();
            doAction(observations);
        }
    }

    public Observations getObservations() {
        Observations observations = null;
        do {
            TimestampedStringVector obs = agentHost.getWorldState().getObservations();
            if (obs.size() > 0) {
                String text = obs.get(0).getText();
                observations = builder.create().fromJson(obs.get(0).getText(), Observations.class);
                Pair<List<Integer>, List<String>> x = JavaAgent.JSONToLists(text);
                observations.items = x.getValue();
                observations.nbItems = x.getKey();
            }
        } while (observations == null);
        return observations;
    }


    protected abstract void doAction(Observations observations);
}
