package domain.fluents;

import domain.AtomicFluent;
import main.Observations;

public class BlockAt implements AtomicFluent {
    public final static String ANY_BLOCK = "any_block";
    private final float mX;
    private final float mZ;
    private final float mY;
    private final String mTypeOfBlock;


    public BlockAt(float x, float y, float z, String typeOfBlock){
        this.mX = x;
        this.mY = y;
        this.mZ = z;

        mTypeOfBlock = typeOfBlock;
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

    public String getTypeOfBlock(){
        return mTypeOfBlock;
    }
}
