package in.incognitech.reminder.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by udit on 09/03/16.
 */
public class FriendDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_TABLE = "friends";

    public static final String contactURI_COLUMN = "contactURI";
    public static final String email_COLUMN = "email";
    public static final String userID_COLUMN = "userID";
    public static final String isActive_COLUMN = "isActive";

    public static final String DATABASE_SCHEMA = String.format(
            "CREATE TABLE %s (" +
                "%s TEXT," +
                "%s TEXT," +
                "%s TEXT," +
                "%s TEXT," +
                "PRIMARY KEY(%s,%s,%s)" +
            ")", DATABASE_TABLE, contactURI_COLUMN, email_COLUMN, userID_COLUMN, isActive_COLUMN, contactURI_COLUMN, email_COLUMN, userID_COLUMN);

    public FriendDbHelper(Context context) {
        super(context, DATABASE_TABLE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_SCHEMA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public static ArrayList<String> getActiveEmails(Context context, String contactURI) {
        ArrayList<String> emails = new ArrayList<String>();

        String where = contactURI_COLUMN + " = ? AND " + isActive_COLUMN + " = 'true'";
        String whereArgs[] = {contactURI};
        String groupBy = null;
        String having = null;
        String order = null;
        String[] resultColumns = {
            email_COLUMN
        };

        SQLiteDatabase db = new FriendDbHelper(context).getWritableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE, resultColumns, where, whereArgs, groupBy, having, order);
        while (cursor.moveToNext()) {
            emails.add(cursor.getString(0));
        }
        db.close();
        return emails;
    }
}
