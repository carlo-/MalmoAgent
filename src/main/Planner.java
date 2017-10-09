package main;

import com.microsoft.msr.malmo.AgentHost;
import com.microsoft.msr.malmo.WorldState;
import domain.Action;
import domain.ActionFactory;
import domain.AtomicFluent;
import domain.fluents.IsAt;

import java.util.ArrayList;
import java.util.List;

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
            plan.remove(0).perform();
        }
        System.out.println("Done executing");
    }

    public List<Action> determinePlan(AtomicFluent goal) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(factory.createMoveToAction((IsAt) goal)); //Just a placeholder so I can demonstrate an idea
        return actions;
    }
}
