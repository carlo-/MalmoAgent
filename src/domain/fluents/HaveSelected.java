package domain.fluents;

import domain.AtomicFluent;
import main.Observations;

public class HaveSelected implements AtomicFluent {
    private final String mItem;

    public HaveSelected(String item){
        mItem = item;
    }

    public String getItem(){
        return mItem;
    }

    @Override
    public boolean test(Observations observations) {
        //the item selected should always be at position 0, the AI will not ever move it
        return observations.items.get(0).equals(mItem);
    }
}
