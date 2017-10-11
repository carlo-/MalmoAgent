package domain.fluents;

import domain.AtomicFluent;
import main.Observations;

public class HasNumberOfItem implements AtomicFluent {
    private final String mItem;
    private final int mNumberOf;

    public HasNumberOfItem(String item, int numberOf){
        mItem = item;
        mNumberOf = numberOf;
    }

    public String getItem(){
        return mItem;
    }

    @Override
    public boolean test(Observations observations) {
        return observations.numberOf(mItem) >= mNumberOf;
    }
}
