package com.shoplist.myshoplistplus.activeListDetail;

import android.app.Dialog;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.ShoppingList;
import com.shoplist.myshoplistplus.utils.Constans;

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
    public static EditListNameDialogFragment newInstance(ShoppingList shoppingList){
        EditListNameDialogFragment editListNameDialogFragment = new EditListNameDialogFragment();
        Bundle bundle = EditListDialogFragment.newInstanceHelper(shoppingList, R.layout.dialog_edit_list);
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
         * Set input text to be the current list name if it is not empty
         */
        if (!inputListName.equals("")){

            if (mListName != null){
                /**
                 * If editText input is not equal to the previous name
                 */
                if (!inputListName.equals(mListName)){

                    shoppingListRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_ACTIVE_LIST);
                    /* Make a Hashmap for the specific properties you are changing */
                    HashMap<String, Object> updatedProperties = new HashMap<String, Object>();
                    updatedProperties.put(Constans.FIREBASE_PROPERTY_LIST_NAME, inputListName);

                    /* Add the timestamp for last changed to the updatedProperties Hasmap */
                    HashMap<String, Object> changedTimestampMap = new HashMap<>();
                    changedTimestampMap.put(Constans.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    /* Add the updated timestamp */
                    updatedProperties.put(Constans.FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED, changedTimestampMap);

                    /* Do the update */
                    shoppingListRef.updateChildren(updatedProperties);
                }
            }
        }

    }
}
