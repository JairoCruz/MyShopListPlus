package com.shoplist.myshoplistplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;
import com.shoplist.myshoplistplus.activeList.AddListDialogFragment;
import com.shoplist.myshoplistplus.activeList.ShoppingListFragment;
import com.shoplist.myshoplistplus.login.LoginActivity;
import com.shoplist.myshoplistplus.meals.MealsFragment;
import com.shoplist.myshoplistplus.model.User;
import com.shoplist.myshoplistplus.utils.Constans;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    // Annotaciones generadas por ButterKnife
    @Bind(R.id.app_bar)
    Toolbar appBar;
    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.pager)
    ViewPager pager;

    private DatabaseReference mUserRef;
    private ValueEventListener mUserRefListener;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // vinculacion con elemento ButterKnife
        ButterKnife.bind(this);
        // Este codigo es para obtener informacion sobre la instancia de firebase y me muestre en la consola de debug
        // aun si ver funcionabilidad
       // FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);

        /**
         * Create Firebase references
         */
        /* Aca ya no utilizo una referencia al objeto Users de la base de dato. por lo tanto ya no utilizo esta linea de codigo*/
        // mUserRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_USERS).child(mEncodedEmail);

        /* En lugar de leer el objecto de User lo leo de Auth y alli recupero el usuario, todo esto lo hice
        * para utilizar la forma correcta de manejar a los usuarios en login o logout */
        mAuth = FirebaseAuth.getInstance();
        initializeScreen();

        /**
         * Add ValueEventListeners to Firebase references
         * to control get data and control behavior and visibility of elements
         */

        /* Estoy recuperando valores de la base de datos user no de Auth *//*
        mUserRefListener = mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                *//**
                 * Set the activity title to current user name if user is not null
                 *//*
                Log.e("user", user.getName());
                if(user != null){
                    *//* Assumes that the firest word in the user's name is the user's fiirst name. *//*
                    String firstName = user.getName().split("\\s+")[0];
                    String title = firstName + "'s Lists";
                    setTitle(title);
                }else{
                    Log.e("no esta logeado", user.getName());
                    *//* Move user to LoginActivity, and remove the backstack *//*
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, "Error en la lectura de datos " + databaseError.getMessage());
            }
        });
*/
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){
                    Log.e("usuario Auth", "hi" + user.getDisplayName());
                    String firstName = user.getDisplayName().split("\\s+")[0];
                    String title = firstName + "'s Lists";
                    setTitle(title);
                }else{
                    Log.e("no esta logeado", "salu");
                    //* Move user to LoginActivity, and remove the backstack *//
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // mUserRef.removeEventListener(mUserRefListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
    * Create an instance of the AddList dialog fragment an show it
    * */
    public void showAddListDialog(View view){
        DialogFragment dialog = AddListDialogFragment.newInstance(mEncodedEmail);
        dialog.show(MainActivity.this.getFragmentManager(), "AddListDialogFragment");
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
