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


    @Override
    public boolean test(Observations observations) {
        int index = 0;
        int total = 0;
        for(String s : observations.items){
            if(s.equals(mItem)){
                total+=observations.nbItems.get(index);
                if(total >= mNumberOf) return true;
            }
            ++index;
        }
        return false;
    }
}
