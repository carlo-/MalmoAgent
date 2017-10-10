package main;

import com.microsoft.msr.malmo.AgentHost;
import com.microsoft.msr.malmo.WorldState;
import domain.Action;
import domain.ActionFactory;
import domain.AtomicFluent;
import domain.fluents.IsAt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mart on 8.10.2017.
 */
public class Planner {
    private final AtomicFluent currentGoal;
    private final ActionFactory factory;
    private final AgentHost agentHost;
    private List<Action> plan;


    public Planner(AtomicFluent currentGoal, AgentHost agentHost) {
        this.currentGoal = currentGoal;
        this.factory = new ActionFactory(agentHost);
        this.plan = determinePlan(currentGoal);
        this.agentHost = agentHost;
    }

    public void execute() {
        WorldState worldState = agentHost.getWorldState();
        while (plan.size() > 0 && worldState.getIsMissionRunning()) {
            Action action = plan.get(0);
            if (action.preconditionsMet()) {
                plan.remove(0).perform();
            } else {
                List<Action> actions = satisfyConditions(action); //Reevaluate if our preconditions are not met for some reason
                actions.addAll(plan);
                plan = actions;
            }
        }
        System.out.println("Done executing");
    }

    public List<Action> determinePlan(AtomicFluent goal) {
        return evaluate(goal);
    }

    private List<Action> evaluate(AtomicFluent fluent) {
        List<Action> actions = factory.createPossibleActions(fluent);
        if (actions.size() < 1) {
            throw new IllegalStateException("I dont know how to solve this");
        }
        Action bestAction = findCheapest(actions);

        if (bestAction.getPreconditions().size() == 0) {
            ArrayList<Action> determinedList = new ArrayList<>();
            determinedList.add(bestAction);
            return determinedList;
        }

        List<Action> collect = satisfyConditions(bestAction);
        collect.add(bestAction);
        return collect;
    }

    private List<Action> satisfyConditions(Action bestAction) {
        return bestAction.getPreconditions().stream()
                .filter(precondition -> !precondition.test(bestAction.getObservations()))
                .map(this::evaluate)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Action findCheapest(List<Action> actions) {
        return actions.get(0); //Whatever, doesn't matter for now
    }
}
