package main;

import domain.BlockType;
import domain.fluents.BlockAt;
import domain.fluents.IsAt;

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
        //TODO: Ugly hack. But its good enough for the test run to mine a block. Block at currently doesnt compute correctly. Too  tired to fix it. If you ran it, this is the
        // TODO:reason it doesnt stop attacking
        System.out.println((int)x + ", " + y + ", " + (int)z);
        BlockAt blockAt = blockAt(x, y, z, "CellBox");
        if (blockAt != null) {
            return blockAt;
        }
        blockAt = blockAt(x, y, z, "CellPlane");
        if (blockAt != null) {
            return blockAt;
        }
        return null;
        //return new BlockAt(x, y, z, BlockType.log);
        //return new BlockAt(x, y, z, BlockType.Any);
    }

    private BlockAt blockAt (float x, float y, float z, String gridName) {
        ObservationGrid grid = getGrid(gridName);
        // to test for any block just see that there is no air
        int i = 0;
        for (String block : grid.observations) {
            BlockAt position = coordinatesOf(i, grid);
            int nX = (int)position.getX();
            int nY = (int)position.getY();
            int nZ = (int)position.getZ();
            System.out.print(nX + ", " + nY + ", " + nZ + ";  ");
            if (nX == (int)x && nY == (int)y && nZ == (int)z) {
                System.out.println("penis " + new BlockAt(x, y, z, BlockType.valueOf(block)) + " in " + gridName + "\n" + CellBox);
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
                BlockAt position = coordinatesOf(i, grid);
                blocks.add(new BlockAt(position.getX(), position.getY(), position.getZ(), blockType));
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

    private BlockAt coordinatesOf (int i, ObservationGrid observationGrid) {
        // calculate position in the grid
        int x = i % observationGrid.getXObservationSize();
        int y = (i % (observationGrid.getXObservationSize() * observationGrid.getYObservationSize())) / observationGrid.getXObservationSize();
        int z = i / (observationGrid.getXObservationSize() * observationGrid.getYObservationSize());
        // calculate position relative to us
        x += observationGrid.getXStartObservation();
        y += observationGrid.getYStartObservation();
        z += observationGrid.getZStartObservation();
        // add new block at with absolute position to list
        x += XPos;
        y += YPos;
        z += ZPos;
        return new BlockAt(x, y, z, BlockType.Any);
    }
}
