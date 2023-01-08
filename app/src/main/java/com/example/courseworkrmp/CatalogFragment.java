package com.example.courseworkrmp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courseworkrmp.data.DatabaseDescription.Book;

public class CatalogFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Метод обратного вызова, реализуемый MainActivity
    public interface BooksFragmentListener {
        // Вызывается при выборе записи
        void onBookSelected(Uri bookUri);

        // Вызывается при нажатии кнопки добавления
        void onAddBook();

        void onAddFavourite();
    }

    private static final int BOOK_LOADER = 0; // Идентификатор Loader
    private static final int BOOK_SEARCH = 1; // Идентификатор Loader
    private BooksFragmentListener listener; // Сообщает MainActivity о выборе записи
    private BookAdapter bookAdapter; // Адаптер для recyclerView

    private SearchView searchView;
    private String textChange;

    // Настройка графического интерфейса фрагмента
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // У фрагмента есть команды меню

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getResources().getString(R.string.app_name));
        actionBar.setDisplayHomeAsUpEnabled(false);
        // Заполнение GUI и получение ссылки на RecyclerView
        View view = inflater.inflate(
                R.layout.catalog_fragment, container, false);
        RecyclerView recyclerView =
                (RecyclerView) view.findViewById(R.id.recyclerView);

        // recyclerView выводит элементы в вертикальном списке
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity().getBaseContext()));

        // создание адаптера recyclerView и слушателя щелчков на элементах
        bookAdapter = new BookAdapter(
                bookUri -> listener.onBookSelected(bookUri)
        );
        recyclerView.setAdapter(bookAdapter); // Назначение адаптера

        searchView = (SearchView) view.findViewById(R.id.searchView);
        // Присоединение ItemDecorator для вывода разделителей
        recyclerView.addItemDecoration(new ItemDivider(requireContext()));

        // Улучшает быстродействие, если размер макета RecyclerView не изменяется
        recyclerView.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onViewCreated(view, savedInstanceState);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                updateBookList();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                textChange = s;

                restartLoader();
                updateBookList();
                return false;
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        searchView.onActionViewCollapsed();
        searchView.clearFocus();
        searchView.setQuery("", false);
    }

    public void restartLoader() {
        LoaderManager.getInstance(requireActivity()).restartLoader(
                BOOK_SEARCH, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_catalog, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.actionAdd) {
            listener.onAddBook();
            AddEditFragment.isAddBook = true;
        }
        else
            if (item.getItemId() == R.id.actionFavourites) {
                listener.onAddFavourite();;
            }
        return super.onOptionsItemSelected(item);
    }

    // Присваивание BooksFragmentListener при присоединении фрагмента
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (BooksFragmentListener) context;
    }

    // Удаление BooksFragmentListener при отсоединении фрагмента
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // Инициализация Loader при создании активности этого фрагмента
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoaderManager.getInstance(this).initLoader(
                BOOK_LOADER, null, this);
    }

    // Вызывается из MainActivity при обновлении базы данных другим фрагментом
    public void updateBookList() {
        bookAdapter.notifyDataSetChanged();
    }

    // Вызывается LoaderManager для создания Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Создание CursorLoader на основании аргумента id;
        switch (id) {
            case BOOK_LOADER:
                return new CursorLoader(requireActivity(),
                        Book.BOOK_URI, // Uri таблицы books
                        null, // все столбцы
                        null, // все записи
                        null, // без аргументов
                        Book.COLUMN_TITLE + " COLLATE NOCASE ASC"); // сортировка
            case BOOK_SEARCH:
                return new CursorLoader(requireActivity(),
                        Book.BOOK_URI, // Uri таблицы books
                        null, // все столбцы
                        Book.COLUMN_TITLE + " LIKE '%" + textChange + "%'"
                                + " OR " + Book.COLUMN_AUTHOR + " LIKE '%" + textChange + "%'",
                        null, // без аргументов
                        Book.COLUMN_TITLE + " COLLATE NOCASE ASC");
            default:
                return null;
        }
    }

    // Вызывается LoaderManager при завершении загрузки
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        bookAdapter.swapCursor(data);
    }

    // Вызывается LoaderManager при сбросе Loader
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookAdapter.swapCursor(null);
    }

}