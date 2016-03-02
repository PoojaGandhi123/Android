package in.incognitech.reminder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.Map;

import in.incognitech.reminder.api.FirebaseAPI;
import in.incognitech.reminder.model.User;
import in.incognitech.reminder.provider.StockImageFetcher;
import in.incognitech.reminder.util.ActivityImageFetcherBridge;
import in.incognitech.reminder.util.Constants;
import in.incognitech.reminder.util.HashGenerator;
import in.incognitech.reminder.util.Utils;
import in.incognitech.reminder.util.image.ImageCache;
import in.incognitech.reminder.util.image.ImageFetcher;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, ActivityImageFetcherBridge {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 3001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private ImageFetcher mImageFetcher;

    private Firebase firebaseRef;

    private boolean imageDone = false;
    private boolean loginDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupGoogleSignIn();

        setupImageCache();

        FirebaseAPI.setAndroidContext(this);
        firebaseRef = FirebaseAPI.getInstance();

        findViewById(R.id.sign_in_button).setOnClickListener(new SignInButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    public boolean isLoginDone() {
        return loginDone;
    }

    public void setLoginDone(boolean loginDone) {
        this.loginDone = loginDone;
    }

    public boolean isImageDone() {
        return imageDone;
    }

    public void setImageDone(boolean imageDone) {
        this.imageDone = imageDone;
    }

    private void setupImageCache() {
        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, Constants.IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        mImageFetcher = new ImageFetcher(this, (int) getResources().getDimension(R.dimen.login_background_width), (int) getResources().getDimension(R.dimen.login_background_height));
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
    }

    private void setupGoogleSignIn() {
        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]
    }

    @Override
    public void onStart() {
        super.onStart();

        showProgressDialog();
        StockImageFetcher stockImageFetcher = new StockImageFetcher(this);
        stockImageFetcher.execute();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            if(!mProgressDialog.isShowing()) {
                showProgressDialog();
            }
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {

                    if(isImageDone()) {
                        hideProgressDialog();
                    }
                    setLoginDone(true);
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, Go to next step
            GoogleSignInAccount acct = result.getSignInAccount();
            String displayName = acct.getDisplayName();
            String email = acct.getEmail();
            Uri photoUrl = acct.getPhotoUrl();

            if ( Utils.getCurrentUserID(this).equals("") ) {
                loginOnFirebase(email, displayName, photoUrl);
            } else {
                if(isImageDone()) {
                    hideProgressDialog();
                }
                setLoginDone(true);
                redirectToHome(email, displayName, photoUrl);
            }

        } else {
            // Signed out, show unauthenticated UI.
            // Handle Error
        }
    }

    private void loginOnFirebase(final String email, final String displayName, final Uri photoUrl) {
        final String password = HashGenerator.generateMD5(email.toLowerCase().trim());
        firebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {

            @Override
            public void onAuthenticated(AuthData authData) {
                Utils.setCurrentUserID(LoginActivity.this, authData.getUid());

                User.setIsActive(authData.getUid(), true);

                if (isImageDone()) {
                    hideProgressDialog();
                }
                setLoginDone(true);

                redirectToHome(email, displayName, photoUrl);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                String message = "";
                switch (firebaseError.getCode()) {
                    case FirebaseError.USER_DOES_NOT_EXIST:
                        firebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                            @Override
                            public void onSuccess(Map<String, Object> result) {
                                Utils.setCurrentUserID(LoginActivity.this, (String) result.get("uid"));

                                User.setIsActive((String) result.get("uid"), true);

                                if (isImageDone()) {
                                    hideProgressDialog();
                                }
                                setLoginDone(true);

                                redirectToHome(email, displayName, photoUrl);
                            }

                            @Override
                            public void onError(FirebaseError firebaseError) {
                                // there was an error
                                PendingResult<Status> pr = Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                Toast.makeText(LoginActivity.this, firebaseError.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    default:
                        message = firebaseError.toString();
                        break;
                }
                PendingResult<Status> pr = Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void redirectToHome(String email, String displayName, Uri photoUrl) {

        Utils.setCurrentUserDisplayName(this, displayName);
        Utils.setCurrentUserEmail(this, email);
        Utils.setCurrentUserPhotoUrl(this, photoUrl != null ? photoUrl.toString() : "https://secure.gravatar.com/avatar/" + HashGenerator.generateMD5(email.toLowerCase().trim()));

        Intent homeIntent = new Intent(this, OutgoingRemindersActivity.class);
        startActivity(homeIntent);
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult.toString());// An unresolvable error has occurred and Google APIs (including Sign-In) will not
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void loadImage(String imageUri) {
        ImageView imageView = (ImageView) findViewById(R.id.login_background);
        mImageFetcher.loadImage(imageUri, imageView);
        hideProgressDialog();
        if(isLoginDone()) {
            hideProgressDialog();
        }
        setImageDone(true);
    }
}
