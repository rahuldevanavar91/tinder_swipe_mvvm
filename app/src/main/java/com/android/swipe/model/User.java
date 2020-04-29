package com.android.swipe.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.android.swipe.model.converts.LocationConverter;
import com.android.swipe.model.converts.NameConverter;
import com.android.swipe.utils.IconSelectedType;

@Entity(tableName = "user")
public class User {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "key")
    private int key;

    @ColumnInfo(name = "view_type")
    private int viewType;

    @ColumnInfo(name = "name")
    @TypeConverters(NameConverter.class)
    private Name name;

    @ColumnInfo(name = "location")
    @TypeConverters(LocationConverter.class)
    private Location location;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "dob")
    private String dob;

    @ColumnInfo(name = "phone")
    private String phone;

    @ColumnInfo(name = "picture")
    private String picture;

    @Ignore
    private IconSelectedType iconSelectedType = IconSelectedType.name;

    public Name getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }


    public String getDob() {
        return dob;
    }

    public String getEmail() {
        return email;
    }


    public String getPhone() {
        return phone;
    }

    public String getPicture() {
        return picture;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public IconSelectedType getIconSelectedType() {
        return iconSelectedType;
    }

    public void setIconSelectedType(IconSelectedType iconSelectedType) {
        this.iconSelectedType = iconSelectedType;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
