package main;

import com.microsoft.msr.malmo.AgentHost;
import com.microsoft.msr.malmo.WorldState;
import domain.Action;
import domain.ActionFactory;
import domain.AtomicFluent;
import domain.ObservationFactory;
import domain.actions.GatherBlock;
import domain.actions.PlaceBlock;
import domain.fluents.BlockAt;
import domain.fluents.Have;
import domain.fluents.HaveSelected;
import domain.fluents.IsAt;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Mart on 8.10.2017.
 */
public class Planner {
    private List<AtomicFluent> currentGoal;
    private final ActionFactory factory;
    private final AgentHost agentHost;
    private List<Action> plan;
    private Observations planObservation;
    private Map<Action, Integer> costs = new HashMap<>();
    private final static int THRESHOLD = 20;

    public Planner(List<AtomicFluent> currentGoal, AgentHost agentHost) {

        this.currentGoal = currentGoal;
        this.agentHost = agentHost;
        this.factory = new ActionFactory(agentHost);
        planObservation = ObservationFactory.getObservations(agentHost);
        this.plan = determinePlan(currentGoal, planObservation);
        System.out.println(plan);
    }

    public void execute() {
        WorldState worldState = agentHost.getWorldState();
        while (!currentGoal.stream().allMatch(pred -> pred.test(ObservationFactory.getObservations(agentHost)) && worldState.getIsMissionRunning())) {
            while (plan.size() > 0) {
                Action action = plan.get(0);
                if (action.preconditionsMet()) {
                    Action remove = plan.remove(0);
                    if (!remove.effectsCompleted() || remove.getEffects().size() == 0) {
                        boolean perform = remove.perform();
                        /*
                        if (this.excludeActionsExcept(action) && (costs.isEmpty() || costs.get(action) >= THRESHOLD)) {
                            plan = plan.stream().filter(this::excludeActionsExcept).collect(Collectors.toList());
                            costs.clear();
                            plan.stream().forEach(a -> {
                                costs.put(a, a.cost());
                            });
                            Collections.sort(plan, (o1, o2) -> costs.get(o1) - costs.get(o2));
                        }
                        */
                        if (!perform) {
                            List<AtomicFluent> fluents = remove.getEffects().stream().filter(pred -> !pred.test(ObservationFactory.getObservations(agentHost))).collect(Collectors.toList());
                            List<Action> actions = determinePlan(fluents, ObservationFactory.getObservations(agentHost)); //Reevaluate if our preconditions are not met for some reason
                            actions.addAll(plan);
                            plan = actions;
                        }
                    }
                } else {
                    List<Action> actions = satisfyConditions(action, ObservationFactory.getObservations(agentHost)); //Reevaluate if our preconditions are not met for some reason
                    actions.addAll(plan);
                    plan = actions;
                }
                System.out.println(plan);
            }
            if (!currentGoal.stream().allMatch(pred -> pred.test(ObservationFactory.getObservations(agentHost)))) {
                currentGoal = currentGoal.stream().filter(pred -> !pred.test(ObservationFactory.getObservations(agentHost))).collect(Collectors.toList());
                List<Action> actions = determinePlan(currentGoal, ObservationFactory.getObservations(agentHost)); //Reevaluate if our preconditions are not met for some reason

                actions.addAll(plan);
                plan = actions;
            }
        }
        System.out.println("Done executing");
    }

    public List<Action> determinePlan(List<AtomicFluent> goal, Observations observations) {
        return goal.stream().map(fluent -> evaluate(fluent, observations)).flatMap(Collection::stream).collect(Collectors.toList());
    }

    private List<Action> evaluate(AtomicFluent fluent, Observations observations) {
        List<Action> actions = factory.createPossibleActions(fluent, observations);
        if (actions.size() < 1) {
            throw new IllegalStateException("I dont know how to solve this");
        }

        Action bestAction = findCheapest(actions);
        if (bestAction.getPreconditions().size() == 0) {
            ArrayList<Action> determinedList = new ArrayList<>();
            determinedList.add(bestAction);
            return determinedList;
        }

        List<Action> collect = null;
        collect = satisfyConditions(bestAction, observations);
        collect.add(bestAction);
        if (observations.equals(planObservation)) {
            updatePlanObservation(bestAction);
        }
        return collect;
    }

