package com.example.simplecalls;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.simplecalls.databinding.FragmentItemBinding;
import com.example.simplecalls.placeholder.PlaceholderContent;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ContactItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ContactItemRecyclerViewAdapter extends RecyclerView.Adapter<ContactItemRecyclerViewAdapter.ViewHolder> {

    private List<ContactItem> mValues;
    private Context c;
    private static ClickListener clickListener;

    public ContactItemRecyclerViewAdapter(Context c) {
        mValues = PlaceholderContent.getContactList();
        this.c = c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).toString());

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public final TextView mContentView;
        public ContactItem mItem;
        ConstraintLayout constraintLayout;
        ImageButton callButton;
        MaterialButton deleteButton;
        MaterialButton changeButton;
        MaterialButton smsButton;

        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            binding.getRoot().setOnClickListener(this);
            binding.getRoot().setOnLongClickListener(this);
            mContentView = binding.content;
            constraintLayout = binding.getRoot().findViewById(R.id.phone_item_layout);
            callButton = binding.getRoot().findViewById(R.id.call_button);
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mItem.getPhoneNumber()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    App.getContext().startActivity(intent);
                }
            });
            changeButton = binding.getRoot().findViewById(R.id.change_button);
            changeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> data = new ArrayList<>();
                    data.add(mItem.getName());
                    data.add(mItem.getPhoneNumber());
                    Intent intent = new Intent(c,ContactEditActivity.class);
                    intent.putStringArrayListExtra("data",data);
                    c.startActivity(intent);
                }
            });

            deleteButton = binding.getRoot().findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteContact(mItem.getName(), mItem.getPhoneNumber());
                    mValues = PlaceholderContent.getContactList();
                    notifyDataSetChanged();
                }
            });

            smsButton = binding.getRoot().findViewById(R.id.change_button2);
            smsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", mItem.getPhoneNumber(), null)));
                }
            });


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


        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onItemLongClick(getAdapterPosition(), view);
            return false;
        }
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        ContactItemRecyclerViewAdapter.clickListener = clickListener;
    }
}