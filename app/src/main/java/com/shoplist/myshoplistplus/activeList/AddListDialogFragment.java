package com.shoplist.myshoplistplus.activeList;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.ShoppingList;
import com.shoplist.myshoplistplus.utils.Constans;
import com.shoplist.myshoplistplus.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by TSE on 03/06/2016.
 */
public class AddListDialogFragment extends DialogFragment {
    String mEncodedEmail;
    EditText mEditTextListName;

    /*
    *  Public static constructor that create fragment and
    *  passes a bundle with data into it when adapter is created
    *  */
    public static AddListDialogFragment newInstance(String encodedEmail){
        AddListDialogFragment addListDialogFragment = new AddListDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constans.KEY_ENCODED_EMAIL, encodedEmail);
        addListDialogFragment.setArguments(bundle);
        return addListDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Aca recivo el dato enviado desde el boton en la activida main
        mEncodedEmail = getArguments().getString(Constans.KEY_ENCODED_EMAIL);
    }

    /*
    *
    *  Open the keyboard automatically when the dialog fragment is opened
    * */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        // Use the Builder class for convenient dialog construction
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_add_list, null);
        mEditTextListName = (EditText) rootView.findViewById(R.id.edit_text_list_name);

        /*
        Call addShoppingList() when user taps "done" keyboard action
        * */
        mEditTextListName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == event.ACTION_DOWN){
                    addShoppingList();
                }
                return true;
            }
        });


        /*Inflate and set the layout for the dialog
        * Pass null as the parent view because its going in the dialog layout
        * */

        builder.setView(rootView).setPositiveButton(R.string.positive_button_create, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // method add Shopping List
                addShoppingList();
            }
        });

        // Add button cancel
        builder.setView(rootView).setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    private void addShoppingList() {
        String userEnteredName = mEditTextListName.getText().toString();


//        If EditText input is not empty
        if (!userEnteredName.equals("")){
           /*Create Firebase references*/

           DatabaseReference userListRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_USER_LISTS).child(mEncodedEmail);
            final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference newListRef = userListRef.push();



            /* Save listsRef.push to maintain same randow Id */
            final String listId = newListRef.getKey();


            /**
             * HasMap for data to update
             */
            HashMap<String, Object> updateShoppingListData = new HashMap<>();

            /**
             * Set raw version of date to the ServerValue.TIMESTAMP value and save into
             * timestampCreatedMap
             */
            HashMap<String, Object> timestampCreated = new HashMap<>();
            timestampCreated.put(Constans.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

            /* Buil the shopping List */
            ShoppingList newShoppingList = new ShoppingList(userEnteredName, mEncodedEmail, timestampCreated);

           HashMap<String, Object> shoppingListMap = (HashMap<String, Object>) new ObjectMapper().convertValue(newShoppingList, Map.class);
            Utils.updateMapForAllWithValue(null ,listId, mEncodedEmail, updateShoppingListData, "", shoppingListMap);

            firebaseRef.updateChildren(updateShoppingListData, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    /* Now that we have the timestamp, update the resersed timestamp */
                    Utils.updateTimestampReversed(databaseError, "AddList", listId, null, mEncodedEmail);
                }
            });

            /* Form short set method push() and setvalue()*/
            /* myRef.push().setValue(newShoppingList); */



/*
        Close the dialog fragment
*/
            AddListDialogFragment.this.getDialog().cancel();
        }

    }
}
