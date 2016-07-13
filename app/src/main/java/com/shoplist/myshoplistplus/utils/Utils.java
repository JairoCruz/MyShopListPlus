package com.shoplist.myshoplistplus.utils;

import com.google.firebase.database.ServerValue;
import com.shoplist.myshoplistplus.model.ShoppingList;

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
    public static HashMap<String, Object> updateMapForAllWithValue(final String listId, final String owner, HashMap<String, Object> mapToUpdate,
                                                                   String propertyToUpdate, Object valueToUpdate){
        mapToUpdate.put("/" + Constans.FIREBASE_LOCATION_USER_LISTS + "/" + owner + "/" + listId + "/" + propertyToUpdate, valueToUpdate);
        return mapToUpdate;
    }


    /**
     * Adds values to a pre-existing HasMap for updating all Last Changed Timestamps for all of
     * the ShoppingList copies. This method use {@link #updateMapForAllWithValue} to update the
     * last changed timestamp for all ShoppingList copies.
     *
     * @param listId                    The id of the shopping list.
     * @param owner                     The owner of the shoppintg list.
     * @param mapToAddDateToUpdate      The map containing the key, value pairs which will be used
     *                                  to update the Firebase database. This MUST be a HasMap of key
     *                                  value pairs who's urls are absolute (i.e. from the root node)
     *
     * @return
     */
    public static HashMap<String, Object> updateMapWithTimestampLastChanged
    (final String listId, final String owner, HashMap<String, Object> mapToAddDateToUpdate){
        /**
         * Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreateMap
         */
        HashMap<String, Object> timestampNowHash = new HashMap<>();
        timestampNowHash.put(Constans.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        updateMapForAllWithValue(listId, owner, mapToAddDateToUpdate, Constans.FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED, timestampNowHash);

        return mapToAddDateToUpdate;
    }
}
