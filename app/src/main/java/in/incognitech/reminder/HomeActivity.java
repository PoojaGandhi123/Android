package in.incognitech.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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

import in.incognitech.reminder.util.Constants;
import in.incognitech.reminder.util.FontAwesomeManager;
import in.incognitech.reminder.util.TextDrawable;
import in.incognitech.reminder.util.image.ImageCache;
import in.incognitech.reminder.util.image.ImageFetcher;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private static GoogleApiClient mGoogleApiClient;

    private ImageFetcher mImageFetcher;

    private Firebase firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupGoogleSignIn();

        setupImageCache();

        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(Constants.FIREBASE_APP_URL);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final View headerView = navigationView.getHeaderView(0);

        setDrawerMenuIcons(navigationView.getMenu());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String displayName = extras.getString("displayName");
            String email = extras.getString("email");
            String photoUrl = extras.getString("photoUrl");

            if ( photoUrl != null ) {
                ImageView imageView = (ImageView) headerView.findViewById(R.id.userAvatar);
                mImageFetcher.loadImage(photoUrl, imageView);
            }

            ((TextView) headerView.findViewById(R.id.userDisplayName)).setText(displayName);
            ((TextView) headerView.findViewById(R.id.userEmail)).setText(email);
        }
    }

    private void setupImageCache() {
        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, Constants.IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        mImageFetcher = new ImageFetcher(this, (int) getResources().getDimension(R.dimen.user_avatar_width), (int) getResources().getDimension(R.dimen.user_avatar_height));
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

    private void setDrawerMenuIcons(Menu menu) {
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
        faIcon.setText(getResources().getText(R.string.fa_sign_out));
        menu.findItem(R.id.nav_logout).setIcon(faIcon);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout ) {
            firebaseRef.unauth();
            this.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        Toast.makeText(getApplicationContext(), "Logged out successfully", Toast.LENGTH_LONG).show();
        PendingResult<Status> pr = Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
