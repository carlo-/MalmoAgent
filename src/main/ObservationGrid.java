package main;

import domain.BlockType;

import java.util.List;

public class ObservationGrid {

    public List<BlockType> observations;
    // start point must be lower than or equal to end point, for findBlock
    private int xStartObservation;
    private int yStartObservation;
    private int zStartObservation;
    private int xEndObservation;
    private int yEndObservation;
    private int zEndObservation;
    private int xObservationSize;
    private int yObservationSize;
    private int zObservationSize;
    public ObservationGrid(int xStartObservation, int yStartObservation, int zStartObservation, int xEndObservation, int yEndObservation, int zEndObservation) {
        this.xStartObservation = xStartObservation;
        this.yStartObservation = yStartObservation;
        this.zStartObservation = zStartObservation;
        this.xEndObservation = xEndObservation;
        this.yEndObservation = yEndObservation;
        this.zEndObservation = zEndObservation;
        xObservationSize = Math.abs(xStartObservation - xEndObservation) + 1;
        yObservationSize = Math.abs(yStartObservation - yEndObservation) + 1;
        zObservationSize = Math.abs(zStartObservation - zEndObservation) + 1;
    }

    @Override
    public String toString() {
        return "ObservationGrid{" +
                "xStartObservation=" + xStartObservation +
                ", yStartObservation=" + yStartObservation +
                ", zStartObservation=" + zStartObservation +
                ", xEndObservation=" + xEndObservation +
                ", yEndObservation=" + yEndObservation +
                ", zEndObservation=" + zEndObservation +
                ", xObservationSize=" + xObservationSize +
                ", yObservationSize=" + yObservationSize +
                ", zObservationSize=" + zObservationSize +
                '}';
    }

    public int getXStartObservation() {
        return xStartObservation;
    }

    public int getYStartObservation() {
        return yStartObservation;
    }

    public int getZStartObservation() {
        return zStartObservation;
    }

    public int getXEndObservation() {
        return xEndObservation;
    }

    public int getYEndObservation() {
        return yEndObservation;
    }

    public int getZEndObservation() {
        return zEndObservation;
    }

    public int getXObservationSize() {
        return xObservationSize;
    }

    public int getYObservationSize() {
        return yObservationSize;
    }

    public int getZObservationSize() {
        return zObservationSize;
    }
}
