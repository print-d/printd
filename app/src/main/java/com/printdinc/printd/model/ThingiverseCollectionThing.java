package com.printdinc.printd.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by andrewthomas on 3/9/17.
 */

public class ThingiverseCollectionThing implements Parcelable {
    public long id;
    public String name;
    public String thumbnail;

    public ThingiverseCollectionThing() {
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

    protected ThingiverseCollectionThing(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.thumbnail = in.readString();
    }

    public static final Parcelable.Creator<ThingiverseCollectionThing> CREATOR = new Parcelable.Creator<ThingiverseCollectionThing>() {
        public ThingiverseCollectionThing createFromParcel(Parcel source) {
            return new ThingiverseCollectionThing(source);
        }

        public ThingiverseCollectionThing[] newArray(int size) {
            return new ThingiverseCollectionThing[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThingiverseCollection that = (ThingiverseCollection) o;

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
