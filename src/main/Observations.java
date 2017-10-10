package main;

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
    public List<String> CellObs;
    public List<Integer> nbItems;
    public List<String> items;
}
