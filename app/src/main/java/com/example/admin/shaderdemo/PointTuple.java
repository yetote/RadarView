package com.example.admin.shaderdemo;

/**
 * com.example.admin.baidumapfinddemo.utils
 *
 * @author Swg
 * @time 2017/10/20 9:49
 */
public class PointTuple<X,Y> {
    private X X;
    private Y Y;
    public PointTuple(X x, Y y) {
        X=x;
        Y=y;
    }

    public X getX() {
        return X;
    }

    public void setX(X x) {
        X = x;
    }

    public Y getY() {
        return Y;
    }

    public void setY(Y y) {
        Y = y;
    }
}
