package com.shoplist.myshoplistplus.activeListDetail;

import android.app.Dialog;
import android.os.Bundle;

import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.ShoppingList;

/**
 * Created by TSE on 13/06/2016.
 */

/* Lets user edit list item name for all copies of the current list */
public class EditListItemNameDialogFragment extends EditListDialogFragment {

    /**
     * Public static constructor that creates fragments and passes a bundle with data into it when adapter is created
     */
    public static EditListItemNameDialogFragment  newInstance(ShoppingList shoppingList, String listId){
        EditListItemNameDialogFragment editListItemNameDialogFragment = new EditListItemNameDialogFragment();
        Bundle bundle = EditListDialogFragment.newInstanceHelper(shoppingList, R.layout.dialog_edit_item, listId);
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
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.createDialogHelper(R.string.positive_button_edit_item);
        return dialog;
    }

    /**
     * Change selected list item name to hte editText input if it is not empty
     */
    @Override
    protected void doListEdit() {

    }
}
