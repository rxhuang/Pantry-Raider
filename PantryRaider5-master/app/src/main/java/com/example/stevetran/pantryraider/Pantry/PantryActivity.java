package com.example.stevetran.pantryraider.Pantry;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.stevetran.pantryraider.Pantry.Ingredients.MyIngredientsFragment;
import com.example.stevetran.pantryraider.Pantry.SavedRecipe.SavedRecipeListFragment;
import com.example.stevetran.pantryraider.R;
import com.example.stevetran.pantryraider.Util.BottomNavigationHelper;
import com.example.stevetran.pantryraider.Util.SectionsPagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Created by lierxi on 11/12/17.
 */

public class PantryActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 2;
    private Context mContext = PantryActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);
        //set up the bottom navigation view
        setupBottomNavigationView();
        //set up view pager for the fragments
        setupViewPager();
        //set up toolbar
        setupToolBar();
    }
    /**
     * start fragments
     */
    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PantryFragment()); //index 0
        adapter.addFragment(new SavedRecipeListFragment()); //index 1
        adapter.addFragment(new MyIngredientsFragment()); //index 2
        ViewPager viewPager = (ViewPager) findViewById(R.id.container); //from layout_center_viewpager
        viewPager.setAdapter(adapter);
    }
    /**
     * setupToolBar
     */
    private void setupToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.tabs);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText("Pantry");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    /**
     * Bottom Navigation setup
     */
    private void setupBottomNavigationView(){
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationHelper.enableNavigation(mContext, bottomNavigationViewEx, this);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
