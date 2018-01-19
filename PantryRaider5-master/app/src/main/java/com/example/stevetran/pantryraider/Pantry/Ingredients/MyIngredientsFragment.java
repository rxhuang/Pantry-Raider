package com.example.stevetran.pantryraider.Pantry.Ingredients;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.stevetran.pantryraider.R;
import com.example.stevetran.pantryraider.Util.SharedConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.SimpleSearchFilter;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;

public class MyIngredientsFragment extends Fragment {
    View view;
    private ArrayList<SearchModel> items;
    private HashSet<String> myIngredients;
    private ArrayList<String> myIngredientsList;
    private DatabaseReference mDatabase;
    private ArrayAdapter<String> adapter;
    private HashMap<String, String> gids;
    private String key;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_ingredients, container, false);
        myIngredients = new HashSet<>();
        myIngredientsList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        gids = new HashMap<>();
        key = SharedConstants.FIREBASE_USER_ID;
        //fetch all ingredients from database and add to myIngreidentList/myIngredients
        readUserIngredients();
        //render myIngredientList to listview
        inflateListView();
        //read all ingredients
        try {
            initData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //overwrite backbutton
        setupBackButton();
        return view;
    }

    private void setupBackButton(){
        //overwrites default backbutton
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
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
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        setupSearchButton(view);
    }
    private void setupSearchButton(View view){
        view.findViewById(R.id.addIngredientsButton).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                SimpleSearchDialogCompat<SearchModel> dialog = new SimpleSearchDialogCompat(getContext(), "Search for ingredients...", "What ingredients do you have?",
                        null, items, new SearchResultListener<Searchable>() {

                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                        if (!myIngredients.contains(searchable.getTitle())) {
                            //add this ingredient to my ingredients
                            myIngredients.add(searchable.getTitle());
                            myIngredientsList.add(searchable.getTitle());
                            //update database
                            addToDb(searchable.getTitle());
                            //reload listview
                            refresh();
                        }
//                        baseSearchDialogCompat.dismiss();
                    }
                });
                //show dialog
                dialog.show();
                //set up filter. This could be customized. For detail, check
                // <url>https://github.com/mirrajabi/search-dialog/blob/master/library/src/main/java/ir/mirrajabi/searchdialog/SimpleSearchFilter.java</url>
                dialog.setFilter(new SimpleSearchFilter<SearchModel>(items, dialog.getFilterResultListener(), false, 0.73f));
            }
        });
    }

    // this method reads all ingredients from ingredients.txt and stores them into items.
    private void initData() throws IOException {
        items = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(getContext().getAssets().open("ingredients.txt")));
        // do reading, usually loop until end of file reading
        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();
        while (mLine != null) {
            mLine = mLine.trim();
            String temp[] = mLine.split("\\t");
            if(myIngredients.contains(temp[0])) continue;
            items.add(new SearchModel(temp[0]));
            gids.put(temp[0],temp[1]);
            mLine = reader.readLine();
        }
        reader.close();
    }
    //add recipe to database
    private void addToDb(String item){
        mDatabase.child("/Account/"+key+"/Ingredients/").child(item).setValue(gids.get(item));
    }

    private void refresh(){
        adapter.notifyDataSetChanged();
    }
    private void removeFromDb(int oId){
        // User clicked Done
        String removeIng = myIngredientsList.get((oId));
        myIngredientsList.remove(oId);
        myIngredients.remove(removeIng);
        adapter.notifyDataSetChanged();
        mDatabase.child("/Account/"+key+"/Ingredients").child(removeIng).removeValue();
    }
    //load recipe from database
    private void readUserIngredients() {
        mDatabase.child("/Account/").child(key + "/").child("Ingredients/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // addIngredients is a JSON object storing all ingredients in
                // the user's pantry, need to populate ListView in 'My Ingredients'
                if(snapshot.getValue() == null) {
                    Log.e("Server Error", "onDataChange: snapshot.getValue() is null");
                    return;
                }
                String ingList = snapshot.getValue().toString();
                ArrayList<String> ingArray = new ArrayList<>();
                ingList = ingList.substring(1, ingList.length());
                String[] tmp = ingList.split(", ");
                String name = "";
                for(int i = 0; i < tmp.length; i++) {
                    name = tmp[i].substring(0, tmp[i].indexOf('='));
                    if(!(name.equals("dummy"))){
                        myIngredientsList.add(name);
                        myIngredients.add(name);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void inflateListView(){
        ListView lvSearch = (ListView) view.findViewById(R.id.MyIngredients);
        adapter = new ArrayAdapter<String>(
                this.getActivity(),
                android.R.layout.simple_list_item_1,
                myIngredientsList
        );
        lvSearch.setAdapter(adapter);
        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long oId) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(myIngredientsList.get((int)oId))
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                removeFromDb((int)oId);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog deleteDialog = builder.create();
                deleteDialog.show();
            }
        });
    }
}