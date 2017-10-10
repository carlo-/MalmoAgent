package domain.fluents;

import domain.AtomicFluent;
import main.Observations;

/**
 * Created by Mart on 7.10.2017.
 */
public class IsAt implements AtomicFluent {

    private final float x;
    private final float z;
    private final float y;
    private final float distance;

    public IsAt(float x, float y, float z, float distance) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.distance = distance;
    }
    public IsAt(float x, float y, float z) {
        this(x,y,z, 0.0f);
    }

    @Override
    public boolean test(Observations observations) {
        return Math.abs(x - observations.XPos) <= distance
                && Math.abs(y - observations.YPos) <= distance
                && Math.abs(z - observations.ZPos) <= distance;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getDistance() {
        return distance;
    }
}
