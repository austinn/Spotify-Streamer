package com.refect.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by anelson on 6/22/15.
 */
public class ArtistModel implements Parcelable {

    private String id;
    private String name;
    private List<Image> images;
    private String url;
    private String genre;

    public ArtistModel() {

    }

    public ArtistModel(String id, String name, String url, String genre) {
        setId(id);
        setName(name);
        setUrl(url);
        setGenre(genre);
    }


    protected ArtistModel(Parcel in) {
    }

    public static final Creator<ArtistModel> CREATOR = new Creator<ArtistModel>() {
        @Override
        public ArtistModel createFromParcel(Parcel in) {
            return new ArtistModel(in);
        }

        @Override
        public ArtistModel[] newArray(int size) {
            return new ArtistModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(id);
        parcel.writeString(url);
        parcel.writeString(genre);
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
