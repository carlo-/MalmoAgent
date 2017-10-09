package domain.fluents;

import domain.AtomicFluent;
import main.Observations;

public class IsLineOfSightFree implements AtomicFluent {
    private final float mX;
    private final float mZ;
    private final float mY;

    /**
     * You may want to look at the edges of the cube instead of its center sometimes...
     * @param x
     * @param y
     * @param z
     */
    public IsLineOfSightFree(float x, float y, float z) {
        this.mX = x;
        this.mY = y;
        this.mZ = z;
    }

    @Override
    public boolean test(Observations observations) {
        //TODO if someone feel like he knows well spacial geometry: need to figure out every block in the segment from the player to the destination
        return false;
    }
}
