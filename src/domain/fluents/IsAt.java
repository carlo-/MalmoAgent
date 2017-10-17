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
    private final boolean exact;

    public IsAt(float x, float y, float z, float distance, boolean exact) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.distance = distance;
        this.exact = exact;
    }

    public IsAt(float x, float y, float z, float distance) {
        this(x, y, z, distance, false);
    }

    public IsAt(float x, float y, float z) {
        this(x, y, z, 0.0f, false);
    }

    @Override
    public boolean test(Observations observations) {
        /*
        return Math.abs(x - observations.XPos) <= distance
           //     && Math.abs(y - observations.YPos) <= distance
                && Math.abs(z - observations.ZPos) <= distance;
        */
        float d = (Math.abs(observations.ZPos - z) + Math.abs(observations.XPos - x));
        if (exact) {
            return d == distance;
        } else {
            return d <= distance;
        }
    }

    public boolean isExact() {
        return exact;
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
