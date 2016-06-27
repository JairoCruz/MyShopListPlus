package com.shoplist.myshoplistplus;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;


/**
 * Created by TSE on 03/06/2016.
 */
public abstract class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    /* Client used to interact with Google APIs*/
    protected  GoogleApiClient mGoogleApiClient;
    protected String mEncodedEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Setup the Google API object to allow Google logins */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_myId))
                .requestEmail()
                .build();


        /**
         * Build a GoogleApiClient with access to the Google Sign-In API and the
         * options specified by gso.
         */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initializeBackground(LinearLayout linearLayout){
        /**
         * Set different background image for landscape and portrait layout
         *
         */
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
           linearLayout.setBackgroundResource(R.drawable.background_loginscreen_land);
        }else{
            linearLayout.setBackgroundResource(R.drawable.background_loginscreen);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}


