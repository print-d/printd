package com.printdinc.printd.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by andrewthomas on 3/21/17.
 */

public class ThingiverseThingFile implements Parcelable {
    public long id;
    public String name;
    public String thumbnail;

    public ThingiverseThingFile() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.thumbnail);
    }

    protected ThingiverseThingFile(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.thumbnail = in.readString();
    }

    public static final Parcelable.Creator<ThingiverseThingFile> CREATOR = new Parcelable.Creator<ThingiverseThingFile>() {
        public ThingiverseThingFile createFromParcel(Parcel source) {
            return new ThingiverseThingFile(source);
        }

        public ThingiverseThingFile[] newArray(int size) {
            return new ThingiverseThingFile[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThingiverseThingFile that = (ThingiverseThingFile) o;

        if (id != that.id) return false;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (thumbnail != null ? !thumbnail.equals(that.thumbnail) : that.thumbnail != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (thumbnail != null ? thumbnail.hashCode() : 0);

        return result;
    }
}
