package com.shoplist.myshoplistplus.sharing;

import android.app.Activity;
import android.view.View;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shoplist.myshoplistplus.model.ShoppingList;
import com.shoplist.myshoplistplus.model.User;

import java.util.HashMap;

/**
 * Created by TSE on 06/07/2016.
 */
public class FriendAdapter extends FirebaseListAdapter<User> {

    private static final String LOG_TAG = FriendAdapter.class.getSimpleName();
    private HashMap<FirebaseDatabase, ValueEventListener> mLocationListenerMap;


    public FriendAdapter(Activity activity, Class<User> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    /**
     * Protected method that populates the view attached to the adapter (list_view_friends_autocomplete)
     * with items inflated from single_user_item.xml
     * populateView also handles data changes and updates the listView accordingly
     *
     *
     * @param v
     * @param friend
     * @param position
     */

    @Override
    protected void populateView(View v, final User friend, int position) {

    }


    /**
     * Public method that is used to pass ShoppingList object when it is loaded in ValueEventListener
     */
    public void setShoppingList(ShoppingList shoppingList){

    }

    /**
     * Public method that is used to pass ShareUsers when they are loaded in ValueEventListener
     */
    public void setShareWithUsers(HashMap<String, User> sharedUsersList){

    }

    /**
     * This method does the tricky job of adding or removing a friend from the sharedWith list.
     * addFriend this is true if the friend is being added, false is the friend is being removed.
     * friendToAddOrRemove this is the friend to either add or remove
     */
    private HashMap<String, Object> updateFriendInSharedWith(Boolean addFriend, User friendToAddOrRemove){
        return null;
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }
}
