package com.shoplist.myshoplistplus.activeListDetail;

import android.app.Dialog;
import android.os.Bundle;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.ShoppingList;
import com.shoplist.myshoplistplus.model.User;
import com.shoplist.myshoplistplus.utils.Constans;
import com.shoplist.myshoplistplus.utils.Utils;

import java.util.HashMap;

/**
 * Created by TSE on 13/06/2016.
 */

/* Lets user edit list item name for all copies of the current list */
public class EditListItemNameDialogFragment extends EditListDialogFragment {

    String mItemName;
    String mItemId;

    /**
     * Public static constructor that creates fragments and passes a bundle with data into it when adapter is created
     */
    public static EditListItemNameDialogFragment  newInstance(ShoppingList shoppingList, String itemName, String itemId, String listId, String encodedEmail,
                                                              HashMap<String, User> sharedWithUsers){
        EditListItemNameDialogFragment editListItemNameDialogFragment = new EditListItemNameDialogFragment();
        Bundle bundle = EditListDialogFragment.newInstanceHelper(shoppingList, R.layout.dialog_edit_item, listId, encodedEmail, sharedWithUsers);
        bundle.putString(Constans.KEY_LIST_ITEM_NAME, itemName);
        bundle.putString(Constans.KEY_LIST_ITEM_ID, itemId);
        editListItemNameDialogFragment.setArguments(bundle);
        return editListItemNameDialogFragment;
    }

    /**
     * Initialize instance variables with data from bundle
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemName = getArguments().getString(Constans.KEY_LIST_ITEM_NAME);
        mItemId = getArguments().getString(Constans.KEY_LIST_ITEM_ID);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.createDialogHelper(R.string.positive_button_edit_item);

        /**
         * EditListDialogFragment#helpSetDefaultValueEditText(String) is a superclass
         * method that sets the default text of the TextView
         */
        super.helpSetDefaultValueEditEditText(mItemName);

        return dialog;
    }

    /**
     * Change selected list item name to hte editText input if it is not empty
     */
    @Override
    protected void doListEdit() {

        String nameInput = mEditTextForList.getText().toString();

        /**
         * Set input text to be the current list item name if it is not empty and is not the previous name.
         *
         */
        if (!nameInput.equals("") && !nameInput.equals(mItemName)){
            DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();

            /* Make a map for the item you are changing the name of */
            HashMap<String, Object> updatedDataItemToEditMap = new HashMap<String, Object>();

            /* Add the new name to the update map */
            updatedDataItemToEditMap.put("/" + Constans.FIREBASE_LOCATION_SHOPPING_LIST_ITEMS + "/" + mListId + "/" + mItemId + "/" + Constans.FIREBASE_PROPERTY_ITEM_NAME, nameInput);


            /* Update affected lists timestamps */
            Utils.updateMapWithTimestampLastChanged(mSharedWith,mListId, mOwner, updatedDataItemToEditMap);

            /* Do the update */
            firebaseRef.updateChildren(updatedDataItemToEditMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    /* Now that we have the timestamp, update the reversed timestamp */
                    Utils.updateTimestampReversed(databaseError, "EditListItem", mListId, mSharedWith, mOwner);
                }
            });
        }
    }
}
