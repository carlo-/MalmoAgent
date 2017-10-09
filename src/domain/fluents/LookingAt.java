package domain.fluents;

import domain.AtomicFluent;
import main.Observations;

/**
 * Created by Carlo on 9.10.2017.
 */
public class LookingAt implements AtomicFluent {

    private final float x;
    private final float z;
    private final float y;

    public LookingAt(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean test(Observations observations) {
        // TODO: Implement
        return false;
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
}
