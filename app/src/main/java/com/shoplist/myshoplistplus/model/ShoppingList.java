package com.shoplist.myshoplistplus.model;

/**
 * Created by Marhinita on 5/6/2016.
 */

public class ShoppingList {
    private String listName;
    private String owner;

    public ShoppingList(){
        // Empty constructor
    }



    public ShoppingList(String listName, String owner) {
        this.listName = listName;
        this.owner = owner;

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
}
