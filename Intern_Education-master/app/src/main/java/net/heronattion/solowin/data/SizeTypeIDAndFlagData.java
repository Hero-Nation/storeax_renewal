package net.heronattion.solowin.data;

/**
 * Created by Heronation on 2017-07-05.
 */

public class SizeTypeIDAndFlagData {

    public int getSizeTypeID() {
        return sizeTypeID;
    }

    public void setSizeTypeID(String sizeTypeID) {
        this.sizeTypeID = Integer.parseInt(sizeTypeID);
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    int sizeTypeID;
    int flag; // 0 error / 1 default / 2 ok



}
