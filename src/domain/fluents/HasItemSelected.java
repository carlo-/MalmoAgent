package domain.fluents;

import domain.AtomicFluent;
import main.Observations;

public class HasItemSelected implements AtomicFluent {
    private final String mItem;

    public HasItemSelected(String item){
        mItem = item;
    }

    @Override
    public boolean test(Observations observations) {
        //the item selected should always be at position 0, the AI will not ever move it
        return observations.items.get(0).equals(mItem);
    }
}
