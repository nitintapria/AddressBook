package org.example.android.databaseexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
/*
Flow of control
 1.onCreate() of MainActivity
 2.DBHelper constructor is called
 3.getAllContacts method of DBHelper class is called in the onCreate method of MainActivity
   to get name of all contacts in database and display them in the list view.
   If database is not present i.e. app is opened for the first time then
   onCreate() method of SQLiteOpenHelper (defined in DBHelper class) is invoked by the
   getReadableDatabase() method called by this getAllContacts method.
   IMP - onCreate() is only run when the database file did not exist and was just created
 4.onCreateOptionsMenu is invoked to create an options menu in the app bar

  CASE 1
 5.If user selects "Insert Contact" from menu then onOptionsItemSelected is invoked which starts
   the DisplayContact Activity (with intent 0) due to which onCreate of DisplayContact Activity is
   invoked which calls the DBHelper constructor
 6.The onCreateOptionsMenu of DisplayContact Activity is invoked. Here the intent received is 0
   indicating that this is insert contact mode so update and delete menu will not be shown
 7.After inserting the values for new contact when the user clicks the save button the the
   manipulateDatabase method (of DisplayContact.java) is invoked which first calls the
   insertContact method of DBHelper by passing the values entered by the user in the
   textViews (this insertContact method stores values into the database)and then starts MainActivity
 8.Go to step 1 (as MainActivity created again)

   CASE 2
 5.If user selects a contact name from the ListView then onItemClick of the onItemClickListener
   is invoked which starts the DisplayContact Activity (with intent storing the id of contact
   selected) due to which the onCreate of that Activity is called which calls the DBHelper
   constructor. In the onCreate method of DisplayContact activity we check the extras send by
   the intent. In this case (contact from list view selected) the intent is non zero indicating
   that this is view contact mode so firstly data from database with respect to that contact name is
   retrieved using the getData method of the DBHelper class , then, the textViews are modified so
   that user cannot click or focus them.
   SUB CASE 1
 6.If the user selects the "Edit Contact" option from the app bar menu then the onOptionsItemSelected
   method of DisplayContact is invoked which has two possibilities in switch case -update or delete
   contact. In this case the update contact is true so the "SAVE CONTACT" button is made visible and
   the text views are modified so that the user can click and focus them. When the user click the
   "SAVE CONTACT" button after updating the contact then the manipulateContact method is invoked
   which firstly, checks the intent to be non zero i.e the id of contact to be updated and calls the
   updateContact method of the DBHelper class by passing the new values entered by the user in the
   textViews, and then starts MainActivity.
 7.Go to step 1 (as MainActivity created again)
   SUB CASE 2
 6.If the user selects the "Delete Contact" option from the app bar menu then the onOptionsItemSelected
   method of DisplayContact is invoked which has two possibilities in switch case -update or delete
   contact. In this case the delete contact is true so an AlertDialog is created which confirms from
   the user if he is sure to delete the database. If "YES" then firstly, deleteContact method of
   DBHelper is invoked which deletes that id of contact entry from the database, then MainActivity
   is started.
 7.Go to step 1 (as MainActivity created again)
* */

public class MainActivity extends AppCompatActivity {
    private ListView listView_contacts;
    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb = new DBHelper(this);
        //We pass the context to DBHelper class
        ArrayList array_list = mydb.getAllContacts();
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,R.layout.list_item,R.id.list_item_textview, array_list);
        //syntax - ArrayAdapter (Context context, int resource, int textViewResourceId, List<T> objects)
        //context - The current context
        //resource - The resource ID for a layout file containing a layout to use when instantiating views
        //textViewResourceId - The id of the TextView within the layout resource to be populated
        //objects - The objects to represent in the ListView

        listView_contacts = (ListView)findViewById(R.id.listView_contacts);
        listView_contacts.setAdapter(arrayAdapter);
        listView_contacts.setOnItemClickListener(new OnItemClickListener(){
            @Override
            //This method is invoked when user selects a contact in the ListView
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                //parent is the adapter view where the click happened
                //view is the view within the AdapterView that was clicked
                //position is the position of the view in the adapter
                //id is the row id of the item that was clicked
                int idOfContact = position + 1;

                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", idOfContact);

                Intent intent = new Intent(getApplicationContext(),DisplayContact.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });
    }

    @Override
    //this method is used to create an options menu in the app bar
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_insert_contact, menu);
        return true;
    }

    //When the user selects an item from the options menu (including action items in the app bar),
    // the system calls your activity's onOptionsItemSelected() method.
    // This method passes the MenuItem selected. You can identify the item by calling getItemId(),
    // which returns the unique ID for the menu item (defined by the android:id attribute in the
    // menu resource or with an integer given to the add() method).
    // You can match this ID against known menu items to perform the appropriate action
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Here this method is invoked when user select Insert Contact option
        super.onOptionsItemSelected(item);

        switch(item.getItemId()) {
            //here item1 is the only item we have created in the menu i.e. add new contact
            case R.id.insert_contact:
                //Bundle is a map with key-value pairs
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", 0);
                Intent intent = new Intent(getApplicationContext(),DisplayContact.class);
                //Add a set of extended data to the intent.
                intent.putExtras(dataBundle);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
