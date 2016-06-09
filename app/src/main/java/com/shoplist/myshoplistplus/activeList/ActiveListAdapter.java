package com.shoplist.myshoplistplus.activeList;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.Query;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.ShoppingList;

/**
 * Created by TSE on 03/06/2016.
 */

// Populates the list_view_active_list inside ShoppingListFragment
public class ActiveListAdapter extends FirebaseListAdapter<ShoppingList> {

    private String mEncodedEmail;

    /**
     * Public constructor that initializes private instance variables when adapter is created
     */
    public ActiveListAdapter(Activity activity, Class<ShoppingList> modelClass, int modelLayout, Query ref, String encodedEmail) {
        super(activity, modelClass, modelLayout, ref);
        this.mEncodedEmail = encodedEmail;
        this.mActivity = activity;
    }

    /**
     * Protected method that populates the view attached to the adapter (list_view_active_lists)
     * with items inflated from single_active_list.xml
     * populateView also handles data changes and updates the listView accordingly
     */
    @Override
    protected void populateView(View v, ShoppingList model, int position) {
        TextView textViewListName = (TextView) v.findViewById(R.id.text_view_list_name);
        final TextView textViewCreatedByUser = (TextView) v.findViewById(R.id.text_view_created_by_user);
        //final TextView textViewUsersShopping = (TextView) v.findViewById(R.id.text_view_people_shopping_count);
    }
}
