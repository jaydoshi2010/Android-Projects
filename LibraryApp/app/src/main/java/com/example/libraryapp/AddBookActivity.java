package com.example.libraryapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.libraryapp.Book;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddBookActivity extends AppCompatActivity {

    private EditText bookIdEditText;
    private EditText bookNameEditText;
    private EditText numberOfBooksEditText;
    private Button addBookButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // initialiize firebase over here
        databaseReference = FirebaseDatabase.getInstance().getReference("booksDatabase");

        bookIdEditText = findViewById(R.id.bookIdEditText);
        bookNameEditText = findViewById(R.id.bookNameEditText);
        numberOfBooksEditText = findViewById(R.id.numberOfBooksEditText);
        addBookButton = findViewById(R.id.addBookButton);

        bookIdEditText.setEnabled(false); // disable

        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookToDatabase();


            }
        });
    }

    private void addBookToDatabase() {

        //String bookId = bookIdEditText.getText().toString().trim()
        String bookId = databaseReference.push().getKey(); // get key value from the firebase
        String bookName = bookNameEditText.getText().toString().trim();
        String numberOfBooks = numberOfBooksEditText.getText().toString().trim();


        if ( bookName.isEmpty() || numberOfBooks.isEmpty()) {
            Toast.makeText(AddBookActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!bookName.matches("^[a-zA-Z \\p{P}+]+[0-9]*$")){
            Toast.makeText(AddBookActivity.this, "Invalid book name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!numberOfBooks.matches("^[0-9]*$")){
            Toast.makeText(AddBookActivity.this, "Invalid book copies!", Toast.LENGTH_SHORT).show();
            return;
        }

//        // Create a new Book object
//        Book book1 = new Book(bookId, bookName, numberOfBooks);

        // store book details
        HashMap<String, Object> bookData = new HashMap<>();
        bookData.put("bookId", bookId);
        bookData.put("bookName", bookName);
        bookData.put("numberOfBooks", numberOfBooks);

        // Push the new book to the database under a unique key
        databaseReference.child(bookId).setValue(bookData);
        //databaseReference.push().setValue(bookData);

        //bookIdEditText.setText("");
        bookNameEditText.setText("");
        numberOfBooksEditText.setText("");
        Toast.makeText(AddBookActivity.this,"Book added successfully!",Toast.LENGTH_SHORT).show();
    }
}
