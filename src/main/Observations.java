package main;

import domain.BlockType;
import domain.fluents.BlockAt;

import java.util.ArrayList;
import java.util.List;

import static main.JavaAgent.*;

//All fields are case sensitive in relation to their json counterparts
public class Observations {
    public int TimeAlive;
    public float XPos;
    public float YPos;
    public float ZPos;
    public float Yaw;
    public float Pitch;
    public boolean IsAlive;
    public List<String> CellObs;
    public List<Integer> nbItems;
    public List<String> items;

    public List<BlockAt> findBlockType(BlockType blockType) {
        List<BlockAt> blocks = new ArrayList<BlockAt>();
        int i = 0;
        int xRelative;
        int yRelative;
        int zRelative;
        for (String block : CellObs) {
            if (blockType.compareTo(BlockType.valueOf(block)) == 0) {
                // calculate position in the grid
                xRelative = i % X_OBSERVATION_SIZE;
                yRelative = (i % (X_OBSERVATION_SIZE * Y_OBSERVATION_SIZE)) / X_OBSERVATION_SIZE;
                zRelative = i / (X_OBSERVATION_SIZE * Y_OBSERVATION_SIZE);
                // calculate position relative to us
                xRelative += X_START_OBSERVATION;
                yRelative += Y_START_OBSERVATION;
                zRelative += Z_START_OBSERVATION;
                // add new block at with absolute position to list
                blocks.add(new BlockAt(XPos + xRelative, YPos + yRelative, ZPos + zRelative, blockType));
            }
            i++;
        }
        if (blocks.size() == 0) {
            // no blocks of given type found
            return null;
        }
        return blocks;
    }

    public int numberOf(String item) {
        int index = 0;
        int total = 0;
        for (String s : items) {
            if (s.equals(item)) {
                total += nbItems.get(index);
            }
            ++index;
        }
        return total;
    }
}
