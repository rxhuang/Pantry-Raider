package com.example.stevetran.pantryraider.Pantry.Ingredients;

/**
 * Created by William Ma on 11/30/2017.
 */

public class Ingredient {
    public String name;
    public String image_url;
    public String gid;
    public int value;

    Ingredient(String name, String image_url, String gid) {
        this.name = name;
        this.image_url = image_url;
        this.gid = gid;
        this.value = 0;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    public String getImage_url() {
        return this.name;
    }

    public int getGid() {
        return this.value;
    }

}
