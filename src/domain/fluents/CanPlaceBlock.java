package domain.fluents;

import domain.AtomicFluent;
import domain.BlockType;
import main.Observations;

public class CanPlaceBlock implements AtomicFluent {
    private final static float MAX_DISTANCE_TO_CLICK = 2.5f;
    private final float mX;
    private final float mZ;
    private final float mY;

    /**
     *
     * @param x coordinate of the place where we want to place a block
     * @param y coordinate of the place where we want to place a block
     * @param z coordinate of the place where we want to place a block
     */
    public CanPlaceBlock(float x, float y, float z) {
        this.mX = x;
        this.mY = y;
        this.mZ = z;
    }

    @Override
    public boolean test(Observations obs) {
        //check destination block is free
        boolean output = !new BlockAt(mX, mY, mZ, BlockType.Any).test(obs);
        if(!output)return false;//intermediate possible return to be faster often...
        //check the player is close enough, MAX_DISTANCE_TO_CLICK on the 3 axes
        output = output && Math.abs(mX - obs.XPos) < MAX_DISTANCE_TO_CLICK
                && Math.abs(mY - obs.YPos) < MAX_DISTANCE_TO_CLICK
                && Math.abs(mZ - obs.ZPos) < MAX_DISTANCE_TO_CLICK;
        if(!output)return false;
        //check the player is looking at ANY face of a cube next to this one (suppose only plane cubes...)
        output = output && ((new BlockAt(mX + 1, mY, mZ, BlockType.Any).test(obs) && new IsLineOfSightFree(mX + 0.5f, mY, mZ).test(obs))||
                (new BlockAt(mX - 1, mY, mZ, BlockType.Any).test(obs) && new IsLineOfSightFree(mX - 0.5f, mY, mZ).test(obs))||
                (new BlockAt(mX, mY + 1, mZ, BlockType.Any).test(obs) && new IsLineOfSightFree(mX, mY + 0.5f, mZ).test(obs))||
                (new BlockAt(mX, mY - 1, mZ, BlockType.Any).test(obs) && new IsLineOfSightFree(mX, mY - 0.5f, mZ).test(obs))||
                (new BlockAt(mX, mY, mZ + 1, BlockType.Any).test(obs) && new IsLineOfSightFree(mX, mY, mZ + 0.5f).test(obs))||
                (new BlockAt(mX, mY, mZ - 1, BlockType.Any).test(obs) && new IsLineOfSightFree(mX, mY, mZ - 0.5f).test(obs)));
        return output;
    }
}
