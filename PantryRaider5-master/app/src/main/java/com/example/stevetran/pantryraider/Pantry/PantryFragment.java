package com.example.stevetran.pantryraider.Pantry;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.stevetran.pantryraider.R;

/**
 * Created by Abel on 11/15/2017.
 */

public class PantryFragment extends Fragment implements View.OnClickListener{
    private Button savedRecipeButton;
    private Button myIngredientsButton;
    private View view;
    final String TITLE = "Pantry";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pantry, container, false);
        //set up buttons
        savedRecipeButton = (Button) view.findViewById(R.id.savedRecipeButton);
        savedRecipeButton.setOnClickListener(this);

        myIngredientsButton = (Button) view.findViewById(R.id.myIngredientsButton);
        myIngredientsButton.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.savedRecipeButton:
                ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.container);
                viewPager.setCurrentItem(1);
                //set up a new title
                TextView mTitle = (TextView) getActivity().findViewById(R.id.toolbar_title);
                mTitle.setText("Saved Recipes");
                break;
            case R.id.myIngredientsButton:
                //Intent ingriIntent = new Intent(getActivity(), MyIngredientsActivity.class);
                //startActivity(ingriIntent);
                ViewPager viewPager2 = (ViewPager) getActivity().findViewById(R.id.container);
                viewPager2.setCurrentItem(2);
                //set up a new title
                TextView mTitle2 = (TextView) getActivity().findViewById(R.id.toolbar_title);
                mTitle2.setText("My Ingredients");
                break;
        }
    }
}

