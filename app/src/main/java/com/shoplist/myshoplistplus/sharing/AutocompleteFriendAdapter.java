package com.shoplist.myshoplistplus.sharing;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.User;
import com.shoplist.myshoplistplus.utils.Constans;
import com.shoplist.myshoplistplus.utils.Utils;


/**
 * Populates the list_view_friends_autocomplet inside AddFriendActivity
 */
public class AutocompleteFriendAdapter extends FirebaseListAdapter<User> {
    private String mEncodedEmail;


    /**
     * Public constructor that initializes private instance variables when adapter is created
     * @param activity
     * @param modelClass
     * @param modelLayout
     * @param ref
     */
    public AutocompleteFriendAdapter(Activity activity, Class<User> modelClass, int modelLayout, Query ref, String encodedEmail) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
        this.mEncodedEmail = encodedEmail;
    }

    /**
     * Protected method that populates the view attached to the adapter (list_view_friends_autocomplete)
     * with items inflated from single_autocomplet_item.xml
     * populateView also handles data changes and update the listView accordingly
     * @param v
     * @param user
     * @param position
     */

    @Override
    protected void populateView(View v, final User user, int position) {
        /* Get friends email textview and set it's text to user.email() */
        TextView textViewFriendEmail = (TextView) v.findViewById(R.id.text_view_autocomplete_item);
        textViewFriendEmail.setText(Utils.decodeEmail(user.getEmail()));

        /**
         * Set the onClickListener to a single list item
         * If selected email is not friend already and if is not the
         * current user's email, ve add selected user to current user's friends
         */
        textViewFriendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * If selected user is not current user proceed
                 */
                if (isNotCurrentUser(user)){
                    DatabaseReference currentUserFriendsRef = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_USER_FRIENDS).child(mEncodedEmail);
                    final DatabaseReference friendRef = currentUserFriendsRef.child(user.getEmail());

                    /**
                     * Add listener for single value event to perform a one time operation
                     */
                    friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            /**
                             * Add selected user to current user's friends if not in friends yet
                             */
                            if (isNotAlreadyAdded(dataSnapshot, user)){
                                friendRef.setValue(user);
                                mActivity.finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(mActivity.getClass().getSimpleName(), "Erro the lectura" + databaseError.getMessage());
                        }
                    });
                }
            }
        });
    }

    /**
     * Checks if the friend you try to add is the current user
     */
    private boolean isNotCurrentUser(User user){
        if (user.getEmail().equals(mEncodedEmail)){
            /* Toast appropriate error message if the user is trying to add themselves */
            Toast.makeText(mActivity, "You can't add yourself", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Checks if the friend you try to add is already added, given a dataSnapshot of a user
     */
    private boolean isNotAlreadyAdded(DataSnapshot dataSnapshot, User user){
        if (dataSnapshot.getValue(User.class) != null){
            /* Toast appropiate error message if the user is already a friend of the user */
            String friendError = String.format(mActivity.getResources().getString(R.string.toast_is_already_your_friend), user.getName());

            Toast.makeText(mActivity, friendError, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
