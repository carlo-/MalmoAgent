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

    public IsAt(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean test(Observations observations) {
        return Math.abs(x - observations.XPos) < 1
                && Math.abs(y - observations.YPos) <1
                && Math.abs(z - observations.ZPos) < 1;
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
