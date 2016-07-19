package com.shoplist.myshoplistplus.activeListDetail;


import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.ShoppingList;
import com.shoplist.myshoplistplus.model.User;
import com.shoplist.myshoplistplus.utils.Constans;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract  class EditListDialogFragment extends DialogFragment {

    String mEncodedEmail;
    String mOwner;
    EditText mEditTextForList;
    int mResource;
    String mListId;
    HashMap mSharedWith;


    /**
     * Helper method that creates a basic bundle of all of the information needed to change values in a shopping list.
     * @param shoppingList The shopping list htat the dialog is editing
     * @param resource The xml layout file associated with the dialog
     * @param listId The id of the shopping list the dialog is editing
     * @param encodedEmail The encoded email of the current user
     * @param sharedWithUsers The HashMap containing all users that the current shopping list is shared with
     * @return The bundel containing all the arguments.
     */

    protected static Bundle newInstanceHelper(ShoppingList shoppingList, int resource, String listId, String encodedEmail,
                                              HashMap<String, User> sharedWithUsers){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constans.KEY_SHARED_WITH_USERS, sharedWithUsers);
        bundle.putString(Constans.KEY_LIST_ID, listId);
        bundle.putInt(Constans.KEY_LAYOUT_RSOURCE, resource);
        bundle.putString(Constans.KEY_LIST_OWNER, shoppingList.getOwner());
        bundle.putString(Constans.KEY_ENCODED_EMAIL, encodedEmail);
        return bundle;
    }


    /* Initialize instance variables with data from bundle */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedWith = (HashMap) getArguments().getSerializable(Constans.KEY_SHARED_WITH_USERS);
        mListId = getArguments().getString(Constans.KEY_LIST_ID);
        mResource = getArguments().getInt(Constans.KEY_LAYOUT_RSOURCE);
        mOwner = getArguments().getString(Constans.KEY_LIST_OWNER);
        mEncodedEmail = getArguments().getString(Constans.KEY_ENCODED_EMAIL);
    }

    /**
     * Open the keyboard automatically whn the dialog fragment is opened
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    protected Dialog createDialogHelper(int stringResourceForPositiveButton){
        /* Use the builder class for convenient dialog construction */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        /* Get the layout inflater */
        LayoutInflater inflater = getActivity().getLayoutInflater();
        /* Inflate the layout, set root ViewGroup to null */
        View rootView = inflater.inflate(mResource, null);
        mEditTextForList = (EditText) rootView.findViewById(R.id.edit_text_list_dialog);


        /**
         * Call doListEdit() when user taps "Done" keyboard action
         */
        mEditTextForList.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == event.ACTION_DOWN){
                    doListEdit();

                    /**
                     * Close the dialog fragment when done
                     */
                    EditListDialogFragment.this.getDialog().cancel();
                }

                return true;
            }
        });

        /**
         * Inflate and set the layout for the dialog
         * Pass null as the parent view becaus its going in the dialog layout
         */
        builder.setView(rootView)
                /* Add Action buttons*/
        .setPositiveButton(stringResourceForPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doListEdit();

                /* Close the dialog fragment */
                EditListDialogFragment.this.getDialog().cancel();
            }
        }).setNegativeButton(R.string.negative_button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /* Close the dialog fragment */
                EditListDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }


    /**
     * Set the EditText text to be the inputted text
     * and put the pointer at the end of the input
     *
     * @param defaultText
     */
    protected void helpSetDefaultValueEditEditText(String defaultText){
        mEditTextForList.setText(defaultText);
        mEditTextForList.setSelection(defaultText.length());
    }

    /**
     * Method to be overriden with whatever edit is supposed to happen to the list
     */
    protected abstract void doListEdit();
}
