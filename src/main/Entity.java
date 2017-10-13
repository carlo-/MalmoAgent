package main;

import domain.BlockType;

/**
 * Created by kartasevm on 12.10.2017.
 */
//Java representation of a floating object in the game world. Such as a block that has been mined or dropped, but not yet picked up.
public class Entity {
    public float yaw;
    public float x;
    public float y;
    public float z;
    public float pitch;
    public BlockType name;
    public int quantity;
    public String variation;
}
