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
public class EditListNameDialogFragment extends EditListDialogFragment {

    private static final String LOG_TAG = ActiveListDetailsActivity.class.getSimpleName();
    String mListName;
    private DatabaseReference shoppingListRef;

    /**
     * Public static constructor that creates fragment and passes a bundle with data into it when adapter is created
     */
    public static EditListNameDialogFragment newInstance(ShoppingList shoppingList, String listId, String encodedemail,
                                                         HashMap<String, User> sharedWithUsers){
        EditListNameDialogFragment editListNameDialogFragment = new EditListNameDialogFragment();
        Bundle bundle = EditListDialogFragment.newInstanceHelper(shoppingList, R.layout.dialog_edit_list, listId, encodedemail, sharedWithUsers);
        // Paso el valor del nombre de lista por medio de esta intancia empleando el bundle
        bundle.putString(Constans.KEY_LIST_NAME, shoppingList.getListName());
        editListNameDialogFragment.setArguments(bundle);
        return editListNameDialogFragment;
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtengo el valor enviado a traves de newInstance utilizando el Bundle
        mListName = getArguments().getString(Constans.KEY_LIST_NAME);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /**
         * EditListDialogFragment#createDialogHelper(int) is a superclass
         * method that creates the dialog
         */
        Dialog dialog = super.createDialogHelper(R.string.positive_button_edit_item);
        /**
         * EditListDialogFragment#helpSetDefaultValueEditText(String)) is a superclass
         * method that sets the default text of the TextView
         */
        helpSetDefaultValueEditEditText(mListName);
        return dialog;
    }

    /**
     * Changes the list name in all copies of the current list
     */

    @Override
    protected void doListEdit() {
        final String inputListName = mEditTextForList.getText().toString();
        /**
         * Check that the user inputted list name is not empty, has changed the original name
         * and that the dialog was properly initialized with the current name and id of the list.
         */
        if (!inputListName.equals("") && mListName != null && mListId != null && !inputListName.equals(mListName)){
            final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();
            /**
             * Create map and fill it in with deep path multi write operations list
             */
            HashMap<String, Object> updatedListData = new HashMap<String, Object>();

            /* Add the value to update at the specified property for all lists */
            Utils.updateMapForAllWithValue(mSharedWith,mListId, mOwner, updatedListData, Constans.FIREBASE_PROPERTY_LIST_NAME, inputListName);

            /* Update affected lists timestamps */
            Utils.updateMapWithTimestampLastChanged(mSharedWith,mListId, mOwner, updatedListData);

            /* Do a deep-path update */
            firebaseRef.updateChildren(updatedListData, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    /* Now that we have the timestamp, update the resersed timestamp */
                    Utils.updateTimestampReversed(databaseError, LOG_TAG, mListId, mSharedWith, mOwner);
                }
            });
        }
    }
}
