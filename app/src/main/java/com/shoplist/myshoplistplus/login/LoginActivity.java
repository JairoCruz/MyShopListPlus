package com.shoplist.myshoplistplus.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.shoplist.myshoplistplus.BaseActivity;
import com.shoplist.myshoplistplus.MainActivity;
import com.shoplist.myshoplistplus.R;
import com.shoplist.myshoplistplus.model.User;
import com.shoplist.myshoplistplus.utils.Constans;
import com.shoplist.myshoplistplus.utils.Utils;

import java.io.IOException;
import java.util.HashMap;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog mAuthProgressDialog;
    private EditText mEditTextEmailInput;
    private EditText mEditTextPasswordInput;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private DatabaseReference userLocation;

    /**
     * Variables related to Google Login
     */
    /* A flag indicating that a PendingIntent is in progress and prevents us from starting further intents. */
    private boolean mGoogleIntentInProgress;

    /* Request code used to invoke sign in user interactions for Google+ */
    public static final int RC_GOOGLE_LOGIN = 1;

    /* A Google account object that is populated if the user signs in with Google */
    GoogleSignInAccount mGoogleAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**
         * Link layout elements from XML and setup progress dialog
         */
        initializeScreen();

        mAuth = FirebaseAuth.getInstance();

        /**
         * Set up an AuthStateListener that responds to changes in the user's sign-in state
         */
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in: " + user.getUid());
                    /* Go to main Activity */
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    // User is signed out
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor spe = sp.edit();
                    spe.putString(Constans.KEY_ENCODED_EMAIL, null);
                    spe.putString(Constans.KEY_PROVIDER, null);
                }
            }
        };

        /**
         * Call signInPassword() when user taps "Done" keyboard action
         */
        mEditTextPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN){
                    signInPassword();
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor spe = sp.edit();
        Log.e(LOG_TAG, "Estoy en onResume"+ sp.getString(Constans.KEY_SIGNUP_EMAIL,null));
        /**
         * Get the newly registrered user email if present, use null as default value
         */
        String signupEmail = sp.getString(Constans.KEY_SIGNUP_EMAIL, null);

        /**
         * Fill in the email editExt and remove value from SharedPreferences if email is present
         */
        if (signupEmail != null){
            mEditTextEmailInput.setText(signupEmail);

            /**
             * Clear signupEmail sharedPreferences to make suere that they are used just once
             */
            spe.putString(Constans.KEY_SIGNUP_EMAIL, null).apply();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Override onCreateOptionsMenu to inflate nothing
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Sign in with Password provider when user clicks sign in button
     *
     */
    public void onSignInPressed(View view){
        signInPassword();
    }

    /**
     * Open CreateAccountActivity when user taps on "Sign up" TextView
     */
    public void onSignUpPressed(View view){
        Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
        startActivity(intent);
    }

    /**
     * Link layout elements from XML and setup the progress dialog
     *
     */
    public void initializeScreen(){
        mEditTextEmailInput = (EditText) findViewById(R.id.edit_text_email);
        mEditTextPasswordInput = (EditText) findViewById(R.id.edit_text_password);
        LinearLayout linearLayoutLoginActivity = (LinearLayout) findViewById(R.id.linear_layout_login_activity);
        initializeBackground(linearLayoutLoginActivity);

        /* Setup the progress dialog that is desplayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getString(R.string.progress_dialog_authenticating_with_firebase));
        mAuthProgressDialog.setCancelable(false);

        /* Setup Google Sign In */
        setupGoogleSignIn();

    }

    /**
     * Sign in with Password provider used when user taps "Done" action on Keyboard
     */
    public void signInPassword(){

        String email = mEditTextEmailInput.getText().toString();
        String password = mEditTextPasswordInput.getText().toString();

        /**
         * If email and password are not empty shwo progress dialog and try to authenticate
         */
        if (email.equals("")){
            mEditTextEmailInput.setError(getString(R.string.error_cannot_be_empty));
            return;
        }

        if (password.equals("")){
            mEditTextPasswordInput.setError(getString(R.string.error_cannot_be_empty));
            return;
        }
        mAuthProgressDialog.show();

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mAuthProgressDialog.dismiss();
                    Log.i(LOG_TAG, FirebaseAuthProvider.PROVIDER_ID +  getString(R.string.log_message_auth_successful));

                    AuthResult ar = task.getResult();
                    mEncodedEmail = Utils.encodeEmail(ar.getUser().getEmail());

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor spe = sp.edit();

                    Log.e("jairo email", mEncodedEmail);

                    spe.putString(Constans.KEY_PROVIDER, FirebaseAuthProvider.PROVIDER_ID).apply();
                    spe.putString(Constans.KEY_ENCODED_EMAIL, mEncodedEmail).apply();



                    /* Go to main Activity *//*
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();*/

                    userLocation = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_USERS).child(mEncodedEmail);

                    /**
                     * check if current user has logged in at least once
                     */
                    userLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null){
                                /**
                                 * If recently registered user has hasLoggedInWithPassword = "false"
                                 * (never logged in using password provider)
                                 */
                                if (!user.isHasLoggedInWithPassword()){
                                    /**
                                     * Change password if user that just signed in signed up recently
                                     * to make sure that  user will be able to use temporary password
                                     * from the email more that 24 hours
                                     */
                                    FirebaseUser userUpdatePassword = FirebaseAuth.getInstance().getCurrentUser();
                                    userUpdatePassword.updatePassword(mEditTextPasswordInput.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Log.d(LOG_TAG, "User password updated");
                                                userLocation.child(Constans.FIREBASE_PROPERTY_USER_HAS_LOGGED_IN_WITH_PASSWORD).setValue(true);
                                                /* the password was changed */
                                                Log.d(LOG_TAG,getString(R.string.log_message_password_changed_successfully) + mEditTextPasswordInput.getText().toString());
                                            }else{
                                                Log.d(LOG_TAG, getString(R.string.log_error_failed_to_change_password));
                                            }
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(LOG_TAG, "Error de lectura" + databaseError.getMessage());
                        }
                    });
                }else {
                    mAuthProgressDialog.dismiss();
                    showErrorToast(task.getException().getMessage());
                }
            }
        });

    }

    /**
     * Helper method that makes sure a user is created if the user
     * logs in with Firebase's email/password provider.
     * authData AuthData object returned from onAuthenticated
     */
    private void setAuthnticatedUserPasswrodProvider(/*AuthData authData*/){

    }

    /**
     * Helper method that makes suere a user is created if the user
     * logs in with Firebase's Google login provider.
     * authData AuthData object returned from onAuthenticated
     */
    private void setAuthenticatedUserGoogle(/*AuthData authData*/){

    }

    /**
     * Show error toast to users
     */
    private void showErrorToast(String message){
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Signs you into ShoppingList++ using the Google Login Provider
     * @param token A Google OAuth access token returned from Google
     */
    private void loginWithGoogle(String token){
        /**
         * Obtener sha-1
         * C:\Program Files\Java\jdk1.8.0_20\bin>keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
         */

    }

    /* Sets up the Google Sign In Button*/
    private void setupGoogleSignIn(){
        SignInButton signInButton = (SignInButton) findViewById(R.id.login_with_google);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInGooglePressed(v);
            }
        });
    }

    /**
     * Sign in with Google plus when user clicks "Sign in with Google" textView (button)
     */
    public void onSignInGooglePressed(View view){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_LOGIN);
        mAuthProgressDialog.show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result){
        /**
         * An unresolvable error has occurred and Google APIs (including Sign-In) will not
         * be available.
         */
        mAuthProgressDialog.dismiss();
        showErrorToast(result.toString());
    }

    /**
     * This callback is triggered when any startActivityForResult finishes. The requestCode maps to the value
     * passed into startActivityForResult.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...); */
        if (requestCode == RC_GOOGLE_LOGIN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    private void handleSignInResult(GoogleSignInResult result){
        Log.d(LOG_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()){
            /* Signed in successfully, get the OAuth token */
            mGoogleAccount = result.getSignInAccount();
            Log.e("mis datos", mGoogleAccount.getDisplayName()+ " " + mGoogleAccount.getEmail());

            //getGoogleOAuthTokenAndLogin();
            firebaseAuthWithGoogle(mGoogleAccount);
        }else{
            if (result.getStatus().getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED){
                showErrorToast("The sign in was cancelled. Make sure you're connected to the internet and try again.");
            }else{
                showErrorToast("Error hanling the sign in: " + result.getStatus().getStatusMessage());
            }
            mAuthProgressDialog.dismiss();
        }
    }


    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct){
        Log.e(LOG_TAG, "FirebaseAuthWithGoogle: " + acct.getId());

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Log.d(LOG_TAG, "signInWithCredential:onComplet: " + task.isSuccessful());
                if (task.isSuccessful()){
                    Log.d(LOG_TAG, "signInWithCredential:onComplet: " + task.isSuccessful());


                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor spe = sp.edit();
                    String unprocessedEmail;
                    if (mGoogleApiClient.isConnected()){
                        unprocessedEmail = mGoogleAccount.getEmail().toLowerCase();
                        spe.putString(Constans.KEY_GOOGLE_EMAIL, unprocessedEmail).apply();
                    }else{
                        unprocessedEmail = sp.getString(Constans.KEY_GOOGLE_EMAIL, null);
                    }
                    mEncodedEmail = Utils.encodeEmail(unprocessedEmail);

                    final String userName = mGoogleAccount.getDisplayName();

                    userLocation = FirebaseDatabase.getInstance().getReference(Constans.FIREBASE_LOCATION_USERS).child(mEncodedEmail);
                    userLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null){
                                HashMap<String, Object> timestampJoined = new HashMap<>();
                                timestampJoined.put(Constans.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
                                User newUser = new User(userName, mEncodedEmail, timestampJoined);
                                userLocation.setValue(newUser);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(LOG_TAG, getString(R.string.log_error_occurred) + databaseError.getMessage());
                        }
                    });

                    spe.putString(Constans.KEY_PROVIDER, credential.getProvider()).apply();
                    spe.putString(Constans.KEY_ENCODED_EMAIL, mEncodedEmail).apply();

                    /* Una vez que inicio session con el boton de Google  de
                    * una sola vez actualize el profile de Auth de Firebase, aparte de actualizar mi objeto Users de la database
                    * asi cuando estoy en el main activity recupero de Auth Firebase los datos de el nombre */
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    if (firebaseUser.getDisplayName() == null) {
                        updateUserProfile(firebaseUser, acct.getDisplayName());
                    }


                    showErrorToast("bien hecho");
                    mAuthProgressDialog.dismiss();
                     /* Go to main Activity */
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    mAuthProgressDialog.dismiss();
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
     * Gets the GoogleAuthToken and logs in.
     */

    // Este metodo no lo utilizo, ya que segui la nueva guia de Firebase
    private void getGoogleOAuthTokenAndLogin(){
        /* Get OAuth token in Background */
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

            String mErrorMessage = null;

            @Override
            protected String doInBackground(Void... params) {
                String token = null;

                try{
                    String scope = String.format(getString(R.string.oauth2_format), new Scope(Scopes.PROFILE)) + "email";

                    token = GoogleAuthUtil.getToken(LoginActivity.this, mGoogleAccount.getEmail(), scope);
                }catch (IOException transientEx){
                    /* Network or server error */
                    Log.e(LOG_TAG, getString(R.string.google_error_auth_with_google) + transientEx);
                    mErrorMessage = getString(R.string.google_error_network_error) + transientEx.getMessage();
                }catch (UserRecoverableAuthException e){
                    Log.w(LOG_TAG, getString(R.string.google_error_recoverable_oauth_error) + e.toString());

                    /* We probably need to ask for permissions, so start the intent if there is none pending */
                    if (!mGoogleIntentInProgress){
                        mGoogleIntentInProgress = true;
                        Intent recover = e.getIntent();
                        startActivityForResult(recover, RC_GOOGLE_LOGIN);
                    }
                }catch (GoogleAuthException authEx){
                    /**
                     * The call is not ever expected to succeed assuming you have already verified that
                     * Google Play services is installed.
                     */
                Log.e(LOG_TAG, " " + authEx.getMessage(), authEx);
                    mErrorMessage = getString(R.string.google_error_auth_with_google) + authEx.getMessage();
                }
                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                mAuthProgressDialog.dismiss();
                if (token != null){
                    /* Successfully got OAuth token, now login with Google */
                    loginWithGoogle(token);
                }else if (mErrorMessage != null ){
                    showErrorToast(mErrorMessage);
                }
            }
        };

        task.execute();
    }
}

