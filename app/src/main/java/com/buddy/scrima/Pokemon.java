package com.buddy.scrima;

import android.os.Parcel;
import android.os.Parcelable;

public class Pokemon implements Parcelable {
    String name;
    String url;
    String id;

    public Pokemon(String name, String url){
        this.name = name;
        this.url = url;
        String[] separated = url.split("/");
        this.id = separated[separated.length-2];
    }

    protected Pokemon(Parcel in) {
        name = in.readString();
        url = in.readString();
        id = in.readString();
    }

    public static final Creator<Pokemon> CREATOR = new Creator<Pokemon>() {
        @Override
        public Pokemon createFromParcel(Parcel in) {
            return new Pokemon(in);
        }

        @Override
        public Pokemon[] newArray(int size) {
            return new Pokemon[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(name);
        parcel.writeValue(url);
    }
}
