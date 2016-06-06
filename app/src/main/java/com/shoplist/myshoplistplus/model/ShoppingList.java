package com.shoplist.myshoplistplus.model;

/**
 * Created by Marhinita on 5/6/2016.
 */

public class ShoppingList {
    private String listName;

    public ShoppingList(){
        // Empty constructor
    }

    public ShoppingList(String listName) {
        this.listName = listName;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}
