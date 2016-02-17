package in.incognitech.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;

import in.incognitech.reminder.api.FirebaseAPI;
import in.incognitech.reminder.util.Constants;
import in.incognitech.reminder.util.FontAwesomeManager;
import in.incognitech.reminder.util.TextDrawable;
import in.incognitech.reminder.util.Utils;
import in.incognitech.reminder.util.image.ImageCache;
import in.incognitech.reminder.util.image.ImageFetcher;

/**
 * Created by udit on 16/02/16.
 */
public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    protected static GoogleApiClient mGoogleApiClient;

    protected ImageFetcher mImageFetcher;

    protected Firebase firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAPI.setAndroidContext(this);
        firebaseRef = FirebaseAPI.getInstance();

        setupGoogleSignIn();

        setupImageCache();

        this.customSetup(R.id.toolbar, R.id.nav_view);
    }

    protected void customSetup(int toolBarID, int navViewID) {
        Toolbar toolbar = (Toolbar) findViewById(toolBarID);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(navViewID);
        navigationView.setNavigationItemSelectedListener(this);
        final View headerView = navigationView.getHeaderView(0);

        this.setDrawerMenuIcons(navigationView.getMenu());

        String displayName = Utils.getCurrentUserDisplayName(this);
        String email = Utils.getCurrentUserEmail(this);
        String photoUrl = Utils.getCurrentUserPhotoUrl(this);

        if ( ! displayName.equals("") ) {
            ((TextView) headerView.findViewById(R.id.userDisplayName)).setText(displayName);
        }

        if ( ! email.equals("") ) {
            ((TextView) headerView.findViewById(R.id.userEmail)).setText(email);
        }

        if ( ! photoUrl.equals("") ) {
            ImageView imageView = (ImageView) headerView.findViewById(R.id.userAvatar);
            mImageFetcher.loadImage(photoUrl, imageView);
        }
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

    private void setupImageCache() {
        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, Constants.IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        mImageFetcher = new ImageFetcher(this, (int) getResources().getDimension(R.dimen.user_avatar_width), (int) getResources().getDimension(R.dimen.user_avatar_height));
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
    }

    protected void setDrawerMenuIcons(Menu menu) {
        TextDrawable faIcon = new TextDrawable(this);
        faIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        faIcon.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        faIcon.setTypeface(FontAwesomeManager.getTypeface(this, FontAwesomeManager.FONTAWESOME));
        faIcon.setText(getResources().getText(R.string.fa_arrow_left));
        menu.findItem(R.id.nav_outgoing_reminders).setIcon(faIcon);

        faIcon = new TextDrawable(this);
        faIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        faIcon.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        faIcon.setTypeface(FontAwesomeManager.getTypeface(this, FontAwesomeManager.FONTAWESOME));
        faIcon.setText(getResources().getText(R.string.fa_arrow_right));
        menu.findItem(R.id.nav_incoming_reminders).setIcon(faIcon);

        faIcon = new TextDrawable(this);
        faIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        faIcon.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        faIcon.setTypeface(FontAwesomeManager.getTypeface(this, FontAwesomeManager.FONTAWESOME));
        faIcon.setText(getResources().getText(R.string.fa_users));
        menu.findItem(R.id.nav_friends).setIcon(faIcon);

        faIcon = new TextDrawable(this);
        faIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        faIcon.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        faIcon.setTypeface(FontAwesomeManager.getTypeface(this, FontAwesomeManager.FONTAWESOME));
        faIcon.setText(getResources().getText(R.string.fa_sign_out));
        menu.findItem(R.id.nav_logout).setIcon(faIcon);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_outgoing_reminders:
                startActivity(new Intent(this, OutgoingRemindersActivity.class));
                finish();
                break;
            case R.id.nav_incoming_reminders:
                startActivity(new Intent(this, IncomingRemindersActivity.class));
                finish();
                break;
            case R.id.nav_logout:
                this.logoutUser();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        firebaseRef.unauth();
        Utils.setCurrentUserID(this, "");
        Utils.setCurrentUserDisplayName(this, "");
        Utils.setCurrentUserEmail(this, "");
        Utils.setCurrentUserPhotoUrl(this, "");
        Toast.makeText(getApplicationContext(), "Logged out successfully", Toast.LENGTH_LONG).show();
        PendingResult<Status> pr = Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
