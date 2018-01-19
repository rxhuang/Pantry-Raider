package com.example.stevetran.pantryraider.Pantry.Ingredients;

import ir.mirrajabi.searchdialog.core.Searchable;

/**
 * Created by Abel on 12/5/2017.
 */

public class SearchModel implements Searchable {
    private String mTitle;

    public SearchModel(String mTitle){
        this.mTitle = mTitle;
    }
    @Override
    public String getTitle() {
        return mTitle;
    }
}
