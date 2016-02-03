package in.incognitech.reminder.api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import in.incognitech.reminder.HomeActivity;
import in.incognitech.reminder.LoginActivity;
import in.incognitech.reminder.util.Constants;

/**
 * Created by udit on 24/01/16.
 */
public class RegisterUserAPI extends AsyncTask<Map<String,Object>, Void, String> {

    private static final String TAG = RegisterUserAPI.class.getSimpleName();

    private String nonce;

    private ProgressDialog mProgressDialog;
    private Activity mParent;
    private GoogleApiClient mGoogleApiClient;

    public RegisterUserAPI( Activity mParent, GoogleApiClient mGoogleApiClient, ProgressDialog mProgressDialog ) {
        this.mParent = mParent;
        this.mGoogleApiClient = mGoogleApiClient;
        this.mProgressDialog = mProgressDialog;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    @Override
    protected String doInBackground(Map<String,Object>... params) {
        Map<String, Object> userData = null;
        if ( params.length > 0 ) {
            userData = params[0];
        }
        return this.registerUser( userData );
    }

    @Override
    protected void onPreExecute() {
        AsyncTask<Void, Void, String> apiNonce = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return RegisterUserAPI.this.requestNonce();
            }

            @Override
            protected void onPostExecute(String nonceResponse) {
                Log.d("nonce Res", nonceResponse);
                try {
                    Log.d("within try", nonceResponse);
                    JSONObject nonceJSON = new JSONObject( nonceResponse );
                    Log.d("after object",nonceJSON.toString());
                    String nonceStr = nonceJSON.getString( "nonce" );
                    Log.d("nonce str",nonceStr);
                    RegisterUserAPI.this.setNonce(nonceStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onPostExecute(nonceResponse);
            }
        };
        apiNonce.execute();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String registerResponse) {
        Log.d("register Res",registerResponse);
        System.out.println(registerResponse);
        try {
            Log.d("within try",registerResponse);
            JSONObject registerJSON = new JSONObject( registerResponse );
            Log.d("after json",registerJSON.toString());
            JSONObject dataJSON = registerJSON.getJSONObject( "data" );
            int status = dataJSON.getInt("status");
            String message = registerJSON.getString("message");
            if ( status == 401 ) {
                Toast.makeText( mParent.getApplicationContext(), message, Toast.LENGTH_LONG ).show();
                PendingResult<com.google.android.gms.common.api.Status> pr = Auth.GoogleSignInApi.signOut(mGoogleApiClient);

                mParent.startActivity(new Intent(mParent, LoginActivity.class));
                mParent.finish();
            } else {
                Log.d(TAG,registerResponse);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
        super.onPostExecute(registerResponse);
    }

    private String requestNonce() {
        URL url = null;
        HttpURLConnection request = null;
        String response = "";
        try {
            url = new URL( Constants.WP_API_REQUEST_NONCE_ENDPOINT );
            request = (HttpURLConnection) url.openConnection();

            request.setReadTimeout(Constants.WP_API_READ_TIMEOUT /* milliseconds */);
            request.setConnectTimeout(Constants.WP_API_CONNECT_TIMEOUT /* milliseconds */);
            request.setRequestMethod("GET");
            request.setDoInput(true);

            request.connect();

            if ( request.getResponseCode() == 200 ) {
                InputStream is = request.getInputStream();
                // Convert the InputStream into a string
                response = this.convertInputStreamToString(is);
            }

        } catch (IOException e) {
            request.disconnect();
            Log.d(TAG, e.toString());
        }
        return response;
    }

    private String registerUser( Map<String, Object> userData ) {

        if ( this.getNonce() == null ) {
            return "";
        }

        URL url = null;
        HttpURLConnection request = null;
        String response = "";
        try {
            url = new URL( Constants.WP_API_USERS_ENDPOINT );
            request = (HttpURLConnection) url.openConnection();

            request.setReadTimeout(Constants.WP_API_READ_TIMEOUT /* milliseconds */);
            request.setConnectTimeout(Constants.WP_API_CONNECT_TIMEOUT /* milliseconds */);
            request.setRequestMethod("POST");
            Log.d("check nonce", this.getNonce());
            request.addRequestProperty("X-WP-Nonce", this.getNonce());
            request.setInstanceFollowRedirects(false);

            StringBuilder postData = new StringBuilder();
            for(Map.Entry<String,Object> param : userData.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            request.getOutputStream().write(postDataBytes);

            request.connect();

            if ( request.getResponseCode() == 200 ) {
                InputStream is = request.getInputStream();
                // Convert the InputStream into a string
                response = this.convertInputStreamToString(is);
                Log.d(TAG, response);
            } else {
                response = this.convertInputStreamToString(request.getErrorStream());
            }

        } catch (IOException e) {
            Log.d(TAG, e.toString());
            request.disconnect();
            response = e.toString();
        }
        Log.d("register userAPI result" , response);
        return response;
    }

    private String convertInputStreamToString(InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader( new InputStreamReader( stream, "UTF-8" ) );
        String str = "", line;
        while ( ( line = br.readLine() ) != null ) {
            str += line;
        }
        return str;
    }
}
