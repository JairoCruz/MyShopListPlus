package com.shoplist.myshoplistplus.utils;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.shoplist.myshoplistplus.model.ShoppingList;
import com.shoplist.myshoplistplus.model.User;

import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Created by TSE on 09/06/2016.
 */
public class Utils {
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * Encode user email to use it as a Firebase key (Firebase does not allow "." in the key name)
     * Encoded email is also used as "userEmail", list and item "owner" value
     */
    public static String encodeEmail(String userEmail){
        return userEmail.replace(".", ",");
    }

    /**
     * Email is being decoded just once to display real email in AutocompleteFriendAdapter
     */
    public static String decodeEmail(String userEmail){
        return userEmail.replace(",", ".");
    }

    /**
     * Return true if currentUserEmail equals to shoppinList.owner()
     * Return false otherwise
     */
    public static boolean checkIfOwner(ShoppingList shoppingList, String currentUserEmail){
        return (shoppingList.getOwner() != null && shoppingList.getOwner().equals(currentUserEmail));
    }


    /**
     * Adds values to a pre-existing HashMap for updating a property for all of the ShoppingList copies.
     * The HashMap can then be used with (@link Firebase#updateChildren(Map)) to update the property
     * for all ShoppingList copies.
     *
     * @param sharedWith
     * @param listId                The id of the sopping list.
     * @param owner                 The owner of the shopping list.
     * @param mapToUpdate           The map containing the key, value pairs which will be used
     *                              to update the Firebase database. This MUST be a HasMap of key
     *                              value pairs who's urls are absolute (i.e. from the root node)
     * @param propertyToUpdate      The property to update
     * @param valueToUpdate         The value to update
     * @return The updated HasMap with the new value inserted in all List
     *
     */
    public static HashMap<String, Object> updateMapForAllWithValue(final HashMap<String, User> sharedWith,final String listId, final String owner, HashMap<String, Object> mapToUpdate,
                                                                   String propertyToUpdate, Object valueToUpdate){
        mapToUpdate.put("/" + Constans.FIREBASE_LOCATION_USER_LISTS + "/" + owner + "/" + listId + "/" + propertyToUpdate, valueToUpdate);
        if (sharedWith != null){
            for (User user : sharedWith.values()){
                mapToUpdate.put("/" + Constans.FIREBASE_LOCATION_USER_LISTS + "/" + user.getEmail() + "/" + listId + "/" + propertyToUpdate, valueToUpdate);
            }
        }
        return mapToUpdate;
    }


    /**
     * Adds values to a pre-existing HasMap for updating all Last Changed Timestamps for all of
     * the ShoppingList copies. This method use {@link #updateMapForAllWithValue} to update the
     * last changed timestamp for all ShoppingList copies.
     *
     * @param sharedWith                The list of users the shopping list htat has been updated is shared with.
     * @param listId                    The id of the shopping list.
     * @param owner                     The owner of the shoppintg list.
     * @param mapToAddDateToUpdate      The map containing the key, value pairs which will be used
     *                                  to update the Firebase database. This MUST be a HasMap of key
     *                                  value pairs who's urls are absolute (i.e. from the root node)
     *
     * @return
     */
    public static HashMap<String, Object> updateMapWithTimestampLastChanged
    (final HashMap<String, User> sharedWith,final String listId, final String owner, HashMap<String, Object> mapToAddDateToUpdate){
        /**
         * Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreateMap
         */
        HashMap<String, Object> timestampNowHash = new HashMap<>();
        timestampNowHash.put(Constans.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        updateMapForAllWithValue(sharedWith, listId, owner, mapToAddDateToUpdate, Constans.FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED, timestampNowHash);

        return mapToAddDateToUpdate;
    }

    /**
     * Once an update is made to a ShoopingList, this method is responsible for updating the reversed timestamp to be equal
     * to the negation of the current timestamp. This comes after the updateMapWithTimestampChanged because ServerValue.TIMESTAMP
     * must be resolved to a log value.
     *
     * @param databaseError                 The Firebase error, if there was one, from the original update. this
     *                                      method should only run if the shopping List's timestamp last changed
     *                                      was successfully updated.
     * @param logTagFromActivity            The log tag from the activity calling this method
     * @param listId                        The updated shopping list push ID
     * @param sharedWith                    The list of users that this updated shopping list is shared with
     * @param owner                         The owner of the updated shopping list
     */
    public static void updateTimestampReversed(DatabaseError databaseError, final String logTagFromActivity,
                                               final String listId, final HashMap<String, User> sharedWith,
                                               final String owner){

        if (databaseError != null){
            Log.d(logTagFromActivity, "Error updating timestamp: " + databaseError.getMessage());
        }else {
            final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();
            firebaseRef.child(Constans.FIREBASE_LOCATION_USER_LISTS).child(owner)
                    .child(listId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ShoppingList list = dataSnapshot.getValue(ShoppingList.class);
                    if (list != null){
                        long timeReverse = -(list.getTimestampLastChangedLong());
                        String timeReverseLocation = Constans.FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED_REVERSE
                                + "/" + Constans.FIREBASE_PROPERTY_TIMESTAMP;

                        /**
                         * Create map and fill it in with deep path multi write operations list
                         */
                        HashMap<String, Object> updatedShoppingListData = new HashMap<String, Object>();

                        updateMapForAllWithValue(sharedWith, listId, owner, updatedShoppingListData, timeReverseLocation, timeReverse);

                        firebaseRef.updateChildren(updatedShoppingListData);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(logTagFromActivity, "Error updating data: " + databaseError.getMessage());
                }
            });
        }

    }


}
