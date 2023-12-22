package com.example.libraryapp;

import static com.google.android.material.color.utilities.MaterialDynamicColors.error;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.libraryapp.BookItemUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private List<BookItemUser> selectedBooks;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        final TextView[] selectedBooksTextView = {findViewById(R.id.selectedBooksTextView)};
        Button finalCheckoutButton = findViewById(R.id.finalCheckoutButton);

        // Retrieve selected books from the intent
        selectedBooks = getIntent().getParcelableArrayListExtra("selectedBooks");

        // Display the selected books in the TextView
        displaySelectedBooks(selectedBooksTextView[0]);

        // Firebase connectivity
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("booksDatabase");

        // Final checkout button event
        finalCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform the final checkout process (delete one numberOfBooks from Firebase)
                if (selectedBooks != null && !selectedBooks.isEmpty()) {
                    for (BookItemUser selectedBook : selectedBooks) {
                        updateNumberOfBooks(selectedBook);
                    }
                    sendEmail(selectedBooks); // Send email with selected book data
                    Toast.makeText(CheckoutActivity.this, "Final checkout successful", Toast.LENGTH_SHORT).show();
                    selectedBooks.clear();
                    finish(); // Finish the activity after the checkout
                } else {
                    Toast.makeText(CheckoutActivity.this, "No books selected for checkout", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Display selected books in the TextView
    private void displaySelectedBooks(TextView textView) {
        if (selectedBooks != null && !selectedBooks.isEmpty()) {
            StringBuilder selectedBooksText = new StringBuilder("Selected Books:\n");
            for (BookItemUser book : selectedBooks) {
                selectedBooksText.append("- ").append(book.getBookName()).append("\n");
            }
            textView.setText(selectedBooksText.toString());
        } else {
            textView.setText("No books selected.");
        }
    }

    // Update the numberOfBooks in Firebase (subtract one)
    private void updateNumberOfBooks(BookItemUser selectedBook) {
        final String[] bookName = {selectedBook.getBookName()};

        System.out.println(bookName[0]);

        // Find the book by name
        databaseReference.orderByChild("bookName").equalTo(bookName[0]).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String bookId = snapshot.getKey();
                    String numberOfBooks = snapshot.child("numberOfBooks").getValue(String.class);
                    int currentNumberOfBooks = Integer.parseInt(numberOfBooks);

                    // Check if there is only 1 copy left, then ask for deletion confirmation
                    if (currentNumberOfBooks == 1) {
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
                    } else {
                        // Decrement the number of books by 1
                        int newNumberOfBooks = currentNumberOfBooks - 1;
                        // Update the 'numberOfBooks' field in the database
                        DatabaseReference bookToUpdateRef = databaseReference.child(bookId);
                        bookToUpdateRef.child("numberOfBooks").setValue(String.valueOf(newNumberOfBooks));
                        //Toast.makeText(CheckoutActivity.this, "One copy of " + bookName + " deleted.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CheckoutActivity.this, "Error deleting book", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void sendEmail(List<BookItemUser> selectedBooks) {
        // Get the current Firebase user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check if the user is signed in
        if (currentUser != null) {
            // User is signed in

            // Create an Intent with action type ACTION_SEND
            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            // Set the recipient email address
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jaydoshi2010@gmail.com"});

            // Create a Uri for the email subject with dynamic user email
            String subject = "ISSUE BOOK Confirmation: " + getUserEmail(currentUser);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

            // Create the email body with the selected book data
            StringBuilder emailBody = new StringBuilder();
            emailBody.append("Selected Books:\n");

            for (BookItemUser book : selectedBooks) {
                emailBody.append("- ").append(book.getBookName()).append("\n");
            }
            
            emailBody.append("\n\n\n\n\n Thank you \n" + getUserEmail(currentUser));

            //  email body
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody.toString());
            emailIntent.setType("text/plain");

            // Start the email activity
            startActivity(emailIntent);
            finish();
        }
        else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
        }
    }

    // get the user email from firebase-login
    private String getUserEmail(FirebaseUser currentUser) {
        if (currentUser != null) {
            // signedin user email:
            return currentUser.getEmail();
        }
        else {
            return "jay@admin.com";
        }
    }
}
