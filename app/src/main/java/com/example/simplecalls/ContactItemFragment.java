package com.example.simplecalls;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.simplecalls.placeholder.PlaceholderContent;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * A fragment representing a list of Items.
 */
public class ContactItemFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    View view;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ContactItemFragment newInstance(int columnCount) {
        ContactItemFragment fragment = new ContactItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    void setAdapter(){
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            ContactItemRecyclerViewAdapter adapter = new ContactItemRecyclerViewAdapter(requireContext());
            adapter.setOnItemClickListener(new ContactItemRecyclerViewAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    if(v.findViewById(R.id.constraintLayout).getVisibility()==View.GONE){
                        v.findViewById(R.id.constraintLayout).setVisibility(View.VISIBLE);
                    } else {
                        v.findViewById(R.id.constraintLayout).setVisibility(View.GONE);
                    }
                }

                @Override
                public void onItemLongClick(int position, View v) {
                    Log.d("TAG", "onItemLongClick pos = " + position);
                    ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("phone", PlaceholderContent.getCurrentList().get(position).getPhoneNumber());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(requireContext(),"Номер скопирован",
                            Toast.LENGTH_SHORT).show();
                }
            });

            ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                    //Remove swiped item from list and notify the RecyclerView
                    int position = viewHolder.getAdapterPosition();
                    String name = PlaceholderContent.getCurrentList().get(position).getName();
                    String phone = PlaceholderContent.getCurrentList().get(position).getPhoneNumber();
                    AlertDialog deleteDialog = new MaterialAlertDialogBuilder(requireContext(),
                            com.google.android.material.R.style.Theme_Material3_DayNight_Dialog_Alert).setTitle("Title")
                            .setMessage("Удалить выбранный контакт?")
                            .setTitle("Внимание")
                            .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    setAdapter();
                                }
                            })
                            .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteContact(name,phone);
                                    setAdapter();
                                }
                            }).show();
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
            recyclerView.setAdapter(new ContactItemRecyclerViewAdapter(requireContext()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item_list, container, false);

        setAdapter();
        return view;
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

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }
}