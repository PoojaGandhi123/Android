/**
 * Created by udit on 01/02/16.
 */

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.incognitech.reminder.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Build.VERSION_CODES;

/**
 * Class containing some static utility methods.
 */
public class Utils {
    private Utils() {};

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
    }

    public static String getGravatar(String email) {
        return "https://secure.gravatar.com/avatar/" + HashGenerator.generateMD5(email.toLowerCase().trim());
    }

    public static void setProcessedContacts(Context context, boolean processed) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Constants.SHARED_PREFS_PROCESSED_CONTACTS, processed);
        editor.commit();
    }

    public static boolean getProcessedContacts(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        boolean processed = prefs.getBoolean(Constants.SHARED_PREFS_PROCESSED_CONTACTS, false);
        return processed;
    }

    public static boolean isUserLoggedIn(Context context) {
        if ( Utils.getCurrentUserID(context).equals("")
            || Utils.getCurrentUserEmail(context).equals("") ) {
            return false;
        }
        return true;
    }

    public static String getCurrentUserID(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        String uid = prefs.getString(Constants.SHARED_PREFS_CUR_USER_ID, "");
        return uid;
    }

    public static void setCurrentUserID(Context context, String uid) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(Constants.SHARED_PREFS_CUR_USER_ID, uid);
        editor.commit();
    }

    public static String getCurrentUserDisplayName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        String displayName = prefs.getString(Constants.SHARED_PREFS_CUR_USER_DISPLAY_NAME, "");
        return displayName;
    }

    public static void setCurrentUserDisplayName(Context context, String displayName) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(Constants.SHARED_PREFS_CUR_USER_DISPLAY_NAME, displayName);
        editor.commit();
    }

    public static String getCurrentUserEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        String email = prefs.getString(Constants.SHARED_PREFS_CUR_USER_EMAIL, "");
        return email;
    }

    public static void setCurrentUserEmail(Context context, String email) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(Constants.SHARED_PREFS_CUR_USER_EMAIL, email);
        editor.commit();
    }

    public static String getCurrentUserPhotoUrl(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        String photoUrl = prefs.getString(Constants.SHARED_PREFS_CUR_USER_PHOTO_URL, "");
        return photoUrl;
    }

    public static void setCurrentUserPhotoUrl(Context context, String photoUrl) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFS_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(Constants.SHARED_PREFS_CUR_USER_PHOTO_URL, photoUrl);
        editor.commit();
    }
}
