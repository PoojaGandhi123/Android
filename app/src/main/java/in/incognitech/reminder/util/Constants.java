package in.incognitech.reminder.util;

/**
 * Created by udit on 20/01/16.
 */
public class Constants {

    public static final String IMAGE_CACHE_DIR = "thumbs";
    public static String shared_prefs_key = "reminder-shared-prefs";

    public static final String WP_API_REQUEST_NONCE_ENDPOINT = "http://reminder.incognitech.in/wp-json/reminder/v1/get-cookie-nonce/";
    public static final String WP_API_USERS_ENDPOINT = "http://reminder.incognitech.in/wp-json/wp/v2/users/";

    public static final int WP_API_READ_TIMEOUT = 10000;
    public static final int WP_API_CONNECT_TIMEOUT = 10000;
}
