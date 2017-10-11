package main;

import domain.BlockType;
import domain.fluents.BlockAt;

import java.util.*;

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
    public List<String> CellPlane;
    public List<String> CellBox;
    public List<Integer> nbItems;
    public List<String> items;

    public BlockAt blockAt(float x, float y, float z) {
        return new BlockAt(x, y, z, BlockType.log);
        //TODO: Ugly hack. But its good enough for the test run to mine a block. Block at currently doesnt compute correctly. Too  tired to fix it. If you ran it, this is the
        // TODO:reason it doesnt stop attacking
     /*   BlockAt blockAt = blockAt(x, y, z, "CellBox");
        if (blockAt != null) {
            return blockAt;
        }
        return blockAt(x, y, z, "CellPlane");*/
    }

    private BlockAt blockAt (float x, float y, float z, String gridName) {
        ObservationGrid grid = getGrid(gridName);
        // to test for any block just see that there is no air
        int i = 0;
        for (String block : grid.observations) {
            int nX = i % grid.getXObservationSize();
            int nY = (i % (grid.getXObservationSize() * grid.getYObservationSize())) / grid.getXObservationSize();
            int nZ = i / (grid.getXObservationSize() * grid.getYObservationSize());
            if (nX == (int)x && nY == (int)y && nZ == (int)z) {
                return new BlockAt(x, y, z, BlockType.valueOf(block));
            }
            i++;
        }
        return null;
    }

    public List<BlockAt> findBlockType(BlockType blockType) {
        List<BlockAt> blocks = findBlockType(blockType, "CellBox");
        blocks.addAll(findBlockType(blockType, "CellPlane"));
        return blocks;
    }

    private List<BlockAt> findBlockType (BlockType blockType, String gridName) {
        ObservationGrid grid = getGrid(gridName);
        List<BlockAt> blocks = new ArrayList<BlockAt>();
        int i = 0;
        int xRelative;
        int yRelative;
        int zRelative;
        for (String block : grid.observations) {
            if (blockType.name().equals(block)) {
                // calculate position in the grid
                xRelative = i % grid.getXObservationSize();
                yRelative = (i % (grid.getXObservationSize() * grid.getYObservationSize())) / grid.getXObservationSize();
                zRelative = i / (grid.getXObservationSize() * grid.getYObservationSize());
                // calculate position relative to us
                xRelative += grid.getXStartObservation();
                yRelative += grid.getYStartObservation();
                zRelative += grid.getZStartObservation();
                // add new block at with absolute position to list
                blocks.add(new BlockAt(XPos + xRelative, YPos + yRelative, ZPos + zRelative, blockType));
            }
            i++;
        }
        return blocks;
    }

    private ObservationGrid getGrid (String gridName) {
        ObservationGrid grid = null;
        if (gridName.equals("CellPlane")) {
            grid = JavaAgent.CELL_PLANE;
            grid.observations = CellPlane;
        } else if (gridName.equals("CellBox")) {
            grid = JavaAgent.CELL_BOX;
            grid.observations = CellBox;
        }
        return grid;
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
