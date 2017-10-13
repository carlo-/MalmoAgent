package domain;

import com.microsoft.msr.malmo.AgentHost;
import main.Observations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mart on 8.10.2017.
 */
public abstract class AbstractAction implements Action {
    protected final AgentHost agentHost;
    protected List<AtomicFluent> effects;
    protected List<AtomicFluent> preconditions;

    public AbstractAction(AgentHost agentHost) {
        this.agentHost = agentHost;
        this.effects = new ArrayList<>();
        this.preconditions = new ArrayList<>();
    }

    @Override
    public List<AtomicFluent> getPreconditions() {
        return preconditions;
    }

    @Override
    public List<AtomicFluent> getEffects() {
        return effects;
    }

    public boolean preconditionsMet() {
        return preconditions.size() == 0 || preconditions.stream().allMatch(predicate -> predicate.test(ObservationFactory.getObservations(agentHost)));
    }

    public boolean effectsCompleted() {
        Observations observations = ObservationFactory.getObservations(agentHost);
        return effectsCompleted(observations);
    }

    protected boolean effectsCompleted(Observations observations) {
        return observations != null && effects.stream().allMatch(predicate -> predicate.test(observations));
    }

    @Override
    public boolean perform() {
        Observations observations = null;
        while (!effectsCompleted(observations) && preconditionsMet()) {
            observations = ObservationFactory.getObservations(agentHost);
            doAction(observations);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return effectsCompleted();
    }

    protected abstract void doAction(Observations observations);

    public int cost() {
        return 0;
    }
}
