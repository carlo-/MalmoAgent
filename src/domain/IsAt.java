package domain;

import main.Observations;

/**
 * Created by Mart on 7.10.2017.
 */
public class IsAt implements Atomic {

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
}
