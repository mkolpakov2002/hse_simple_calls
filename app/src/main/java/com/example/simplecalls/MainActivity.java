package com.example.simplecalls;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int PERMISSION_REQUEST_CODE = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestMultiplePermissions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_contact:
                addContact();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void addContact(){
        Intent intent = new Intent(
                ContactsContract.Intents.SHOW_OR_CREATE_CONTACT,
                Uri.parse("tel:" + null));
        intent.putExtra(ContactsContract.Intents.EXTRA_FORCE_CREATE, true);
        startActivity(intent);
    }

    private void requestMultiplePermissions() {
        ArrayList<String> permissionList = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
            permissionList.add(Manifest.permission.CALL_PHONE);
        }

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_DENIED) {
            permissionList.add(Manifest.permission.READ_CONTACTS);
        }

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_CONTACTS)== PackageManager.PERMISSION_DENIED) {
            permissionList.add(Manifest.permission.WRITE_CONTACTS);
        }

        if(permissionList.size()>0){
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[0]),PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Arrays.stream(grantResults).anyMatch(n -> n!= PackageManager.PERMISSION_GRANTED)){
            // объект Builder для создания диалогового окна
            AlertDialog dialog = new MaterialAlertDialogBuilder(this, com.google.android.material.R.style.Theme_Material3_DayNight_Dialog).create();
            dialog.setTitle("Ошибка");
            dialog.setMessage("Чтобы использовать приложение, предоставьте разрешения.");
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "ОК",
                    (dialog1, which) -> {
                        // Closes the dialog and terminates the activity.
                        dialog1.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + getPackageName()));
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        this.finish();
                    });
            dialog.setCancelable(false);
            dialog.show();
        }
    }
}