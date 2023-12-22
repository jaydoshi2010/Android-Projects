package com.example.libraryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class IssueBookActivity extends AppCompatActivity {

    private RecyclerView bookRecyclerView;
    private BookAdapterUser bookAdapterUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_book);

        Button checkoutButton = findViewById(R.id.checkoutButton);

        bookRecyclerView = findViewById(R.id.bookRecyclerViewUser);
        bookAdapterUser = new BookAdapterUser();
        bookRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookRecyclerView.setAdapter(bookAdapterUser);

        // Firebase connectivity
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("booksDatabase");

        // Read books from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<BookItemUser> bookList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String bookName = snapshot.child("bookName").getValue(String.class);
                    String numberOfBooks = snapshot.child("numberOfBooks").getValue(String.class);
                    bookList.add(new BookItemUser(bookName, numberOfBooks));
                }
                bookAdapterUser.setBooks(bookList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(IssueBookActivity.this, "Error loading books", Toast.LENGTH_SHORT).show();
            }
        });


        // Checkout button event
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<BookItemUser> selectedBooks = bookAdapterUser.getSelectedBooks();

                if (selectedBooks.size() > 0) {
                    // Start a new activity and pass the selected items
                    Intent intent = new Intent(IssueBookActivity.this, CheckoutActivity.class);
                    intent.putParcelableArrayListExtra("selectedBooks", new ArrayList<>(selectedBooks));
                    startActivity(intent);
                } else {
                    Toast.makeText(IssueBookActivity.this, "Please select at least one book", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
