package com.shoplist.myshoplistplus;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by TSE on 21/07/2016.
 */
public class ShoppingListApplication extends android.app.Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("lo primero","ya pase");
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