    private void updatePlanObservation(Action bestAction) {
        bestAction.getEffects().forEach(effect -> {
            if (effect instanceof Have) {
                Have have = (Have) effect;
                int i = planObservation.items.indexOf((have.getItem()));
                if (i != -1) {
                    planObservation.nbItems.set(i, planObservation.nbItems.get(i)
                            + have.getmNumberOf() -
                            ObservationFactory.getObservations(agentHost).numberOf(have.getItem()));
                } else {
                    int air = planObservation.items.indexOf("air");
                    planObservation.items.set(air, have.getItem());
                    planObservation.nbItems.set(air, have.getmNumberOf() -
                            ObservationFactory.getObservations(agentHost).numberOf(have.getItem()));
                }
            } else if (effect instanceof HaveSelected) {
                HaveSelected haveSelected = (HaveSelected) effect;
                int i = planObservation.items.indexOf(haveSelected.getItem());
                String itemSwapped = planObservation.items.get(0);
                int quantitySwapped = planObservation.nbItems.get(0);
                planObservation.items.set(0, planObservation.items.get(i));
                planObservation.nbItems.set(0, planObservation.nbItems.get(i));
                planObservation.items.set(i, itemSwapped);
                planObservation.nbItems.set(i, quantitySwapped);
            } else if (effect instanceof IsAt) {
                IsAt isAt = (IsAt) effect;
                planObservation.XPos = isAt.getX();
                planObservation.YPos = isAt.getY();
                planObservation.ZPos = isAt.getZ();
            } else if (effect instanceof BlockAt) {
                BlockAt blockAt = (BlockAt) effect;
                planBlockAt(blockAt);
            }
        });
    }

    private List<Action> satisfyConditions(Action bestAction, Observations observations) {
        List<Action> test = bestAction.getPreconditions().stream()
                .filter(precondition -> !precondition.test(observations))
                .map(fluent -> evaluate(fluent, observations))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return test;
    }

    public void planBlockAt(BlockAt effect) {
        planBlockAt(effect, "CellBox");
        planBlockAt(effect, "CellPlane");
    }

    public void planBlockAt(BlockAt effect, String gridd) {
        ObservationGrid grid = planObservation.getGrid(gridd);
        int xRelative = (int) (effect.getX() - grid.getXStartObservation() - planObservation.XPos);
        int yRelative = (int) (effect.getY() - grid.getYStartObservation() - planObservation.YPos + 1);
        int zRelative = (int) (effect.getZ() - grid.getZStartObservation() - planObservation.ZPos);

        if (zRelative > grid.getZObservationSize() || zRelative < 0 ||
                xRelative > grid.getXObservationSize() || xRelative < 0 ||
                yRelative > grid.getYObservationSize() || yRelative < 0) {
            return;
        }

        int index = xRelative + zRelative * grid.getXObservationSize() + yRelative * grid.getXObservationSize() * grid.getZObservationSize();
        if (gridd.equals("CellPlane")) {
            planObservation.CellPlane.set(index, effect.getTypeOfBlock());
        } else {
            planObservation.CellBox.set(index, effect.getTypeOfBlock());
        }
    }

    private Action findCheapest(List<Action> actions) {
        int cost = Integer.MAX_VALUE;
        Action cheapestAction = null;
        for (Action action : actions) {
            if (cost > action.cost()) {
                cost = action.cost();
                cheapestAction = action;
            }

        }
        actions.remove(cheapestAction);
        return cheapestAction;
    }

    public class BigAction {
        private final int mCost;
        private final Action mAction;

        public BigAction(Action action) {
            mCost = action.cost();
            mAction = action;
        }
    }

    public boolean excludeActionsExcept(Action action) {
        return action instanceof GatherBlock || action instanceof PlaceBlock;
    }
}
