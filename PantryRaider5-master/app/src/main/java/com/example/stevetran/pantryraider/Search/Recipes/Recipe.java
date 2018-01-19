package com.example.stevetran.pantryraider.Search.Recipes;

/**
 * Created by rongfalu on 11/28/17.
 */

public class Recipe {
    public String title;
    public String imageUrl;
    public String rid;

    public Recipe(){}

    public String getName() {
        return this.title;
    }

    String getImage_url() {
        return this.imageUrl;
    }

    public String getId() {
        return this.rid;
    }
}