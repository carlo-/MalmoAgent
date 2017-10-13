package main;

import domain.BlockType;
import domain.fluents.BlockAt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//All fields are case sensitive in relation to their json counterparts
public class Observations {
    public int TimeAlive;
    public float XPos;
    public float YPos;
    public float ZPos;
    public float Yaw;
    public float Pitch;
    public boolean IsAlive;
    public List<BlockType> CellPlane;
    public List<BlockType> CellBox;
    public List<Integer> nbItems;
    public List<String> items;
    public List<Entity> Entities;

    public BlockAt blockAt(float x, float y, float z) {
        BlockAt blockAt = blockAt(x, y, z, "CellBox");
        if (blockAt != null) {
            return blockAt;
        }
        return blockAt(x, y, z, "CellPlane");
    }

    private BlockAt blockAt(float x, float y, float z, String gridName) {
        ObservationGrid grid = getGrid(gridName);
        int xRelative = (int) (x - grid.getXStartObservation() - XPos);
        int yRelative = (int) (y - grid.getYStartObservation() - YPos + 1);
        int zRelative = (int) (z - grid.getZStartObservation() - ZPos);

        if (zRelative > grid.getZObservationSize() || zRelative < 0 ||
                xRelative > grid.getXObservationSize() || xRelative < 0 ||
                yRelative > grid.getYObservationSize() || yRelative < 0) {
            return null;
        }

        int index = xRelative + zRelative * grid.getXObservationSize() + yRelative * grid.getXObservationSize() * grid.getZObservationSize();
        return new BlockAt(x, y, z, grid.observations.get(index));
    }

    public List<BlockAt> findBlockType(BlockType blockType) {
        if(blockType == null) return Arrays.asList();
        List<BlockAt> blocks = findBlockType(blockType, "CellBox");
        blocks.addAll(findBlockType(blockType, "CellPlane"));
        return blocks;
    }

    private List<BlockAt> findBlockType(BlockType blockType, String gridName) {
        ObservationGrid grid = getGrid(gridName);
        List<BlockAt> blocks = new ArrayList<>();
        int i = 0;
        int xRelative;
        int yRelative;
        int zRelative;
        for (BlockType block : grid.observations) {
            if (blockType.equals(block)) {
                // calculate position in the grid
                xRelative = i % grid.getXObservationSize();
                yRelative = i / (grid.getXObservationSize() * grid.getZObservationSize());
                zRelative = i / (grid.getXObservationSize());
                // calculate position relative to us
                xRelative += grid.getXStartObservation();
                yRelative += grid.getYStartObservation() - 1;
                zRelative += grid.getZStartObservation();
                // add new block at with absolute position to list
                blocks.add(new BlockAt(XPos + xRelative, YPos + yRelative, ZPos + zRelative, blockType));
            }
            i++;
        }
        return blocks;
    }

    private ObservationGrid getGrid(String gridName) {
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
