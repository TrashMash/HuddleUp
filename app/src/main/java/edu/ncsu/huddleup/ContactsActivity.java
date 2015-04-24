package edu.ncsu.huddleup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends Activity {

    private ListView mainListView ;
    private ArrayList<Contact> contacts = new ArrayList<Contact>();
    private ArrayAdapter<Contact> listAdapter ;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_layout);

        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.contactsListView );

        // When item is tapped, toggle checked properties of CheckBox and Contact.
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View item,
                                     int position, long id) {
                Contact contact = listAdapter.getItem( position );
                contact.toggleChecked();
                ContactViewHolder viewHolder = (ContactViewHolder) item.getTag();
                viewHolder.getCheckBox().setChecked( contact.isChecked() );
            }
        });


        // Get phone contacts
        contacts = getPhoneContacts();

        // Set our custom array adapter as the ListView's adapter.
        listAdapter = new ContactArrayAdapter(this, contacts);
        mainListView.setAdapter( listAdapter );
    }

    /*@Override
    public void onResume() {
        super.onResume();
        contacts = getPhoneContacts();
    }*/

    /** Holds contact data. */
    private static class Contact {
        private String name = "" ;
        private boolean checked = false ;
        public Contact() {}
        public Contact( String name ) {
            this.name = name ;
        }
        public Contact( String name, boolean checked ) {
            this.name = name ;
            this.checked = checked ;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public boolean isChecked() {
            return checked;
        }
        public void setChecked(boolean checked) {
            this.checked = checked;
        }
        public String toString() {
            return name ;
        }
        public void toggleChecked() {
            checked = !checked ;
        }
    }

    /** Holds child views for one row. */
    private static class ContactViewHolder {
        private CheckBox checkBox ;
        private TextView textView ;
        public ContactViewHolder() {}
        public ContactViewHolder( TextView textView, CheckBox checkBox ) {
            this.checkBox = checkBox ;
            this.textView = textView ;
        }
        public CheckBox getCheckBox() {
            return checkBox;
        }
        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }
        public TextView getTextView() {
            return textView;
        }
        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }

    /** Custom adapter for displaying an array of Contact objects. */
    private static class ContactArrayAdapter extends ArrayAdapter<Contact> {

        private LayoutInflater inflater;

        public ContactArrayAdapter( Context context, List<Contact> contactsList ) {
            super( context, R.layout.simplerow, R.id.rowTextView, contactsList );
            // Cache the LayoutInflate to avoid asking for a new one each time.
            inflater = LayoutInflater.from(context) ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Contact to display
            Contact contact = (Contact) this.getItem( position );

            // The child views in each row.
            CheckBox checkBox ;
            TextView textView ;

            // Create a new row view
            if ( convertView == null ) {
                convertView = inflater.inflate(R.layout.simplerow, null);

                // Find the child views.
                textView = (TextView) convertView.findViewById( R.id.rowTextView );
                checkBox = (CheckBox) convertView.findViewById( R.id.CheckBox01 );

                // Optimization: Tag the row with it's child views, so we don't have to
                // call findViewById() later when we reuse the row.
                convertView.setTag( new ContactViewHolder(textView,checkBox) );

                // If CheckBox is toggled, update the contact it is tagged with.
                checkBox.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Contact contact = (Contact) cb.getTag();
                        contact.setChecked(cb.isChecked());
                    }
                });
            }
            // Reuse existing row view
            else {
                // Because we use a ViewHolder, we avoid having to call findViewById().
                ContactViewHolder viewHolder = (ContactViewHolder) convertView.getTag();
                checkBox = viewHolder.getCheckBox() ;
                textView = viewHolder.getTextView() ;
            }

            // Tag the CheckBox with the Contact it is displaying, so that we can
            // access the contact in onClick() when the CheckBox is toggled.
            checkBox.setTag( contact );

            // Display contact data
            checkBox.setChecked( contact.isChecked() );
            textView.setText( contact.getName() );

            return convertView;
        }

    }

    public ArrayList<Contact> getPhoneContacts() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        Cursor phones = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Contact c = new Contact(name);
            contacts.add(c);
        }
        phones.close();
        return contacts;
    }

    public void openMap(View view) {

        Intent i = new Intent(this, MapActivity.class);
        startActivity(i);
    }
}
