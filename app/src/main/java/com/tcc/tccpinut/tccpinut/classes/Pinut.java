package com.tcc.tccpinut.tccpinut.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by muffinmad on 14/09/2016.
 */
public class Pinut implements Parcelable {

    // TODO: Criar os métodos (se tiver métodos...)
    private int pinid;
    private int ownerid;
    private long expireOn;
    private int privacy;
    private long createdOn;
    private double latitude;
    private double longitude;
    private String imagepath;
    private String audiopath;
    private String title;
    private String text;
    private int type;

    public Pinut() {

    }

    protected Pinut(Parcel in) {
        pinid = in.readInt();
        ownerid = in.readInt();
        expireOn = in.readLong();
        privacy = in.readInt();
        createdOn = in.readLong();
        latitude = in.readDouble();
        longitude = in.readDouble();
        imagepath = in.readString();
        audiopath = in.readString();
        title = in.readString();
        text = in.readString();
        type = in.readInt();
    }

    public static final Creator<Pinut> CREATOR = new Creator<Pinut>() {
        @Override
        public Pinut createFromParcel(Parcel in) {
            return new Pinut(in);
        }

        @Override
        public Pinut[] newArray(int size) {
            return new Pinut[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

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

    public long getExpireOn() {
        return expireOn;
    }

    public void setExpireOn(long expireOn) {
        /*Date d = new Date();
        d.setTime(expireOn);*/
        this.expireOn = expireOn ;
    }

    public int getPrivacy() {
        return privacy;
    }

    public void setPrivacy(int privacy) {
        this.privacy = privacy;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        /*Date d = new Date();
        d.setTime(createdOn);*/
        this.createdOn = createdOn ;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pinid);
        dest.writeInt(ownerid);
        dest.writeLong(expireOn);
        dest.writeInt(privacy);
        dest.writeLong(createdOn);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(imagepath);
        dest.writeString(audiopath);
        dest.writeString(title);
        dest.writeString(text);
        dest.writeInt(type);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
