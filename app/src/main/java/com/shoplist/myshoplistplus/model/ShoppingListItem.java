package com.shoplist.myshoplistplus.model;

/**
 * Created by Marhinita on 18/6/2016.
 *
 * Defines the data structure for ShoppingListItem objects.
 */

public class ShoppingListItem {

    private String itemName;
    private String owner;

    public ShoppingListItem() {
    }

    public ShoppingListItem(String itemName, String owner) {
        this.itemName = itemName;
        this.owner = owner;
    }

    public String getItemName() {
        return itemName;
    }

    public String getOwner() {
        return owner;
    }
}
