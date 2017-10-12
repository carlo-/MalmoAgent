package domain.fluents;

import domain.AtomicFluent;
import domain.BlockType;
import main.Observations;

import java.lang.reflect.Array;

public class IsLineOfSightFree implements AtomicFluent {
    private final float mX;
    private final float mZ;
    private final float mY;

    /**
     * You may want to look at the edges of the cube instead of its center sometimes...
     *
     * @param x
     * @param y
     * @param z
     */
    public IsLineOfSightFree(float x, float y, float z) {
        this.mX = x;
        this.mY = y;
        this.mZ = z;
    }

    private boolean checkBlock(Observations observations, BlockAt sourceBlock, BlockAt currBlock, BlockAt comeFrom, float maxDist) {

        float blockMargin = 0.5f;

        float[] offsets_z = {1,0,0,0,0,-1};
        float[] offsets_x = {0,0,0,-1,1,0};
        float[] offsets_y = {0,1,-1,0,0,0};

        // For every block around the current
        for (int i = 0; i<6; ++i) {

            float this_x = currBlock.getX()+offsets_x[i];
            float this_y = currBlock.getY()+offsets_y[i];
            float this_z = currBlock.getZ()+offsets_z[i];

            // Check if already visited
            if (comeFrom.distanceFrom(this_x, this_y, this_z) < blockMargin) {
                // We don't want to go back
                continue;
            }

            LookingAt la = new LookingAt(this_x, this_y, this_z);

            // Check if we're looking at it
            if (la.test(observations)) {

                // Check if this is our target
                if (sourceBlock.distanceFrom(currBlock) >= maxDist) {
                    // Target reached without obstacles
                    return true;
                }

                BlockAt this_b = observations.blockAt(this_x, this_y, this_z);

                if (this_b == null) {
                    // Out of bounds
                    return true;
                }

                if (this_b.getTypeOfBlock().equals(BlockType.air)) {
                    // If block is air, we recur
                    return checkBlock(observations, sourceBlock, this_b, currBlock, maxDist);
                }

                // Nothing else to do
                break;
            }
        }

        return false;
    }

    @Override
    public boolean test(Observations o) {
        BlockAt startingBlock = o.blockAt(o.XPos, o.YPos, o.ZPos);
        float dist = startingBlock.distanceFrom(mX, mY, mZ);
        return checkBlock(o, startingBlock, startingBlock, startingBlock, dist);
    }
}
