package com.tcc.tccpinut.tccpinut.classes;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by muffinmad on 14/09/2016.
 */
public class Pinut {

    // TODO: Criar os métodos (se tiver métodos...)
    private int pinid;
    private int ownerid;
    private Date expireOn;
    private int privacy;
    private Date createdOn;
    private LatLng location;
    private String imagepath;
    private String audiopath;

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getAudiopath() {
        return audiopath;
    }

    public void setAudiopath(String audiopath) {
        this.audiopath = audiopath;
    }

    public int getPinid() {
        return pinid;
    }

    public void setPinid(int pinid) {
        this.pinid = pinid;
    }

    public int getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(int ownerid) {
        this.ownerid = ownerid;
    }

    public Date getExpireOn() {
        return expireOn;
    }

    public void setExpireOn(Date expireOn) {
        this.expireOn = expireOn;
    }

    public int getPrivacy() {
        return privacy;
    }

    public void setPrivacy(int privacy) {
        this.privacy = privacy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }


}
