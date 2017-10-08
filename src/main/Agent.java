package main;

import domain.Action;
import domain.ActionFactory;
import domain.AtomicFluent;
import domain.actions.Stop;
import domain.fluents.IsAt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mart on 8.10.2017.
 */
public class Agent {
    private final AtomicFluent currentGoal;
    private final ActionFactory factory;
    private Action currentAction;
    private List<Action> plan;
    private Stop stop;

    public Agent(AtomicFluent currentGoal, ActionFactory factory) {
        this.currentGoal = currentGoal;
        this.factory = factory;
        this.stop = factory.createStop();
        this.plan = determinePlan(currentGoal);
    }

    public void execute(Observations observations) {
        if (currentAction == null && plan.size() > 0) {
            currentAction = plan.remove(0);
        }

        if(currentAction == null && plan.size() == 0){
            System.out.println("Done executing");
            return;
        }

        if (currentAction != null && !currentAction.getEffects().stream().allMatch(fluent -> fluent.test(observations))) {
            currentAction.accept(observations);
        } else {
            stop.accept(observations); //Temporary so we can identify when we have ended.
            currentAction = null;
        }
    }

    public List<Action> determinePlan(AtomicFluent goal) {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(factory.createMoveToAction((IsAt) goal)); //Just a placeholder so I can demonstrate an idea
        return actions;
    }
}
