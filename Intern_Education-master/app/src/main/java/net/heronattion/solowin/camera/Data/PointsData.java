package net.heronattion.solowin.camera.Data;

import android.support.annotation.NonNull;

import georegression.struct.point.Point2D_F32;

/**
 * Created by HeroNation on 2017-03-30.
 */

public class PointsData implements Comparable<PointsData> {

    private Point2D_F32 a = new Point2D_F32(); // 시점
    private Point2D_F32 b = new Point2D_F32(); // 종점

    private int flag; // 0이면 LineX, 1이면 LineY

    public void setA(Point2D_F32 a) {
        this.a = a;
    }

    public void setB(Point2D_F32 b) {
        this.b = b;
    }

    public Point2D_F32 getA() {
        return a;
    }

    public Point2D_F32 getB() {
        return b;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public int compareTo(@NonNull PointsData o) {
        // 시점과 종점의 중점 좌표로 비교 한다.
        Point2D_F32 avgBasic = new Point2D_F32((a.x + b.x) / 2, (a.y+b.y) / 2);
        Point2D_F32 avgComp = new Point2D_F32((o.getA().x + o.getB().x) / 2, (o.getA().y+o.getB().y) / 2);
        if (this.flag == 0) { // 가로선일때
            if (avgBasic.getY() > avgComp.getY()) {
                return 1;
            } else if (avgBasic.getY() < avgComp.getY()) {
                return -1;
            } else
                return 0;
        } else if (this.flag == 1) {// 세로선일때
            if (avgBasic.getX() > avgComp.getX()) {
                return 1;
            } else if (avgBasic.getX() < avgComp.getX()) {
                return -1;
            } else
                return 0;
        } else return 0;
    }
}
