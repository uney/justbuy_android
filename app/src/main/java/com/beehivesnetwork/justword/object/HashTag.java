package com.beehivesnetwork.justword.object;

/**
 * Created by davidtang on 2015-12-29.
 */
public class HashTag {
    private String hid;
    private String hashTagName;
    private String hashTagNumber;

    public HashTag(String hid, String hashTagName, String hashTagNumber) {
        this.hid = hid;
        this.hashTagName = hashTagName;
        this.hashTagNumber = hashTagNumber;
    }

    public String getHid() {
        return hid;
    }

    public String getHashTagName() {
        return hashTagName;
    }

    public String getHashTagNumber() {
        return hashTagNumber;
    }
}
