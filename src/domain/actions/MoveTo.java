package domain.actions;

import com.microsoft.msr.malmo.AgentHost;
import domain.AbstractAction;
import domain.BlockType;
import domain.fluents.BlockAt;
import main.ObservationFactory;
import domain.fluents.Have;
import domain.fluents.IsAt;
import main.Entity;
import main.Observations;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Mart on 8.10.2017.
 */
public class MoveTo extends AbstractAction {
    private final float z;
    private final float y;
    private final float x;
    private final float distance;
    private final float powDistance;
    private final boolean exact;
    private Stack<Position> path;
    private Position lastPos;

    public MoveTo(AgentHost agentHost, IsAt isAt) {
        this(agentHost, isAt, null);
    }

    public MoveTo(AgentHost agentHost, IsAt isAt, Entity entity) {
        super(agentHost);
        this.x = isAt.getX();
        this.y = isAt.getY();
        this.z = isAt.getZ();
        this.distance = isAt.getDistance();
        this.exact = isAt.isExact();
        powDistance = distance * distance;
        this.effects.add(isAt);

        /*
        // FIXME: Enable this back if not using the AutoPickup mod!
        Observations obs = ObservationFactory.getObservations(agentHost);
        if (entity != null) {
            String item = GatherBlock.BLOCK_TO_ITEM.get(entity.name);
            this.effects.add(new Have(item, obs.numberOf(item) + 1));
        }
        */
    }


    public int cost() {
        Observations obs = ObservationFactory.getObservations(agentHost);
        return (int) (Math.abs(x - obs.XPos) + Math.abs(z - obs.ZPos));
    }

    public void doAction(Observations observations) {
        if (path == null/* || path.isEmpty()*/) {
            lastPos = new Position(observations.XPos, observations.ZPos);
            path = BFS(observations);
        }
        if(path.isEmpty())return;
        Position currentChild = path.pop();
        float xDifference = currentChild.mX - lastPos.mX;
        //float yDifference = y - observations.YPos;
        float zDifference = currentChild.mZ - lastPos.mZ;

        if (zDifference > 0) {
            agentHost.sendCommand("movesouth 1");
        } else if (zDifference < 0) {
            agentHost.sendCommand("movenorth 1");
        } else if (xDifference > 0) {
            agentHost.sendCommand("moveeast 1");
        } else if (xDifference < 0) {
            agentHost.sendCommand("movewest 1");
        }

        lastPos = currentChild;

    }

    @Override
    public String toString() {
        String str = "MoveTo position : x = " + x + ", y = " + y + ", z = " + z;
        if (exact) {
            return str + " at exact distance of " + distance;
        } else {
            return str + " within distance of " + distance;
        }
    }

    private Stack<Position> BFS(Observations obs) {
        Map<Position, Position> map = new HashMap<>();
        Position currentPosition = new Position(obs.XPos, obs.ZPos);
        map.put(currentPosition, null);
        Queue<Position> nextPositions = new ConcurrentLinkedQueue<>();

        while (currentPosition != null && !checkPosGoal(currentPosition, obs)) {
            addChild(currentPosition.mX + 1, currentPosition.mZ, obs, map, nextPositions, currentPosition);
            addChild(currentPosition.mX - 1, currentPosition.mZ, obs, map, nextPositions, currentPosition);
            addChild(currentPosition.mX, currentPosition.mZ + 1, obs, map, nextPositions, currentPosition);
            addChild(currentPosition.mX, currentPosition.mZ - 1, obs, map, nextPositions, currentPosition);
            currentPosition = nextPositions.poll();
        }
        Stack<Position> output = new Stack<>();
        while (currentPosition != null) {
            output.push(currentPosition);
            currentPosition = map.get(currentPosition);
        }
        return output;
    }

    public boolean checkPosGoal(Position current, Observations obs) {
        //return (Math.pow(current.mZ - z, 2) + Math.pow(current.mX - x, 2) <= powDistance);
        //return (Math.abs(current.mZ - z) + Math.abs(current.mX - x) <= distance);

        float d = (Math.abs(current.mZ - z) + Math.abs(current.mX - x));
        if (exact) {
            return d == distance;
        } else {
            return d <= distance;
        }
    }

    public void addChild(float x, float z, Observations obs, Map<Position, Position> map, Queue<Position> nextPos, Position currentPosition) {
        if (isFree(x, z, obs)) {
            Position child = new Position(x, z);
            if (!map.containsKey(child)) {
                map.put(child, currentPosition);
                nextPos.add(child);
            }
        }
    }

    public boolean isFree(float x, float z, Observations obs) {
        BlockAt b1 = obs.blockAt(x, obs.YPos - 1, z);
        BlockAt b2 = obs.blockAt(x, obs.YPos - 2, z);
        return b1 != null && b2 != null && // Can't walk outside of the observable area
                b1.getTypeOfBlock().equals(BlockType.air) && // Can't walk through solid blocks
                !b2.getTypeOfBlock().equals(BlockType.air); // Can't walk over a hole!
    }

    public class Position {
        private final float mX;
        private final float mZ;

        public Position(float x, float z) {
            mX = x;
            mZ = z;
        }

        @Override
        public int hashCode() {
            return (int) mX + 10000 * (int) mZ;
        }

        public String toString() {
            return "x " + mX + " z " + mZ;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Position) {
                Position p = (Position) o;
                return p.mX == mX && p.mZ == mZ;
            }
            return false;
        }
    }
}
