package domain.fluents;

import domain.AtomicFluent;
import main.Observations;

public class Have implements AtomicFluent {
    private final String mItem;
    private final int mNumberOf;

    public Have(String item, int numberOf) {
        mItem = item;
        mNumberOf = numberOf;
    }

    public String getItem() {
        return mItem;
    }

    public int getmNumberOf() {
        return mNumberOf;
    }

    @Override
    public boolean test(Observations observations) {
        int i = observations.numberOf(mItem);
        boolean b = i >= mNumberOf;
        return b;
    }
}
