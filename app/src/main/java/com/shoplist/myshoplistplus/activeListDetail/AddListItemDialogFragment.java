package com.shoplist.myshoplistplus.activeListDetail;

import android.app.Dialog;
import android.os.Bundle;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.ShoppingList;
import com.shoplist.myshoplistplus.model.ShoppingListItem;
import com.shoplist.myshoplistplus.model.User;
import com.shoplist.myshoplistplus.utils.Constans;
import com.shoplist.myshoplistplus.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by TSE on 13/06/2016.
 */

/* Lets user add new list item */
public class AddListItemDialogFragment extends EditListDialogFragment {

    DatabaseReference firebaseRef;
    DatabaseReference itemsRef;

    /**
     * Public static constructor that creates fragment and passes a bundle with data into it when adapter is created
     */
    public static AddListItemDialogFragment newInstance(ShoppingList shoppingList, String listId, String encodedEmail,
                                                        HashMap<String, User> sharedWithUsers){
        AddListItemDialogFragment addListItemDialogFragment = new AddListItemDialogFragment();
        Bundle bundle = EditListDialogFragment.newInstanceHelper(shoppingList, R.layout.dialog_add_item, listId, encodedEmail, sharedWithUsers);
        addListItemDialogFragment.setArguments(bundle);
        return addListItemDialogFragment;
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /**
         * EditListDialogFragment#createDialogHelper(int) is a
         * superclass method that creates the dialog
         */
        return super.createDialogHelper(R.string.positive_button_add_list_item);
    }

    /**
     * Adds new item to the current shopping list
     */
    @Override
    protected void doListEdit() {
        String mItemName = mEditTextForList.getText().toString();
        /**
         * Adds list item if the input name is not empty
         */
        if (!mItemName.equals("")){
            firebaseRef = FirebaseDatabase.getInstance().getReference();
            itemsRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_SHOPPING_LIST_ITEMS).child(mListId);

            /* Make a map for the item you are adding */
            HashMap<String, Object> updateItemToAddMap = new HashMap<String, Object>();

            /* Save push() to maintain same randow Id */
            DatabaseReference newRef = itemsRef.push();
            String itemId = newRef.getKey();

            /* Make a POJO for the item and immediately turn it into a HashMap */
            ShoppingListItem itemToAddObject = new ShoppingListItem(mItemName, mEncodedEmail);
            HashMap<String, Object> itemToAdd = (HashMap<String, Object>) new ObjectMapper().convertValue(itemToAddObject, Map.class);

            /* Add the item to the update map */
            updateItemToAddMap.put("/" + Constans.FIREBASE_LOCATION_SHOPPING_LIST_ITEMS + "/" + mListId + "/" + itemId, itemToAdd);


            /* Update affected lists timestamps */
            Utils.updateMapWithTimestampLastChanged(mSharedWith, mListId, mOwner, updateItemToAddMap);

            /* Do the update */
            firebaseRef.updateChildren(updateItemToAddMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    /* Now that we have the timestamp, update the reserved timestamp */
                    Utils.updateTimestampReversed(databaseError, "AddListItem", mListId,
                            mSharedWith, mOwner);
                }
            });

            /**
             * Close the dialog fragment when done
             *
             */
        AddListItemDialogFragment.this.getDialog().cancel();
        }
    }
}
