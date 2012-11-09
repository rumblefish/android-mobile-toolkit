
package com.rumblefish.friendlymusic.components;

import android.graphics.Point;

public class PointKu extends Point
{

    public PointKu()
    {
        super(0, 0);
    }

    public PointKu(float f, float f1)
    {
        super((int)f, (int)f1);
        x = (int)f;
        y = (int)f1;
    }

    public PointKu(int i, int j)
    {
        super(i, j);
        x = i;
        y = j;
    }

    public void incPoint(int i, int j)
    {
        x = i + x;
        y = j + y;
    }

    public void setPoint(int i, int j)
    {
        x = i;
        y = j;
    }
}