package com.example.stevetran.pantryraider.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.stevetran.pantryraider.R;
import com.example.stevetran.pantryraider.Search.Recipes.RecipeActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.stevetran.pantryraider.Util.SharedConstants.BASE_URL;

/**
 * Created by erxili on 11/30/2017.
 */

public class HomeFragment extends Fragment implements View.OnClickListener{

    View view;
    String[] rid = new String[8];

    int[] text = {
            R.id.textView1,
            R.id.textView2,
            R.id.textView3,
            R.id.textView4,
            R.id.textView5,
            R.id.textView6,
            R.id.textView7,
            R.id.textView8
    };
    int[] picture = {
            R.id.imageView1,
            R.id.imageView2,
            R.id.imageView3,
            R.id.imageView4,
            R.id.imageView5,
            R.id.imageView6,
            R.id.imageView7,
            R.id.imageView8
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        for(int i = 0; i < 8; i++) {
            TextView tv = view.findViewById(text[i]);
            ImageView iv = view.findViewById(picture[i]);

            tv.setOnClickListener(this);
            iv.setOnClickListener(this);
        }

        makeRequest();
        return view;
    }

    @Override
    public void onClick(View v) {
        int clickedrecipe = 0;
        int name = v.getId();
        if((name == R.id.textView1)||(name == R.id.imageView1))
            clickedrecipe = 0;
        if((name == R.id.textView2)||(name == R.id.imageView2))
            clickedrecipe = 1;
        if((name == R.id.textView3)||(name == R.id.imageView3))
            clickedrecipe = 2;
        if((name == R.id.textView4)||(name == R.id.imageView4))
            clickedrecipe = 3;
        if((name == R.id.textView5)||(name == R.id.imageView5))
            clickedrecipe = 4;
        if((name == R.id.textView6)||(name == R.id.imageView6))
            clickedrecipe = 5;
        if((name == R.id.textView7)||(name == R.id.imageView7))
            clickedrecipe = 6;
        if((name == R.id.textView8)||(name == R.id.imageView8))
            clickedrecipe = 7;

        Intent myIntent = new Intent(getActivity(), RecipeActivity.class);
        myIntent.putExtra("rid", rid[clickedrecipe]);
        getActivity().startActivity(myIntent);
    }

    public void makeRequest() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = BASE_URL + "explore";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject json;
                        try {
                            json = new JSONObject(response);
                            JSONArray recipes = json.getJSONArray("results");

                            TextView recipeName;
                            ImageView recipeImage;
                            for(int i = 0; i < 8; i++) {
                                String title = recipes.getJSONObject(i).getString("title");
                                String imageUrl = recipes.getJSONObject(i).getString("image_url");
                                rid[i] = recipes.getJSONObject(i).getString("rid");

                                recipeName = view.findViewById(text[i]);
                                recipeImage = view.findViewById(picture[i]);

                                recipeName.setText(title);
                                Picasso.with(getContext())
                                        .load(imageUrl)
                                        .into(recipeImage);
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        }) {
            @Override
            protected Map<String,String> getParams(){
                return new HashMap<String, String>();
            }
        };
        queue.add(stringRequest);
    }
}