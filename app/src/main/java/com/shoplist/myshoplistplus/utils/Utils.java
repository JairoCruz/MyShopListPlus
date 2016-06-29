package com.shoplist.myshoplistplus.utils;

import com.shoplist.myshoplistplus.model.ShoppingList;

import java.text.SimpleDateFormat;

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
}
