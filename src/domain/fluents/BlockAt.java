package domain.fluents;

import domain.AtomicFluent;
import domain.BlockType;
import main.Observations;

public class BlockAt implements AtomicFluent {
    private final float mX;
    private final float mZ;
    private final float mY;
    private final BlockType mTypeOfBlock;


    public BlockAt(float x, float y, float z, BlockType type){
        this.mX = x;
        this.mY = y;
        this.mZ = z;

        mTypeOfBlock = type;
    }

    @Override
    public boolean test(Observations observations) {
        //TODO special case ANY_BLOCK
        return false;
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

    public BlockType getTypeOfBlock(){
        return mTypeOfBlock;
    }
}
