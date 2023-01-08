package com.example.courseworkrmp;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseworkrmp.data.DatabaseDescription.Book;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder>{


    // Интерфейс реализуется BookClickListener для обработки
    // прикосновения к элементу в списке RecyclerView
    public interface BookClickListener {
        void onClick(Uri bookUri);
    }

    // Вложенный субкласс RecyclerView.ViewHolder используется
    // для реализации паттерна View–Holder в контексте RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView;
        private long rowID;

        // Настройка объекта ViewHolder элемента RecyclerView
        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(android.R.id.text1);

            // Присоединение слушателя к itemView
            // Выполняется при щелчке на книгу в ViewHolder
            itemView.setOnClickListener(
                    view -> clickListener.onClick(Book.buildBookUri(rowID))
            );
        }

        // Идентификатор записи базы данных для книги в ViewHolder
        public void setRowID(long rowID) {
            this.rowID = rowID;
        }
    }


    private Cursor cursor = null;
    private final BookClickListener clickListener;

    // Конструктор
    public BookAdapter(BookClickListener clickListener) {
        this.clickListener = clickListener;
    }

    // Подготовка нового элемента списка и его объекта ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Заполнение макета android.R.layout.simple_list_item_1
        View view = LayoutInflater.from(parent.getContext()).inflate(
                android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view); // ViewHolder текущего элемента
    }

    // Назначает текст элемента списка
    @SuppressLint({"Range", "SetTextI18n"})
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.setRowID(cursor.getLong(cursor.getColumnIndex(Book._ID)));

        String text = "<font color=#000000>"+ cursor.getString(cursor.getColumnIndex(
                Book.COLUMN_TITLE)) + "</font>" + "<br>" + "<font color=#808080>" + cursor.getString(cursor.getColumnIndex(
                Book.COLUMN_AUTHOR)) + "</font>";
        holder.textView.setText(Html.fromHtml(text));
        /*holder.textView.setText(cursor.getString(cursor.getColumnIndex(
                Book.COLUMN_AUTHOR)) + " - " + '"' + cursor.getString(cursor.getColumnIndex(
                Book.COLUMN_TITLE)) + '"');*/
    }

    // Возвращает количество элементов, предоставляемых адаптером
    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    // Текущий объект Cursor адаптера заменяется новым
    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

}
