package com.davidgerstenmier.peoplefinder.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Hobby implements Parcelable {

    private String name = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    public Hobby() {
    }

    protected Hobby(Parcel in) {
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Hobby> CREATOR = new Parcelable.Creator<Hobby>() {
        @Override
        public Hobby createFromParcel(Parcel source) {
            return new Hobby(source);
        }

        @Override
        public Hobby[] newArray(int size) {
            return new Hobby[size];
        }
    };
}
