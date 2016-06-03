package com.shoplist.myshoplistplus;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    // Annotaciones generadas por ButterKnife
    @Bind(R.id.app_bar)
    Toolbar appBar;
    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.pager)
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // vinculacion con elemento ButterKnife
        ButterKnife.bind(this);


        initializeScreen();
    }

    // Este metodo lo utilizo para inicializar los elementos de mi app
    private void initializeScreen() {
        // Set a Toolbar to act as (actue como) the ActionBar for this Activity window.
        setSupportActionBar(appBar);

        /*create SectionPagerAdapter, set it as adapter to viewPager with setOffscreenPageLimit (2)*/

        // Class inner
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        // setOffscreenPageLimit to ViewPager call paper for ButterKnife
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(adapter);

        /*Setup the TabLayout with view pager*/
        tabLayout.setupWithViewPager(pager);
    }


    /*
    * SectionPagerAdapter class that extends FragmentStatePagerAdapter to save fragments state
    * */
    public  class SectionPagerAdapter extends FragmentStatePagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /*Use positions (0 and 1) to find and instantiante fragments with newInstance()*/
        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;

           /* Set fragment to different fragments depending on positon in ViewPager */
            switch (position){
                case 0:
                    fragment = ShoppingListFragment.newInstance(mEncodedEmail);
                    break;
                case 1:
                    fragment = MealsFragment.newInstance();
                    break;
                default:
                    fragment = ShoppingListFragment.newInstance(mEncodedEmail);
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        /*Set string resources as titles for each fragment by it's position*/

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return getString(R.string.paper_title_shopping_lists);
                case 1:
                default:
                    return getString(R.string.paper_title_meals);
            }
        }
    }
}
