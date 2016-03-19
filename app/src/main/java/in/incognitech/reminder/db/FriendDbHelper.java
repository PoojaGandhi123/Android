package in.incognitech.reminder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import in.incognitech.reminder.model.User;
import in.incognitech.reminder.util.Utils;

/**
 * Created by udit on 09/03/16.
 */
public class FriendDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_TABLE = "friends";

    public static final String authorID_COLUMN = "authorID";
    public static final String email_COLUMN = "email";
    public static final String userID_COLUMN = "userID";
    public static final String isActive_COLUMN = "isActive";
    public static final String name_COLUMN = "name";
    public static final String photoURL_COLUMN = "photoURL";

    public static final String DATABASE_SCHEMA = String.format(
            "CREATE TABLE %s (" +
                "_id INTEGER DEFAULT 0," +
                "%s TEXT," +
                "%s TEXT," +
                "%s TEXT," +
                "%s TEXT DEFAULT 'false'," +
                "%s TEXT," +
                "%s TEXT," +
                "PRIMARY KEY(%s,%s,%s)" +
            ")", DATABASE_TABLE, authorID_COLUMN, email_COLUMN, userID_COLUMN, isActive_COLUMN, name_COLUMN, photoURL_COLUMN, authorID_COLUMN, email_COLUMN, userID_COLUMN);

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

    public static User getFriendByEmail(Context context, String email) {
        String where = authorID_COLUMN + " = ? AND " + email_COLUMN + " = ?";
        String whereArgs[] = {Utils.getCurrentUserID(context), email};
        String groupBy = null;
        String having = null;
        String order = null;

        SQLiteDatabase db = new FriendDbHelper(context).getWritableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE, null, where, whereArgs, groupBy, having, order);

        User user = null;

        if(cursor.moveToFirst()) {
            String isActive = cursor.getString(cursor.getColumnIndex(isActive_COLUMN));
            String photoUrl = cursor.getString(cursor.getColumnIndex(photoURL_COLUMN));
            String name = cursor.getString(cursor.getColumnIndex(name_COLUMN));
            String e = cursor.getString(cursor.getColumnIndex(email_COLUMN));
            String id = cursor.getString(cursor.getColumnIndex(userID_COLUMN));

            user = new User();
            user.setIsActive(isActive.equals("true") ? true : false);
            user.setPhotoUrl(photoUrl);
            user.setName(name);
            user.setEmail(e);
            user.setId(id);
        }
        db.close();
        return user;
    }

    public static User getFriend(Context context, String userID) {
        String where = authorID_COLUMN + " = ? AND " + userID_COLUMN + " = ?";
        String whereArgs[] = {Utils.getCurrentUserID(context), userID};
        String groupBy = null;
        String having = null;
        String order = null;

        SQLiteDatabase db = new FriendDbHelper(context).getWritableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE, null, where, whereArgs, groupBy, having, order);

        User user = null;

        if(cursor.moveToFirst()) {
            String isActive = cursor.getString(cursor.getColumnIndex(isActive_COLUMN));
            String photoUrl = cursor.getString(cursor.getColumnIndex(photoURL_COLUMN));
            String name = cursor.getString(cursor.getColumnIndex(name_COLUMN));
            String email = cursor.getString(cursor.getColumnIndex(email_COLUMN));
            String id = cursor.getString(cursor.getColumnIndex(userID_COLUMN));

            user = new User();
            user.setIsActive(isActive.equals("true") ? true : false);
            user.setPhotoUrl(photoUrl);
            user.setName(name);
            user.setEmail(email);
            user.setId(id);
        }
        db.close();
        return user;
    }

    public static void addFriend(Context context, User user) {

        ContentValues newValues = new ContentValues();
        newValues.put(authorID_COLUMN, Utils.getCurrentUserID(context));
        newValues.put(userID_COLUMN, user.getId());
        newValues.put(email_COLUMN, user.getEmail());
        newValues.put(name_COLUMN, user.getName());
        newValues.put(photoURL_COLUMN, user.getPhotoUrl());
        newValues.put(isActive_COLUMN, user.isActive() ? "true" : "false");

        SQLiteDatabase db = new FriendDbHelper(context).getWritableDatabase();

        User existingUser = getFriend(context, user.getId());
        if(existingUser==null) {
            db.insert(DATABASE_TABLE, null, newValues);
        } else {
            String where = authorID_COLUMN + " = ? AND " + userID_COLUMN + " = ?";
            String whereArgs[] = {Utils.getCurrentUserID(context), user.getId()};
            db.update(DATABASE_TABLE, newValues, where, whereArgs);
        }
    }

    public static ArrayList<String> getActiveEmails(Context context, String authorID) {
        ArrayList<String> emails = new ArrayList<String>();

        String where = authorID_COLUMN + " = ? AND " + isActive_COLUMN + " = 'true'";
        String whereArgs[] = {authorID};
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

    public static String getUserID(Context context, String authorID) {
        String userID = null;
        String where = authorID_COLUMN + " = ? AND " + isActive_COLUMN + " = 'true'";
        String whereArgs[] = {authorID};
        String groupBy = null;
        String having = null;
        String order = null;
        String[] resultColumns = {
                userID_COLUMN
        };

        SQLiteDatabase db = new FriendDbHelper(context).getWritableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE, resultColumns, where, whereArgs, groupBy, having, order);
        while (cursor.moveToNext()) {
            userID = cursor.getString(0);
        }
        db.close();
        return userID;
    }
}
