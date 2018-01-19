package com.example.stevetran.pantryraider.Search;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.stevetran.pantryraider.R;
import com.example.stevetran.pantryraider.Search.Recipes.Recipe;
import com.example.stevetran.pantryraider.Search.Recipes.RecipeActivity;
import com.example.stevetran.pantryraider.Search.Recipes.RecipeAdapter;
import com.example.stevetran.pantryraider.Util.SharedConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stevetran on 11/19/17.
 */

public class SearchFragment extends Fragment implements View.OnClickListener{

    //this view
    View view;

    //buttons
    Button searchButton;
    Button filterBtn;

    ProgressBar spinner;

    // display results
    RecipeAdapter adapter;
    ArrayList<Recipe> arrayRecipes = new ArrayList<>();

    // pass to RecipeActivity
    ArrayList<String> rid = new ArrayList<>();

    // filters
    AlertDialog filterDialog;
    int cuisineType = -1;
    int calories = 0;
    int recipeType = -1;

    private DatabaseReference mDatabase;
    private boolean hasIngredients = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        //overwrite back button behavior
        setupBackButton(view);

        //set up buttons
        searchButton = (Button) view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        filterBtn = (Button) view.findViewById(R.id.filterBtn);
        filterBtn.setOnClickListener(this);

        spinner = view.findViewById(R.id.loading);
        spinner.setVisibility(View.GONE);

        // set up listview
        ListView lv = (ListView) view.findViewById(R.id.listview);
        adapter = new RecipeAdapter(this.getContext(), arrayRecipes);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(SearchFragment.this.getActivity(), RecipeActivity.class);
                myIntent.putExtra("rid", rid.get((int)id));
                SearchFragment.this.getActivity().startActivity(myIntent);
            }
        });

        // set up database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        readUserIngredients();
        return view;
    }
    @Override
    public void onClick(View v) {
        EditText query = (EditText) view.findViewById(R.id.editText2);
        switch (v.getId()) {
            case R.id.searchButton:
                InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                spinner.setVisibility(View.VISIBLE);
                String q = query.getText().toString();
                if((q.equals("") && recipeType != 0)) {
                    Toast.makeText(getContext(), "Enter a keyword or choose search by pantry.",
                            Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.GONE);
                }
                else if (!hasIngredients && recipeType == 0) {
                    Toast.makeText(getContext(), "Add ingredients to your pantry to search by pantry.",
                            Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.GONE);
                }
                else {
                    makeRequest(query.getText().toString(), cuisineType);
                    setupBackButton(v);
                }
                break;
            case R.id.filterBtn:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Filters")
                        .setSingleChoiceItems(R.array.filters, recipeType, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface mainDialog, int which) {
                                if(which == 0) {
                                    recipeType = which;
                                }
                                else if(which == 1) {
                                    recipeType = which;
                                    AlertDialog.Builder cuisineBuilder = new AlertDialog.Builder(getActivity());
                                    cuisineBuilder.setTitle("Pick cuisine")
                                            .setSingleChoiceItems(R.array.cuisine, cuisineType, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // The 'which' argument contains the index position
                                                    // of the selected item
                                                    cuisineType = which;
                                                }
                                            })
                                            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // User clicked Done
                                                    ((AlertDialog) mainDialog).getListView().setItemChecked(1, true);
                                                }
                                            })
                                            .setNegativeButton("Clear", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // User cancelled the dialog
                                                    ((AlertDialog) mainDialog).getListView().setItemChecked(1, false);
                                                    cuisineType = -1;
                                                }
                                            });
                                    AlertDialog cuisineDialog = cuisineBuilder.create();
                                    cuisineDialog.show();
                                }
                                else if(which == 2) {
                                    recipeType = which;
                                    AlertDialog.Builder caloriesBuilder = new AlertDialog.Builder(getActivity());
                                    final EditText input = new EditText(getContext());
                                    caloriesBuilder.setTitle("Enter max calories")
                                            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // User clicked Done
                                                    String str = input.getText().toString();
                                                    if(!str.equals("")) {
                                                        calories = Integer.parseInt(str);
                                                    }
                                                    if(calories > 0) {
                                                        ((AlertDialog) mainDialog).getListView().setItemChecked(2, true);
                                                    }
                                                    else {
                                                        ((AlertDialog) mainDialog).getListView().setItemChecked(2, false);
                                                    }
                                                }
                                            })
                                            .setNegativeButton("Clear", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // User cancelled the dialog
                                                    calories = 0;
                                                    ((AlertDialog) mainDialog).getListView().setItemChecked(2, false);
                                                }
                                            });
                                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    if(calories > 0) {
                                        input.setText(Integer.toString(calories));
                                    }
                                    caloriesBuilder.setView(input);
                                    AlertDialog caloriesDialog = caloriesBuilder.create();
                                    caloriesDialog.show();
                                }
                            }
                        })
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked Done
                            }
                        })
                        .setNegativeButton("Clear Filters", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                recipeType = -1;
                            }
                        });
                filterDialog = builder.create();
                filterDialog.show();
                break;
        }
    }

    private void makeRequest(final String query, final int cuisineType) {
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url ="http://54.175.239.59:8080/search_recipes";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        rid.clear();
                        arrayRecipes.clear();
                        JSONObject json = null;
                        try {
                            json = new JSONObject(response);
                            JSONArray recipes = json.getJSONArray("results");

                            for(int i = 0; i < recipes.length(); i++) {

                                Recipe recipe = new Recipe();
                                recipe.title = recipes.getJSONObject(i).getString("title");
                                recipe.rid = recipes.getJSONObject(i).getString("rid");
                                recipe.imageUrl = recipes.getJSONObject(i).getString("image_url");
                                arrayRecipes.add(recipe);
                                rid.add(recipe.rid);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();
                        spinner.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}

        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("uid", getUid());
                params.put("query",query);
                switch (recipeType) {
                    case -1:
                        params.put("sbp","false");
                        params.put("cuisine","");
                        params.put("calories","0");
                        break;
                    case 0:
                        params.put("sbp","true");
                        params.put("cuisine","");
                        params.put("calories","0");
                        break;
                    case 1:
                        params.put("sbp","false");
                        params.put("cuisine",getResources().getStringArray(R.array.cuisine)[cuisineType]);
                        params.put("calories","0");
                        break;
                    case 2:
                        params.put("sbp","false");
                        params.put("cuisine","");
                        params.put("calories",Integer.toString(calories));
                        break;

                }
                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public String getUid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getUid();
    }

    public void setupBackButton(View view) {
        //overwrites default backbutton
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
    }

    private void readUserIngredients() {
        String key = SharedConstants.FIREBASE_USER_ID;
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
                if (tmp.length > 1) {
                    hasIngredients = true;
                }
                else {
                    hasIngredients = false;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
