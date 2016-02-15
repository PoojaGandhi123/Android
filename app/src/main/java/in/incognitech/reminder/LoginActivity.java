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
import com.google.android.gms.common.api.ResultCallback;

import java.util.Map;

import in.incognitech.reminder.util.Constants;
import in.incognitech.reminder.util.HashGenerator;
import in.incognitech.reminder.util.image.ImageCache;
import in.incognitech.reminder.util.image.ImageFetcher;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 3001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private ImageFetcher mImageFetcher;

    private Firebase firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupGoogleSignIn();

        setupImageCache();

        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(Constants.FIREBASE_APP_URL);

        findViewById(R.id.sign_in_button).setOnClickListener(new SignInButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
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

        ImageView imageView = (ImageView) findViewById(R.id.login_background);
        mImageFetcher.loadImage("https://assetcdn2.500px.org/assets/home/home_cover-15196e45d21c537dc47edb5ea028db85.jpg", imageView);

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
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
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
            final String displayName = acct.getDisplayName();
            final String email = acct.getEmail();
            final Uri photoUrl = acct.getPhotoUrl();

            final String password = HashGenerator.generateMD5(email.toLowerCase().trim());
            firebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {

                @Override
                public void onAuthenticated(AuthData authData) {
                    System.out.println(authData);
//                  System.out.println("Successfully created user account with uid: " + result.get("uid"));
                    LoginActivity.this.hideProgressDialog();

                    LoginActivity.this.redirectToHome(email, displayName, photoUrl);
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    String message = "";
                    switch (firebaseError.getCode()) {
                        case FirebaseError.USER_DOES_NOT_EXIST:
                            LoginActivity.this.firebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                                @Override
                                public void onSuccess(Map<String, Object> result) {
                                    System.out.println(result);
                                    System.out.println("Successfully created user account with uid: " + result.get("uid"));
                                    LoginActivity.this.hideProgressDialog();
                                    LoginActivity.this.redirectToHome(email, displayName, photoUrl);
                                }

                                @Override
                                public void onError(FirebaseError firebaseError) {
                                    // there was an error
                                    Toast.makeText(LoginActivity.this, firebaseError.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        default:
                            message = firebaseError.toString();
                            break;
                    }
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });

        } else {
            // Signed out, show unauthenticated UI.
            // Handle Error
        }
    }

    private void redirectToHome(String email, String displayName, Uri photoUrl) {
        Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
        homeIntent.putExtra("displayName", displayName);
        homeIntent.putExtra("email", email);
        if (photoUrl != null) {
            homeIntent.putExtra("photoUrl", photoUrl.toString());
        } else {
            homeIntent.putExtra("photoUrl", "https://secure.gravatar.com/avatar/" + HashGenerator.generateMD5(email.toLowerCase().trim()));
        }
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
}
