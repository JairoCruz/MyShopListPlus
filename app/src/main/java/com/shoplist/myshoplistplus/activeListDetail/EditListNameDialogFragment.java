package com.shoplist.myshoplistplus.activeListDetail;

import android.app.Dialog;
import android.os.Bundle;

import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.ShoppingList;

/**
 * Created by TSE on 13/06/2016.
 */
public class EditListNameDialogFragment extends EditListDialogFragment {

    private static final String LOG_TAG = ActiveListDetailsActivity.class.getSimpleName();

    /**
     * Public static constructor that creates fragment and passes a bundle with data into it when adapter is created
     */
    public static EditListNameDialogFragment newInstance(ShoppingList shoppingList){
        EditListNameDialogFragment editListNameDialogFragment = new EditListNameDialogFragment();
        Bundle bundle = EditListDialogFragment.newInstanceHelper(shoppingList, R.layout.dialog_edit_list);
        editListNameDialogFragment.setArguments(bundle);
        return editListNameDialogFragment;
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
         * EditListDialogFragment#createDialogHelper(int) is a superclass
         * method that creates the dialog
         */
        Dialog dialog = super.createDialogHelper(R.string.positive_button_edit_item);
        return dialog;
    }

    /**
     * Changes the list name in all copies of the current list
     */

    @Override
    protected void doListEdit() {

    }
}
