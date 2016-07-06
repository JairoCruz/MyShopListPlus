package com.shoplist.myshoplistplus.sharing;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.User;


/**
 * Populates the list_view_friends_autocomplet inside AddFriendActivity
 */
public class AutocompleteFriendAdapter extends FirebaseListAdapter<User> {


    /**
     * Public constructor that initializes private instance variables when adapter is created
     * @param activity
     * @param modelClass
     * @param modelLayout
     * @param ref
     */
    public AutocompleteFriendAdapter(Activity activity, Class<User> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    /**
     * Protected method that populates the view attached to the adapter (list_view_friends_autocomplete)
     * with items inflated from single_autocomplet_item.xml
     * populateView also handles data changes and update the listView accordingly
     * @param v
     * @param model
     * @param position
     */

    @Override
    protected void populateView(View v, User model, int position) {

    }

    /**
     * Checks if the friend you try to add is the current user
     */
    private boolean isNotCurrentUser(User user){
        return true;
    }

    /**
     * Checks if the friend you try to add is already added, given a dataSnapshot of a user
     */
    private boolean isNotAlreadyAdded(DataSnapshot dataSnapshot, User user){
        return true;
    }
}
