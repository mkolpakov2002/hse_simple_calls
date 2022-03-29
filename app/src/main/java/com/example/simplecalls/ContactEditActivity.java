package com.example.simplecalls;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toolbar;

import java.util.ArrayList;

public class ContactEditActivity extends AppCompatActivity {
    EditText phoneEdit;
    EditText nameEdit;
    EditText infoEdit;
    ImageButton acceptButton;
    ImageButton cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);
        ArrayList<String> data = getIntent().getExtras().getStringArrayList("data");
        androidx.appcompat.app.ActionBar toolbar = getSupportActionBar();

        nameEdit = findViewById(R.id.contact_name);
        nameEdit.setText(data.get(0));
        phoneEdit = findViewById(R.id.contact_number);
        phoneEdit.setText(data.get(1));
        if (toolbar != null) {
            toolbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            toolbar.setDisplayShowCustomEnabled(true);
            toolbar.setCustomView(R.layout.toolbar_view);
            View view = toolbar.getCustomView();
            view.findViewById(R.id.imageButton3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            view.findViewById(R.id.imageButton4).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteContact(data.get(0), data.get(1));
                    Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, new ContentValues());
                    /* Получаем id добавленного контакта */
                    long rawContactId =  ContentUris.parseId(rawContactUri);

                    ContentValues values = new ContentValues();

                    /* Связываем наш аккаунт с данными */
                    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                    /* Устанавливаем MIMETYPE для поля данных */
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                    /* Имя для нашего аккаунта */
                    values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nameEdit.getText().toString().trim());

                    getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

                    values.clear();

                    values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                    /* Тип данных – номер телефона */
                    values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                    /* Номер телефона */
                    values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneEdit.getText().toString().trim());
                    /* Тип – мобильный */
                    values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

                    getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
                    finish();
                }
            });
        }


    }

    @SuppressLint("Range")
    boolean deleteContact(String name, String phone){
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = App.getContext().getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                        String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        App.getContext().getContentResolver().delete(uri, null, null);
                        return true;
                    }

                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            cur.close();
        }
        return false;
    }
}