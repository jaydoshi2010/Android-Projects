package com.example.libraryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BookAdapterUser extends RecyclerView.Adapter<BookAdapterUser.BookViewHolder> {

    private List<BookItemUser> books = new ArrayList<>();

    private List<BookItemUser> selectedBooks = new ArrayList<>();


    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_user, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookItemUser book = books.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void setBooks(List<BookItemUser> bookList) {
        this.books = bookList;
        notifyDataSetChanged();
    }



    class BookViewHolder extends RecyclerView.ViewHolder {

        private TextView bookNameTextView;
        private CheckBox selectCheckBox;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookNameTextView = itemView.findViewById(R.id.bookNameTextViewUser);
            selectCheckBox = itemView.findViewById(R.id.selectCheckBoxUser);
        }

        public void bind(final BookItemUser book) {
            bookNameTextView.setText(book.getBookName());
            selectCheckBox.setChecked(book.isSelected());

            // Toggle the selected state when the checkbox is clicked
            selectCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectCheckBox.isChecked() && selectedBooks.size() >= 3) {
                        selectCheckBox.setChecked(false);
                        // Optionally, show a message to the user
                        Toast.makeText(view.getContext(), "You can select up to 3 books", Toast.LENGTH_SHORT).show();
                    } else {
                        book.setSelected(selectCheckBox.isChecked());
                        if (book.isSelected()) {
                            selectedBooks.add(book);
                        } else {
                            selectedBooks.remove(book);
                        }
                    }
                }
            });
        }
    }
    // Getter for selected books
    public List<BookItemUser> getSelectedBooks() {
        return selectedBooks;
    }

    // Clear selected books
    public void clearSelectedBooks() {
        selectedBooks.clear();
    }
}
