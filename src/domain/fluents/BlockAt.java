package domain.fluents;

import domain.AtomicFluent;
import domain.BlockType;
import main.Observations;
import static main.JavaAgent.*;

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

    public String getTypeOfBlockString(){
        return mTypeOfBlock.name();
    }

    @Override
    public boolean test(Observations observations) {
        // to test for any block just see that there is no air
        int i = 0;
        for (String block : observations.CellObs) {
            int xPos = i % X_OBSERVATION_SIZE;
            int yPos = (i % (X_OBSERVATION_SIZE * Y_OBSERVATION_SIZE)) / X_OBSERVATION_SIZE;
            int zPos = i / (X_OBSERVATION_SIZE * Y_OBSERVATION_SIZE);
            if (xPos == (int)mX && yPos == (int)mY && zPos == (int)mZ) {
                if (BlockType.valueOf(block).compareTo(BlockType.air) == 0) {
                    return false;
                } else {
                    return true;
                }
            }
            i++;
        }
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

    @Override
    public String toString() {
        return "Block Type: " + mTypeOfBlock.toString() + "\tx: " + mX + "\ty: " + mY + "\tz: " + mZ;
    }
}
