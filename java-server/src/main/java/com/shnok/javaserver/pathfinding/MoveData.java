package com.shnok.javaserver.pathfinding;

import com.shnok.javaserver.model.Point3D;

import java.util.List;

public class MoveData {
    public long moveTimestamp;
    public Point3D startPosition;
    public Point3D destination;
    public long moveStartTime;
    public int ticksToMove;
    public float xSpeedTicks;
    public float ySpeedTicks;
    public float zSpeedTicks;
    public List<Point3D> path;
}
