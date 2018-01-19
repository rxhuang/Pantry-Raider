package com.example.stevetran.pantryraider.Pantry.SavedRecipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stevetran.pantryraider.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Abel on 11/19/2017.
 */

public class SavedRecipeAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<SavedRecipe> mDataSource;

    public SavedRecipeAdapter(Context context, ArrayList<SavedRecipe> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setmDataSource(ArrayList<SavedRecipe> items) {
        mDataSource = items;
    }

    //1
    @Override
    public int getCount() {
        return mDataSource.size();
    }

    //2
    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    //4
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.list_item_recipe, parent, false);
        // Get title element
        TextView titleTextView =
                (TextView) rowView.findViewById(com.example.stevetran.pantryraider.R.id.recipe_list_title);

        // Get subtitle element
        TextView subtitleTextView =
                (TextView) rowView.findViewById(com.example.stevetran.pantryraider.R.id.recipe_list_subtitle);

        // Get detail element
        TextView detailTextView =
                (TextView) rowView.findViewById(com.example.stevetran.pantryraider.R.id.recipe_list_detail);

        // Get thumbnail element
        ImageView thumbnailImageView =
                (ImageView) rowView.findViewById(com.example.stevetran.pantryraider.R.id.recipe_list_thumbnail);

        SavedRecipe recipe = (SavedRecipe) getItem(position);

        titleTextView.setText(recipe.title);
        subtitleTextView.setText(recipe.description);
        detailTextView.setText(recipe.label);

        Picasso.with(mContext).load(recipe.imageUrl).placeholder(R.mipmap.ic_launcher).into(thumbnailImageView);
        return rowView;
    }
}
