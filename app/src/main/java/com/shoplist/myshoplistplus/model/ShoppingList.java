package com.shoplist.myshoplistplus.model;

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


    public ShoppingList(){
        // Empty constructor
    }



    public ShoppingList(String listName, String owner) {
        this.listName = listName;
        this.owner = owner;
        // Para guardar la hora en la cual se hase un cambio en el registro utilizo un valor desde ServerValue.TIMESTAMP
        HashMap<String,Object> timestampLastChangedObj = new HashMap<String, Object>();
        timestampLastChangedObj.put(Constans.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampLastChangedObj;

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

}
