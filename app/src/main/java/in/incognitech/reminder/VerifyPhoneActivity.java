package in.incognitech.reminder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import in.incognitech.reminder.util.Constants;
import io.fabric.sdk.android.Fabric;

public class VerifyPhoneActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "3FLAiWQI23gi6INIial3MLDaM";
    private static final String TWITTER_SECRET = "p4JPLZMy9JdHiYzVwS9jPTctCIIJDairgDHAMSj0cKO65nYyDc";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits());

        sharedPreferences = getSharedPreferences(Constants.shared_prefs_key, Activity.MODE_PRIVATE);
        sharedPreferencesEditor = getSharedPreferences( Constants.shared_prefs_key, Activity.MODE_PRIVATE ).edit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        DigitsAuthButton digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);
        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // TODO: associate the session userID with your user model
//                Toast.makeText(getApplicationContext(), "Authentication successful for " + phoneNumber, Toast.LENGTH_LONG).show();
                sharedPreferencesEditor.putString( Constants.verifiedPhoneNumberKey, phoneNumber );
                sharedPreferencesEditor.commit();
            }

            @Override
            public void failure(DigitsException exception) {
                Log.d("Digits", "Sign in with Digits failure", exception);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        String verifiedPhoneNumber = sharedPreferences.getString(Constants.verifiedPhoneNumberKey, "");
        if ( ! verifiedPhoneNumber.equals("") ) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            // show unauthenticated UI.
//            updateUI(false);
        }
    }
}
