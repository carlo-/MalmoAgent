package domain.fluents;

import domain.AtomicFluent;
import domain.BlockType;
import main.Observations;

public class BlockAt implements AtomicFluent {
    private final float mX;
    private final float mZ;
    private final float mY;
    private final BlockType mTypeOfBlock;


    public BlockAt(float x, float y, float z, BlockType type) {
        this.mX = x;
        this.mY = y;
        this.mZ = z;

        mTypeOfBlock = type;
    }

    public String getTypeOfBlockString() {
        return mTypeOfBlock.name();
    }

    @Override
    public boolean test(Observations observations) {
        BlockAt blockAt = observations.blockAt(mX, mY, mZ);
        if (mTypeOfBlock.equals(BlockType.Any)) {
            return !mTypeOfBlock.equals(BlockType.air);
        }
        boolean equals = mTypeOfBlock.equals(blockAt.getTypeOfBlock());
        return equals;
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    public float getZ() {
        return mZ;
    }

    public BlockType getTypeOfBlock() {
        return mTypeOfBlock;
    }

    // for test purposes
    @Override
    public String toString() {
        return "Block Type: " + mTypeOfBlock.toString() + ", x: " + mX + ", y: " + mY + ", z: " + mZ;
    }
}
