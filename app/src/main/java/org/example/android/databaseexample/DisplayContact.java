package org.example.android.databaseexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//if not extends AppCompatActivity then no action bar
public class DisplayContact extends AppCompatActivity {
    private DBHelper mydb ;

    TextView textView_name ;
    TextView textView_phone;
    TextView textView_email;
    TextView textView_street;
    TextView textView_city;
    int idToUpdate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contact);
        textView_name = (TextView) findViewById(R.id.editText_name);
        textView_phone = (TextView) findViewById(R.id.editText_phone);
        textView_email = (TextView) findViewById(R.id.editText_street);
        textView_street = (TextView) findViewById(R.id.editText_email);
        textView_city = (TextView) findViewById(R.id.editText_city);

        mydb = new DBHelper(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            int idOfContact = extras.getInt("id");
            if(idOfContact>0){
                //means this is the view contact part, not the add contact part.
                Cursor rs = mydb.getData(idOfContact);
                //this cursor stores the data with respect to the idOfContact received from the intent
                idToUpdate = idOfContact;
                //if user selects the update contact option from the menu then idToUpdate stores the
                    //id of the contact to be updated

                rs.moveToFirst();
                //IF WE DON'T DO MOVE TO FIRST THEN ERROR SINCE CURSOR MUST BE MOVED TO THE
                //FIRST ROW OF ITS RESULT SET TO ACCESS ITS DATA
                String name = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_NAME));
                String phone = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_PHONE));
                String email = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_EMAIL));
                String street = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_STREET));
                String city = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_CITY));
                if (!rs.isClosed())  {
                    rs.close();
                }
                Button b = (Button)findViewById(R.id.button_saveContact);
                b.setVisibility(View.INVISIBLE);
                viewContactsMode(textView_name, name);
                viewContactsMode(textView_phone,phone);
                viewContactsMode(textView_email,email);
                viewContactsMode(textView_street,street);
                viewContactsMode(textView_city,city);
            }
        }
    }

    public void viewContactsMode(TextView v,String s) {
        v.setText(s);
        v.setFocusable(false);
        v.setClickable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                int idOfContact = extras.getInt("id");
                if (idOfContact > 0) {
                    //idOfContact > 0 indicate that this is the view contact part (not add contact part)
                    //so user should see menus representing update or delete contact options
                    getMenuInflater().inflate(R.menu.menu_update_or_delete_contact, menu);
                }
            }
            return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
            case R.id.edit_Contact:
                Button b = (Button)findViewById(R.id.button_saveContact);
                b.setVisibility(View.VISIBLE);
                b.setText("UPDATE CONTACT");
                editContactsMode(textView_name);
                editContactsMode(textView_city);
                editContactsMode(textView_email);
                editContactsMode(textView_phone);
                editContactsMode(textView_street);
                return true;

            case R.id.delete_Contact:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.deleteContact)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mydb.deleteContact(idToUpdate);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

                AlertDialog d = builder.create();
                d.setTitle("Are you sure");
                d.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void editContactsMode(TextView v) {
        v.setEnabled(true);
        v.setFocusableInTouchMode(true); //if used setFocusable instead then does not work
        v.setClickable(true);
    }

    public void manipulateDatabase(View view) {
        Bundle extras = getIntent().getExtras();
        if(extras !=null) { //we have to update contact
            int idOfContact = extras.getInt("id");
            if(idOfContact>0){
                if(mydb.updateContact(idToUpdate,textView_name.getText().toString(),
                        textView_phone.getText().toString(), textView_email.getText().toString(),
                        textView_street.getText().toString(), textView_city.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(getApplicationContext(), "not Updated", Toast.LENGTH_SHORT).show();
                }
            } else{ //we have to insert new contact
                if(mydb.insertContact(textView_name.getText().toString(), textView_phone.getText().toString(),
                        textView_email.getText().toString(), textView_street.getText().toString(),
                        textView_city.getText().toString())){
                    Toast.makeText(getApplicationContext(), "done",
                            Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getApplicationContext(), "not done",
                            Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        }
    }
}