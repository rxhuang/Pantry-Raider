package com.example.stevetran.pantryraider.Pantry.SavedRecipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.stevetran.pantryraider.R;
import com.example.stevetran.pantryraider.Search.Recipes.RecipeActivity;
import com.example.stevetran.pantryraider.Util.SharedConstants;

import java.util.ArrayList;

/**
 * Created by William Ma on 11/17/2017.
 */

public class SavedRecipeListFragment extends Fragment {
    View view;
    private ListView savedRecipeList;
    ArrayList<SavedRecipe> recipeList;
    SavedRecipeAdapter adapter;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_saved_recipe, container, false);
        inflateListView();

        //overwrites default backbutton
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK) {
                    ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.container);
                    viewPager.setCurrentItem(0);
                    //set up a new title
                    TextView mTitle = (TextView) getActivity().findViewById(R.id.toolbar_title);
                    mTitle.setText("Pantry");
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    private void inflateListView(){
        savedRecipeList = (ListView) getActivity().findViewById(R.id.SavedRecipeList);
        SharedConstants.rids.clear();
        recipeList = new ArrayList<>();
        //final ArrayList<SavedRecipe> recipeList = SavedRecipe.getRecipesFromFile("recipes.json", getActivity());
        ListView savedRecipeList = (ListView) view.findViewById(R.id.SavedRecipeList);
        adapter = new SavedRecipeAdapter(getActivity(), recipeList);

        SavedRecipe.getRecipesFromFirebase(getActivity(), recipeList, adapter);
        savedRecipeList = (ListView) view.findViewById(R.id.SavedRecipeList);
        savedRecipeList.setAdapter(adapter);

        savedRecipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(getActivity(), RecipeActivity.class);
                myIntent.putExtra("rid", recipeList.get((int)id).rid);
                String key = SharedConstants.FIREBASE_USER_ID;
                myIntent.putExtra("uid", key);
                getActivity().startActivity(myIntent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
        Log.e("tag", "HEY WE ARE HERE IN ON RESUME");
    }
    private void refresh(){
        if(SharedConstants.deletedRecipes == null) return;
        int position = SharedConstants.rids.indexOf(SharedConstants.deletedRecipes);
        if(position == -1) return;
        recipeList.remove(recipeList.get(position));
        SharedConstants.rids.remove(SharedConstants.deletedRecipes);
        adapter.notifyDataSetChanged();
        SharedConstants.deletedRecipes = null;
    }

}