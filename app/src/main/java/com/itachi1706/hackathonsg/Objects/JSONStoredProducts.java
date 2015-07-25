package com.itachi1706.hackathonsg.Objects;

/**
 * Created by Kenneth on 26/7/2015
 * for Hackathon@SG in package com.itachi1706.hackathonsg.Objects
 */
public class JSONStoredProducts {

    public int key;
    public boolean isPurchased;
    public long dateStored;

    public void setIsPurchased(boolean isPurchased) {
        this.isPurchased = isPurchased;
    }

    public int getKey() {

        return key;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public long getDateStored() {
        return dateStored;
    }
}
