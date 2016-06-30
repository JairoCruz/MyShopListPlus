package com.shoplist.myshoplistplus.login;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.shoplist.myshoplistplus.BaseActivity;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.User;
import com.shoplist.myshoplistplus.utils.Constans;
import com.shoplist.myshoplistplus.utils.Utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;

public class CreateAccountActivity extends BaseActivity {


    private static final String LOG_TAG = CreateAccountActivity.class.getSimpleName();
    private ProgressDialog mAuthProgressDialog;
    private EditText mEditTextUsernameCreate;
    private EditText mEditTextEmailCreate;
    private String mUserName;
    private String mUserEmail;
    private String mPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference userLocation;
    private SecureRandom mRandom = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        /**
         * Create Firebase references
         */
        mAuth = FirebaseAuth.getInstance() ;

        /**
         * Link layout elements form XML and setup the progress dialog
         */
        initializeScreen();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d(LOG_TAG, "usuario se logego");
                }else{
                    Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * Link layout elements from XML and setup the progress dialog
     */
    public void initializeScreen(){
        mEditTextUsernameCreate = (EditText) findViewById(R.id.edit_text_username_create);
        mEditTextEmailCreate = (EditText) findViewById(R.id.edit_text_email_create);


        LinearLayout linearLayoutCreateAccountActivity = (LinearLayout) findViewById(R.id.linear_layout_create_account_activity);
        initializeBackground(linearLayoutCreateAccountActivity);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getResources().getString(R.string.progress_dialog_check_inbox));
        mAuthProgressDialog.setCancelable(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
       //  mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Open LoginActivity when user taps on "Sign in" textView
     */
    public void onSignInPressed(View view){
        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Create new account usign Firebase email/password provider
     */
    public void onCreateAccountPressed(View view){
        mUserName = mEditTextUsernameCreate.getText().toString();
        mUserEmail = mEditTextEmailCreate.getText().toString().toLowerCase();
        mPassword = new BigInteger(130, mRandom).toString(32);

        /**
         * Check that email and user name are okay
         */
        boolean validEmail = isEmailValid(mUserEmail);
        boolean validUserName = isUserNameValid(mUserName);


        if (!validEmail || !validUserName) return;

        /**
         * If everything was valid show the progress dialog to indicate that
         * account creation has started
         */
        mAuthProgressDialog.show();

        /**
         * Create new user with specified Email and password
         */
        mAuth.createUserWithEmailAndPassword(mUserEmail, mPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    Log.i(LOG_TAG, getString(R.string.log_message_auth_successful));



                    FirebaseUser user = task.getResult().getUser();

                    updateUserProfile(user, mUserName);
                    /* *//* Create de User Data *//*
                    AuthResult authResult = task.getResult();
                    String uid = authResult.getUser().getUid();*/
                    createUserInFirebaseHelper();

                    /* Reset Password */
                    mAuth.sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d(LOG_TAG, "Email sent.");
                                /* Dismiss the progress dialog */
                                mAuthProgressDialog.dismiss();

                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CreateAccountActivity.this);
                                SharedPreferences.Editor spe = sp.edit();

                                /**
                                 * Save name and email to sharedPrefernces to create User database record
                                 * when the registrered user will sign in for the first time
                                 */
                                spe.putString(Constans.KEY_SIGNUP_EMAIL, mUserEmail).apply();


                            }
                        }
                    });



                    /**
                     * Password reset email sent, open app chooser to pick app
                     * for handling inbox email intent
                     */
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_APP_EMAIL);

                    try{
                        Log.e(LOG_TAG,"voy saliendo de aqui");
                        /* If successful create user then log out */
                        FirebaseAuth.getInstance().signOut();
                        startActivity(intent);
                        finish();
                    }catch (android.content.ActivityNotFoundException ex){
                        Log.e("error en abrir email", ex.getMessage());
                    }


                }else{
                    /* Error occurred, log the error and dismiss the progress dialog */
                    mAuthProgressDialog.dismiss();
                    Log.d(LOG_TAG, getString(R.string.log_error_occurred) + task.getException());
                    /* Display the appropriate error message */
                    FirebaseAuthException l = (FirebaseAuthException) task.getException();
                    Log.e("error ver", l.getErrorCode());
                    showErrorToast(task.getException().getMessage());
                }
            }
        });

    }

    public void updateUserProfile(FirebaseUser user, String userName){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build();
        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.e(LOG_TAG, "User profile updated. ");
                }
            }
        });
    }

    /**
     * Creates a new user in Firebase form the Java POJO
     */
    private void createUserInFirebaseHelper(){
        /**
         * Con este cambio ahora coloco como Key el Email del usuario, debo recordar que creo el usuario en la db de firebase y en
         * Auth de FireBase.
         */
        final String encodedEmail = Utils.encodeEmail(mUserEmail);
        userLocation = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_USERS).child(encodedEmail);
        /**
         * See if there is already a user (for example, if they already logged in with an associated
         * Google account.
         */
        userLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /* If there is no user, make one */
                if (dataSnapshot.getValue() == null){
                    Log.e("Desde el creado objeto","voy aqui");
                    /* Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap */
                    HashMap<String, Object> timestampJoined = new HashMap<>();
                    timestampJoined.put(Constans.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    User newUser = new User(mUserName, encodedEmail, timestampJoined);
                    userLocation.setValue(newUser);
                }else{
                    Log.e("crear","no");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(LOG_TAG, getString(R.string.log_error_occurred) + databaseError.getMessage());
            }
        });

    }

    private boolean isEmailValid(String email){
        boolean isGoodEmail = (email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail){
            mEditTextEmailCreate.setError(String.format(getString(R.string.error_invalid_email_not_valid), email));
            return isGoodEmail;
        }
        return isGoodEmail;
    }

    private boolean isUserNameValid(String userName){
        if (userName.equals("")){
            mEditTextUsernameCreate.setError(getResources().getString(R.string.error_cannot_be_empty));
            return false;
        }
        return true;
    }



    /**
     * Show error toast to users
     */
    private void showErrorToast(String message){
        Toast.makeText(CreateAccountActivity.this, message, Toast.LENGTH_LONG).show();
    }
}
