package com.example.simplecalls;

import androidx.annotation.NonNull;

/**
 * A placeholder item representing a piece of content.
 */
public class ContactItem {
    public String id;
    public String name;
    public String phoneNumber;

    public ContactItem() {
        name = "";
        phoneNumber = "";
    }

    public ContactItem(String name, String phoneNumber ) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @NonNull
    @Override
    public String toString() {
        return name + "\n" + phoneNumber;
    }
}
