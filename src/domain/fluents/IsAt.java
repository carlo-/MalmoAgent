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
    private final float squareDistance;

    public IsAt(float x, float y, float z, float distance) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.squareDistance = distance*distance;
    }

    public IsAt(float x, float y, float z) {
        this(x, y, z, 0.0f);
    }

    @Override
    public boolean test(Observations observations) {
        return Math.pow(x - observations.XPos, 2) + Math.pow(z - observations.ZPos, 2) <= squareDistance;
             //   && Math.pow(y - observations.YPos, 2) <= squareDistance
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
        return squareDistance;
    }
}
