package com.shoplist.myshoplistplus.activeList;

import android.app.Activity;
import android.view.View;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.shoplist.myshoplistplus.model.ShoppingList;

/**
 * Created by TSE on 03/06/2016.
 */

// Populates the list_view_active_list inside ShoppingListFragment
public class ActiveListAdapter extends FirebaseListAdapter<ShoppingList> {
    public ActiveListAdapter(Activity activity, Class<ShoppingList> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, modelLayout, ref);
    }

    @Override
    protected void populateView(View v, ShoppingList model, int position) {

    }
}
