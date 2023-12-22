package com.example.libraryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends AppCompatActivity {

    private ListView bookListView;
    private ArrayAdapter<String> adapter;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);


        Button logoutButton = findViewById(R.id.logoutButton);
        Button addBookButton = findViewById(R.id.addBookButton);
        //Button viewBooksButton = findViewById(R.id.viewBooksButton);
        //Button deleteBooksButton = findViewById(R.id.deleteBooksButton);

        bookListView = findViewById(R.id.bookListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        bookListView.setAdapter(adapter);

        // firebase display books
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("booksDatabase");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String bookId = snapshot.getKey();
                    String bookName = snapshot.child("bookName").getValue(String.class);
                    String numberOfBooks = snapshot.child("numberOfBooks").getValue(String.class);
                    String bookInfo = bookName + " (" + numberOfBooks + " copies)";
                    adapter.add(bookInfo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminDashboard.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });

        // Add button event
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboard.this,AddBookActivity.class));
            }
        });

        // Logout button event
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logoutIntent = new Intent(AdminDashboard.this, MainActivity.class);
                startActivity(logoutIntent);
                Toast.makeText(AdminDashboard.this, "Successfully logged out", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Set up an item click listener for ListView
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // Create an alert dialog for confirmation
                new AlertDialog.Builder(AdminDashboard.this)
                        .setTitle("Delete Book")
                        .setMessage("Are you sure you want to delete this book?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Perform the delete operation here
                                String selectedBookInfo = adapter.getItem(position);
                                String bookName = selectedBookInfo.split("\\s+")[0]; // Extract book name
                                deleteBookFromFirebase(bookName);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

    }
    private void deleteBookFromFirebase(final String bookName) {
        // Find the book ID by the book name: space-insensitive
        databaseReference.orderByChild("bookName").startAt(bookName).endAt(bookName + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String bookId = snapshot.getKey();
                    String numberOfBooks = snapshot.child("numberOfBooks").getValue(String.class);
                    int currentNumberOfBooks = Integer.parseInt(numberOfBooks);

                    // Check if there is only 1 copy left, then ask for deletion confirmation
                    if (currentNumberOfBooks == 1) {
                        new AlertDialog.Builder(AdminDashboard.this)
                                .setTitle("Delete Book")
                                .setMessage("This is the last copy of the book. Are you sure you want to delete it?")
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Delete the book using the book ID
                                        DatabaseReference bookToDeleteRef = databaseReference.child(bookId);
                                        bookToDeleteRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    //Toast.makeText(CheckoutActivity.this, "Book deleted: " + bookName, Toast.LENGTH_SHORT).show();
                                                } else {
                                                    //Toast.makeText(CheckoutActivity.this, "Error deleting book", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    } else {
                        // Decrement the number of books by 1
                        int newNumberOfBooks = currentNumberOfBooks - 1;

                        // Update the 'numberOfBooks' field in the database
                        DatabaseReference bookToUpdateRef = databaseReference.child(bookId);
                        bookToUpdateRef.child("numberOfBooks").setValue(String.valueOf(newNumberOfBooks));

                        Toast.makeText(AdminDashboard.this, "One copy of " + bookName + " deleted.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminDashboard.this, "Error deleting book", Toast.LENGTH_SHORT).show();
            }
        });
    }


}