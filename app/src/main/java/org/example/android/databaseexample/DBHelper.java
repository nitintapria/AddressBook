package org.example.android.databaseexample;

/**
 * Created by Nitin on 20/01/2017.
 */
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    //Helper object is used to create, open, and/or manage a database.
    // This method always returns very quickly. The database is not actually created
    // or opened until one of getWritableDatabase() or getReadableDatabase() is called.

    public static final String DATABASE_NAME = "MyDatabase";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_EMAIL = "email";
    public static final String CONTACTS_COLUMN_STREET = "street";
    public static final String CONTACTS_COLUMN_CITY = "city";
    public static final String CONTACTS_COLUMN_PHONE = "phone";
    public static long lastDatabaseIndex;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //here null is the object value of SQLiteDatabase.CursorFactory
        // it is used for creating cursor objects, or null for the default
        //here 1 is the database version
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+CONTACTS_TABLE_NAME+
            "("+CONTACTS_COLUMN_ID+" integer primary key,"+CONTACTS_COLUMN_NAME+" text,"+
                CONTACTS_COLUMN_PHONE+" text,"+CONTACTS_COLUMN_EMAIL+" text,"+
                CONTACTS_COLUMN_STREET+" text,"+CONTACTS_COLUMN_CITY+" text)");
        // execSQL execute a single SQL statement that is NOT a SELECT or
        // any other SQL statement that returns data.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+CONTACTS_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertContact (String name, String phone, String email, String street,String city) {
        SQLiteDatabase db = this.getWritableDatabase();
        //getWritableDatabase is a method of SQLiteOpenHelper class which
        // create and/or open a database that will be used for reading and writing.
        // and has return type SQLiteDatabase
        ContentValues contentValues = new ContentValues();
        //it is a map that stores key-value pairs
        contentValues.put(CONTACTS_COLUMN_NAME, name);
        contentValues.put(CONTACTS_COLUMN_PHONE, phone);
        contentValues.put(CONTACTS_COLUMN_EMAIL, email);
        contentValues.put(CONTACTS_COLUMN_STREET, street);
        contentValues.put(CONTACTS_COLUMN_CITY, city);
        // insert method returns the primary key value of the new row and -1 if error
        long check = db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        //insert is a convenience method for inserting a row into the database.
        // syntax -	long insert(String table, String nullColumnHack, ContentValues values)
            // here String table: the table to insert the row into
            // here String nullColumnHack :may be null. SQL doesn't allow inserting a completely
               //empty row without naming at least one column name. If your provided values is empty,
               //no column names are known and an empty row can't be inserted. If not set to null,
               //the nullColumnHack parameter provides the name of nullable column name to explicitly
               //insert a NULL into in the case where your values is empty.
            // here ContentValues: this map contains the initial column values for the row.
            // The keys should be the column names and the values the column values
        //It returns the row ID of the newly inserted row, or -1 if an error occurred
        if (check != -1) {
            lastDatabaseIndex = check;
            return true;
        }
        else
        return false;
    }

    public Cursor getData(int id) {
    //This method is used to get the data with respect to the id of the contact name from the database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from "+CONTACTS_TABLE_NAME+" where "+CONTACTS_COLUMN_ID+" = "+Integer.toString(id),null);
        //the rawQuery method runs the provided SQL and returns a Cursor over the result set.
        //Syntax - Cursor rawQuery (String sql,String[] selectionArgs)
            //sql - the SQL query. The SQL string must not be ; terminated
            //selectionArgs - You may include ?s in the where clause, which will be replaced
                // by the values from whereArgs. The values will be bound as Strings
        return res;
    }

    public boolean updateContact (Integer id, String name, String phone, String email, String street,String city) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_NAME, name);
        contentValues.put(CONTACTS_COLUMN_PHONE, phone);
        contentValues.put(CONTACTS_COLUMN_EMAIL, email);
        contentValues.put(CONTACTS_COLUMN_STREET, street);
        contentValues.put(CONTACTS_COLUMN_CITY, city);
        db.update(CONTACTS_TABLE_NAME, contentValues, CONTACTS_COLUMN_ID+" = ? ",
                new String[] { Integer.toString(id) } );
        //update is a convenience method for updating rows in the database.
        //syntax- int update(String table, ContentValues values, String whereClause, String[] whereArgs)
        //table is the table to update in
        //ContentValues: a map from column names to new column values.
            // null is a valid value that will be translated to NULL.
        //It returns the number of rows affected
        return true;
    }

    public void deleteContact (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(id == lastDatabaseIndex) {
            db.delete(CONTACTS_TABLE_NAME,
                    CONTACTS_COLUMN_ID + " = ? ",
                    new String[]{Integer.toString(id)});
            lastDatabaseIndex--;
        }
        //delete is a convenience method for deleting rows in the database
        //syntax - int delete (String table,String whereClause,String[] whereArgs)
            //table - the table to delete from
            //whereClause - the optional WHERE clause to apply when deleting.
                // Passing null will delete all rows.
            //whereArgs - You may include ?s in the where clause, which will be replaced
                // by the values from whereArgs. The values will be bound as Strings
        //It returns the number of rows affected if a whereClause is passed in, 0 otherwise.
            // To remove all rows and get a count pass "1" as the whereClause.
        else {
                 db.delete(CONTACTS_TABLE_NAME,
                         CONTACTS_COLUMN_ID + " = ? ",
                         new String[]{Integer.toString(id)});
                 while (lastDatabaseIndex > id) {
                     ContentValues contentValues = new ContentValues();
                     contentValues.put(CONTACTS_COLUMN_ID,id);
                     id++;
                     db.update(CONTACTS_TABLE_NAME, contentValues, CONTACTS_COLUMN_ID + " = ? ",
                             new String[]{Integer.toString(id)});
                 }
                 lastDatabaseIndex--;
                //THIS IS THE SOLUTION OF BELOW PROBLEM
                //I MADE A STATIC VARIABLE TO POINT THE NUMBER OF ROWS IN THE TABLE
                //IF USER DELETES AN INTERMEDIATE ROW THEN THE CONSECUTIVE ROWS ARE MODIFIED
                //SO THAT THE IDs OF THE CONTACTs ARE ALWAYS IN SERIAL FASHION
                // i.e 1,2,3 rather than 2,3 (see below)
         }
        //IF WE JUST DO DELETE ROW AT THE END THEN NO PROBLEM BUT IF WE DELETE INTERMEDIATE
        //ROW THEN THAT INDEX ROW IS DELETED, REST ROWS REMAIN SAME
        //THIS APP SEARCHES THE ROW INDEX TO BE DELETED BY THE POSITION OF CONTACT IN THE
        //LIST VIEW (which modifies as we delete intermediate row) SO IF WE HAVE DELETED FIRST ROW
        //AND THEN CLICK ON FIRST CONTACT IN LIST VIEW THEN ERROR SINCE ROW CORRESPONDING TO ID 1
        //DOES NOT EXISTS ANYMORE
        //EXAMPLE LOG IS BELOW
              /*01-22 20:36:23.535 14265-14265/? E/Delete!: !=
                01-22 20:36:23.536 14265-14265/? E/index-1- a ab abc abcd: !=
                01-22 20:36:23.536 14265-14265/? E/index-2- b op opq pqrs: !=
                01-22 20:36:23.536 14265-14265/? E/index-3- t tu tuv tuvw: !=
                01-22 20:36:33.464 14265-14265/? E/Delete!: !=
                01-22 20:36:33.467 14265-14265/? E/index-2- b op opq pqrs: !=
                01-22 20:36:33.467 14265-14265/? E/index-3- t tu tuv tuvw: !=
                * */
    }

    public ArrayList<String> getAllContacts() {
        //This method return an ArrayList of names of contact present in the database
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME, null );
        if(res!=null && res.moveToFirst()) {
            //the moveToFirst method move the cursor to the first row and returns whether the move succeeded.
            do{
                //isAfterLast returns whether the cursor is pointing to the position after the last row.
                if(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)) != null) {
                    array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
                }
                //moveToNext method moves the cursor to the next row.
            }while(res.moveToNext());
        }
        res.close(); //It is important to close cursor after its use to prevent memory leakage
        db.close();
        return array_list;
    }
}
