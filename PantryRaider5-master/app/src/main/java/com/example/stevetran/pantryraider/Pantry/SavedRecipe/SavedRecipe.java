package com.example.stevetran.pantryraider.Pantry.SavedRecipe;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.stevetran.pantryraider.R;
import com.example.stevetran.pantryraider.Util.SharedConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SavedRecipe {

    public String title;
    public String description;
    public String imageUrl;
    public String instructionUrl;
    public String label;
    public String rid;

    private static DatabaseReference mDatabase;
    private static DatabaseReference relPath;

    public static ArrayList<SavedRecipe> getRecipesFromFile(String filename, Context context) {
        final ArrayList<SavedRecipe> recipeList = new ArrayList<>();

        try {
            // Load data
            String jsonString = loadJsonFromAsset("recipes.json", context);
            JSONObject json = new JSONObject(jsonString);
            JSONArray recipes = json.getJSONArray("recipes");

            // Get Recipe objects from data
            for(int i = 0; i < recipes.length(); i++){
                SavedRecipe recipe = new SavedRecipe();

                recipe.title = recipes.getJSONObject(i).getString("title");
                recipe.description = recipes.getJSONObject(i).getString("description");
                recipe.imageUrl = recipes.getJSONObject(i).getString("image");
                recipe.instructionUrl = recipes.getJSONObject(i).getString("url");
                recipe.label = recipes.getJSONObject(i).getString("dietLabel");

                recipeList.add(recipe);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recipeList;
    }

    public static void getRecipesFromFirebase(final Context context,
                                              final ArrayList<SavedRecipe> recipeList,
                                              final SavedRecipeAdapter adapter) {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String key = SharedConstants.FIREBASE_USER_ID;

        mDatabase.child("/Saved_Recipes/").child(key + "/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    String ridsListStr = "";
                    for (DataSnapshot child : snapshot.getChildren()) {
                        // Parse recipe ID and use it t
                        int recipeId = Integer.parseInt(child.getKey().substring(1));
                        if(recipeId != -1) {
                            ridsListStr += recipeId + ",";
                        }
                    }
                    ridsListStr = ridsListStr.substring(0,ridsListStr.length()-1);

                    getRecipesListInfo(ridsListStr, recipeList, context, adapter);
                } catch (Exception e) {
                    e.printStackTrace();;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private static void getRecipesListInfo(final String ridsListStr,
                                           final ArrayList<SavedRecipe> recipeList,
                                           final Context context,
                                           final SavedRecipeAdapter adapter) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://54.175.239.59:8080/get_recipes_bulk";

        Log.d(null, "GOT HERE 3 !!!!!!!!! ");
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(null, response);
                        System.out.println(response);

                        // Load data
                        JSONObject json = null;
                        try {
                            json = new JSONObject(response);
                            JSONArray recipes = json.getJSONArray("results");

                            // Get Recipe objects from data
                            for(int i = 0; i < recipes.length(); i++) {
                                SavedRecipe recipe = new SavedRecipe();

                                recipe.title = recipes.getJSONObject(i).getString("title");
                                //recipe.description = recipes.getJSONObject(i).getString("description");
                                recipe.imageUrl = recipes.getJSONObject(i).getString("image_url");
                                recipe.rid = recipes.getJSONObject(i).getString("rid");
                                //recipe.instructionUrl = recipes.getJSONObject(i).getString("url");
                                //recipe.label = recipes.getJSONObject(i).getString("dietLabel");

                                recipeList.add(recipe);
                                SharedConstants.rids.add(recipe.rid);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ListView savedRecipeList = (ListView) ((FragmentActivity) context).findViewById(R.id.SavedRecipeList);
                        adapter.setmDataSource(recipeList); //= new SavedRecipeAdapter(((FragmentActivity) context), recipeList);
                        savedRecipeList.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}

        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("rids", ridsListStr);
                //params.put("uid",uid);
                return params;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private static String loadJsonFromAsset(String filename, Context context) {
        String json = null;

        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }
        catch (java.io.IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }

}