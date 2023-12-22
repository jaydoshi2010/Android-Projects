package com.example.libraryapp;

import android.os.Parcel;
import android.os.Parcelable;

public class BookItemUser implements Parcelable {
    private String bookName;
    private String numberOfBooks;
    private boolean isSelected;

    // Constructor
    public BookItemUser(String bookName, String numberOfBooks) {
        this.bookName = bookName;
        this.numberOfBooks = numberOfBooks;
    }

    // Getter and Setter methods
    public String getBookName() {
        return bookName;
    }

    public String getNumberOfBooks() {
        return numberOfBooks;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookName);
        dest.writeString(numberOfBooks);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    public static final Parcelable.Creator<BookItemUser> CREATOR = new Parcelable.Creator<BookItemUser>() {
        @Override
        public BookItemUser createFromParcel(Parcel in) {
            return new BookItemUser(in);
        }

        @Override
        public BookItemUser[] newArray(int size) {
            return new BookItemUser[size];
        }
    };

    private BookItemUser(Parcel in) {
        bookName = in.readString();
        numberOfBooks = in.readString();
        isSelected = in.readByte() != 0;
    }
}
