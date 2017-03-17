package com.printdinc.printd.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ThingiverseCollection implements Parcelable {
    public long id;
    public String name;
    public String description;
    public String added;
    public String modified;
    public String url;
    public int count;
    public String thumbnail;
    public String thumbnail_1;
    public String thumbnail_2;
    public String thumbnail_3;

    public ThingiverseCollection() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.added);
        dest.writeString(this.modified);
        dest.writeString(this.url);
        dest.writeInt(this.count);
        dest.writeString(this.thumbnail);
        dest.writeString(this.thumbnail_1);
        dest.writeString(this.thumbnail_2);
        dest.writeString(this.thumbnail_3);
    }

    protected ThingiverseCollection(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.description = in.readString();
        this.added = in.readString();
        this.modified = in.readString();
        this.url = in.readString();
        this.count = in.readInt();
        this.thumbnail = in.readString();
        this.thumbnail_1 = in.readString();
        this.thumbnail_2 = in.readString();
        this.thumbnail_3 = in.readString();
    }

    public static final Creator<ThingiverseCollection> CREATOR = new Creator<ThingiverseCollection>() {
        public ThingiverseCollection createFromParcel(Parcel source) {
            return new ThingiverseCollection(source);
        }

        public ThingiverseCollection[] newArray(int size) {
            return new ThingiverseCollection[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThingiverseCollection that = (ThingiverseCollection) o;

        if (id != that.id) return false;
        if (count != that.count) return false;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        if (added != null ? !added.equals(that.added) : that.added != null) return false;
        if (modified != null ? !modified.equals(that.modified) : that.modified != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (thumbnail != null ? !thumbnail.equals(that.thumbnail) : that.thumbnail != null) return false;
        if (thumbnail_1 != null ? !thumbnail_1.equals(that.thumbnail_1) : that.thumbnail_1 != null) return false;
        if (thumbnail_2 != null ? !thumbnail_2.equals(that.thumbnail_2) : that.thumbnail_2 != null) return false;
        if (thumbnail_3 != null ? !thumbnail_3.equals(that.thumbnail_3) : that.thumbnail_3 != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (added != null ? added.hashCode() : 0);
        result = 31 * result + (modified != null ? modified.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (thumbnail != null ? thumbnail.hashCode() : 0);
        result = 31 * result + (thumbnail_1 != null ? thumbnail_1.hashCode() : 0);
        result = 31 * result + (thumbnail_2 != null ? thumbnail_2.hashCode() : 0);
        result = 31 * result + (thumbnail_3 != null ? thumbnail_3.hashCode() : 0);
        result = 31 * result + count;

        return result;
    }
}
