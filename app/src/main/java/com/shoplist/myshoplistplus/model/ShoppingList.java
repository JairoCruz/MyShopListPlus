package com.shoplist.myshoplistplus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.database.ServerValue;
import com.shoplist.myshoplistplus.utils.Constans;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Marhinita on 5/6/2016.
 */

public class ShoppingList {
    private String listName;
    private String owner;
    private HashMap<String,Object> timestampLastChanged;
    private HashMap<String,Object> timestampCreated;
    private HashMap<String, User> usersShopping;
    private HashMap<String, Object> timestampLastChangedReverse;


    public ShoppingList(){
        // Empty constructor
    }



    public ShoppingList(String listName, String owner, HashMap<String, Object> timestampCreated) {
        this.listName = listName;
        this.owner = owner;
        this.timestampCreated = timestampCreated;
        // Para guardar la hora en la cual se hase un cambio en el registro utilizo un valor desde ServerValue.TIMESTAMP
        HashMap<String,Object> timestampLastChangedObj = new HashMap<String, Object>();
        timestampLastChangedObj.put(Constans.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampLastChangedObj;
        this.usersShopping = new HashMap<>();
        this.timestampLastChangedReverse = null;

    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public HashMap<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public HashMap<String, User> getUsersShopping() {
        return usersShopping;
    }

    public HashMap<String, Object> getTimestampLastChangedReverse() {
        return timestampLastChangedReverse;
    }

    @JsonIgnore
    public long getTimestampLastChangedReverseLong(){
        return (long)timestampLastChangedReverse.get(Constans.FIREBASE_PROPERTY_TIMESTAMP);
    }

    @JsonIgnore
    public long getTimestampLastChangedLong(){
        return (long) timestampLastChanged.get(Constans.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public void setTimestampLastChangedToNow(){
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(Constans.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;
    }
}
