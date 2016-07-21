package com.shoplist.myshoplistplus.sharing;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.ShoppingList;
import com.shoplist.myshoplistplus.model.User;
import com.shoplist.myshoplistplus.utils.Constans;
import com.shoplist.myshoplistplus.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by TSE on 06/07/2016.
 */
public class FriendAdapter extends FirebaseListAdapter<User> {

    private static final String LOG_TAG = FriendAdapter.class.getSimpleName();
    private ShoppingList mShoppingList;
    private String mListId;
    private DatabaseReference mFirebaseRef;
    private HashMap<String, User> mSharedUsersList;
    private HashMap<DatabaseReference, ValueEventListener> mLocationListenerMap;


    public FriendAdapter(Activity activity, Class<User> modelClass, int modelLayout, Query ref, String listId) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
        this.mListId = listId;
        mFirebaseRef = FirebaseDatabase.getInstance().getReference();
        mLocationListenerMap = new HashMap<>();
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
        ((TextView)v.findViewById(R.id.user_name)).setText(friend.getName());
        final ImageButton buttonToggleShare = (ImageButton) v.findViewById(R.id.button_toggle_share);

        final DatabaseReference sharedFriendInShoppingListRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_LISTS_SHARED_WITH)
                .child(mListId).child(friend.getEmail());


        /**
         * Gets the value of the friend from the ShoppingList's shareWith list of users
         * and then allows the friend to be toggled as shared with or not.
         *
         * The friend in the snapshot (sharedFriendInShoppingList) will either be a User object
         * (if they are in the the sharedWith List) or null (if they are not in the sharedWith list)
         */

        final ValueEventListener listener = sharedFriendInShoppingListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User sharedFriendInShoppingList = dataSnapshot.getValue(User.class);

                /**
                 * If list is already being shared with this friend, set the buttonToggleShare
                 * to remove selected friend from sharedWith onClick and change the buttonToggleShare image to green
                 */
                if (sharedFriendInShoppingList != null){
                    buttonToggleShare.setImageResource(R.drawable.ic_shared_check);
                    buttonToggleShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /**
                             * Create map and fill it in with deep path multi write operations list.
                             * Use false to mark that you are removing this friend.
                             */
                            HashMap<String, Object> updatedUserData = updateFriendInSharedWith(false, friend);

                            /* Do a deep-path update */
                            mFirebaseRef.updateChildren(updatedUserData, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    Utils.updateTimestampReversed(databaseError, LOG_TAG, mListId,
                                            mSharedUsersList, mShoppingList.getOwner());
                                }
                            });
                        }
                    });
                }else{
                    /**
                     * Set the buttonToggleShare onClickListener to add selected friend to sharedWith
                     * and chage the buttonToggleShare image to grey otherwise
                     */
                    buttonToggleShare.setImageResource(R.drawable.icon_add_friend);
                    buttonToggleShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /**
                             * Create map and fill it in with deep path multi write operations list
                             */
                            HashMap<String, Object> updatedUserData = updateFriendInSharedWith(true, friend);

                            /* Do a deep-path update */
                            mFirebaseRef.updateChildren(updatedUserData, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    Utils.updateTimestampReversed(databaseError, LOG_TAG, mListId,
                                            mSharedUsersList, mShoppingList.getOwner());
                                }
                            });
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, "Error the lectura "   + databaseError.getMessage());
            }
        });
        /* Add the listener to the HasMap so that it can be removed on cleanup */
        mLocationListenerMap.put(sharedFriendInShoppingListRef, listener);
    }


    /**
     * Public method that is used to pass ShoppingList object when it is loaded in ValueEventListener
     */
    public void setShoppingList(ShoppingList shoppingList){

        this.mShoppingList = shoppingList;
        this.notifyDataSetChanged();

    }

    /**
     * Public method that is used to pass ShareUsers when they are loaded in ValueEventListener
     */
    public void setShareWithUsers(HashMap<String, User> sharedUsersList){
        this.mSharedUsersList = sharedUsersList;
        this.notifyDataSetChanged();
    }

    /**
     * This method does the tricky job of adding or removing a friend from the sharedWith list.
     * addFriend this is true if the friend is being added, false is the friend is being removed.
     * friendToAddOrRemove this is the friend to either add or remove
     */
    private HashMap<String, Object> updateFriendInSharedWith(Boolean addFriend, User friendToAddOrRemove){
        HashMap<String, Object> updatedUserData = new HashMap<String, Object>();


        /* The newSharedWith lists contains all users who need their last time changed updated */
        HashMap<String, User> newSharedWith = new HashMap<String, User>(mSharedUsersList);

        /* Update the sharedWith list for this Shopping List */
        if (addFriend){

            /**
             * Changes the timestamp changed to now; Because of ancestry issues, we cannot
             * have one updateChildren call that both creates data and then updates that same data
             * because updateChildren has no way of knowing what was the intended update
             */
            mShoppingList.setTimestampLastChangedToNow();
            /* Make it a Hashmap of the shopping list and user */
            final HashMap<String, Object> shoppingListForFirebase = (HashMap<String, Object>) new ObjectMapper().convertValue(mShoppingList, Map.class);

            final HashMap<String, Object> friendForFirebase = (HashMap<String, Object>) new ObjectMapper().convertValue(friendToAddOrRemove, Map.class);

            /* Add the friend to the shared list */
            updatedUserData.put("/" + Constans.FIREBASE_LOCATION_LISTS_SHARED_WITH + "/" + mListId + "/" + friendToAddOrRemove.getEmail(), friendForFirebase);

            /* Add that shopping list hashmap to the new user's active lists */
            updatedUserData.put("/" + Constans.FIREBASE_LOCATION_USER_LISTS + "/" + friendToAddOrRemove.getEmail()
                + "/" + mListId, shoppingListForFirebase);


        }else {
            /* Remove the friend from the shared list */
            updatedUserData.put("/" + Constans.FIREBASE_LOCATION_LISTS_SHARED_WITH + "/" + mListId + "/" + friendToAddOrRemove.getEmail(), null);

            /* Remove the list from the shared friend */
            updatedUserData.put("/" + Constans.FIREBASE_LOCATION_USER_LISTS + "/" + friendToAddOrRemove.getEmail()
                + "/" + mListId, null);

            newSharedWith.remove(friendToAddOrRemove.getEmail());
        }

        Utils.updateMapWithTimestampLastChanged(newSharedWith,mListId, mShoppingList.getOwner(), updatedUserData);

        return updatedUserData;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        /* Clean up the event listeners */
        for (HashMap.Entry<DatabaseReference, ValueEventListener> listenerToClean : mLocationListenerMap.entrySet()){
            listenerToClean.getKey().removeEventListener(listenerToClean.getValue());
        }
    }
}
