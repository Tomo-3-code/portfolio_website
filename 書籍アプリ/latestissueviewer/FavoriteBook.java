package com.example.latestissueviewer;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FavoriteBook extends RealmObject {
    @PrimaryKey
    private String id;
    private String title;
    private String imageUrl;
    private String releaseDate;  // 発売日フィールド

    // Getter and Setter for releaseDate
    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    // Getter and Setter for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // 既存の getter と setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
