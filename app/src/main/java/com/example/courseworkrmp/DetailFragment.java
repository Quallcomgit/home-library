package com.example.courseworkrmp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.courseworkrmp.data.DatabaseDescription.Book;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import id.zelory.compressor.Compressor;

public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    // Методы обратного вызова, реализованные MainActivity
    public interface DetailFragmentListener {
        void onBookDeleted(); // Вызывается при удалении записи

        // Передает URI редактируемой книги DetailFragmentListener
        void onEditBook(Uri bookUri);
    }

    private static final int BOOK_LOADER = 0; // Идентифицирует Loader

    private DetailFragmentListener listener; // MainActivity
    private Uri bookUri; // Uri выбранной записи

    private TextView authorTextView;
    private TextView titleTextView;
    private TextView genreTextView;
    private TextView whereBookTextView;
    private TextView chapterTextView;
    private TextView rackTextView;
    private TextView shelfTextView;
    private TextView centuryTextView;
    private TextView isReadTextView;
    private TextView whoHasBookTextView;
    private ImageView photoImageView;
    private TextView textImageView;
    private TextView pdfFileTextView;
    private TextView isFavouriteTextView;

    private TextView whoHasBookDetails;
    private TextView chapterTextViewDetails;
    private TextView rackTextViewDetails;
    private TextView shelfTextViewDetails;
    private TextView centuryTextViewDetails;

    // Назначение DetailFragmentListener при присоединении фрагмента
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (DetailFragmentListener) context;
    }

    // Удаление DetailFragmentListener при отсоединении фрагмента
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // Вызывается при создании представлений фрагмента
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // У фрагмента есть команды меню

        // Получение объекта Bundle с аргументами и извлечение URI
        Bundle arguments = getArguments();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getResources().getString(R.string.informationName));
        actionBar.setDisplayHomeAsUpEnabled(false);

        if (arguments != null)
            bookUri = arguments.getParcelable(com.example.courseworkrmp.MainActivity.BOOK_URI);

        // Заполнение макета DetailFragment
        View view =
                inflater.inflate(R.layout.book_information_fragment, container, false);


        authorTextView = (TextView) view.findViewById(R.id.textViewSetAuthor);
        titleTextView = (TextView) view.findViewById(R.id.textViewSetTitle);
        genreTextView = (TextView) view.findViewById(R.id.textViewSetGenre);
        whereBookTextView = (TextView) view.findViewById(R.id.textViewSetWhereBook);
        chapterTextView = (TextView) view.findViewById(R.id.textViewSetChapter);
        rackTextView = (TextView) view.findViewById(R.id.textViewSetRack);
        shelfTextView = (TextView) view.findViewById(R.id.textViewSetShelf);
        centuryTextView = (TextView) view.findViewById(R.id.textViewSetCentury);
        isReadTextView = (TextView) view.findViewById(R.id.textViewSetIsRead);
        whoHasBookTextView = (TextView) view.findViewById(R.id.textViewSetWhoHasBook);
        photoImageView = (ImageView) view.findViewById(R.id.imageViewSetPhoto);
        textImageView = (TextView) view.findViewById(R.id.imageViewSetText);
        pdfFileTextView = (TextView) view.findViewById(R.id.textViewSetPdfFile);
        isFavouriteTextView = (TextView) view.findViewById(R.id.textViewSetInFavourite);

        whoHasBookDetails = (TextView) view.findViewById(R.id.textViewDetailsWhoHasBook);
        chapterTextViewDetails = (TextView) view.findViewById(R.id.textViewDetailsChapter);
        rackTextViewDetails = (TextView) view.findViewById(R.id.textViewDetailsRack);
        shelfTextViewDetails = (TextView) view.findViewById(R.id.textViewDetailsShelf);
        centuryTextViewDetails = (TextView) view.findViewById(R.id.textViewDetailsCentury);

                // Загрузка книги
        LoaderManager.getInstance(this).initLoader(BOOK_LOADER, null, this);
        return view;
    }

    // Отображение команд меню фрагмента
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    // Обработка выбора команд меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionEdit:
                listener.onEditBook(bookUri); // Передача Uri слушателю
                AddEditFragment.isAddBook = false;
                return true;
            case R.id.actionDelete:
                deleteBook(); // метод описан ниже
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class ConfirmDialogFragment extends DialogFragment {

        public static ConfirmDialogFragment newInstance(Uri uri, Context context) {
            ConfirmDialogFragment frag = new ConfirmDialogFragment();
            Bundle args = new Bundle();
            args.putParcelable(com.example.courseworkrmp.MainActivity.BOOK_URI,uri);
            args.putSerializable("listener",(Serializable)context);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Uri uri=getArguments().getParcelable(MainActivity.BOOK_URI);
            final DetailFragmentListener listener =
                    (DetailFragmentListener)getArguments().getSerializable("listener");
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.confirmTitle)
                    .setMessage(R.string.confirmMessage)
                    .setPositiveButton(R.string.buttonDelete,
                            (dialog, button) -> {
                                // объект ContentResolver используется
                                // для вызова delete в AddressBookContentProvider
                                getActivity().getContentResolver().delete(
                                        uri, null, null);
                                listener.onBookDeleted(); // Оповещение слушателя
                            }
                    )
                    .setNegativeButton(R.string.buttonCancel,null)
                    .create();
        }
    }

    // DialogFragment для подтверждения удаления записи
    private DialogFragment confirmDelete;

    // Удаление записи
    private void deleteBook() {
        // FragmentManager используется для отображения confirmDelete
        confirmDelete= ConfirmDialogFragment.newInstance(bookUri, (Context)listener);
        confirmDelete.show(getParentFragmentManager(), "confirm delete");
    }

    // Вызывается LoaderManager для создания Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Создание CursorLoader на основании аргумента id; в этом
        // фрагменте только один объект Loader, и команда switch не нужна
        CursorLoader cursorLoader;

        switch (id) {
            case BOOK_LOADER:
                cursorLoader = new CursorLoader(getActivity(),
                        bookUri, // Uri отображаемой записи
                        null, // Все столбцы
                        null, // Все записи
                        null, // Без аргументов
                        null); // Порядок сортировки
                break;
            default:
                cursorLoader = null;
                break;
        }

        return cursorLoader;
    }

    // Вызывается LoaderManager при завершении загрузки
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Если запись существует в базе данных, вывести её информацию
        if (data != null && data.moveToFirst()) {
            // Получение индекса столбца для каждого элемента данных
            int authorIndex = data.getColumnIndex(Book.COLUMN_AUTHOR);
            int titleIndex = data.getColumnIndex(Book.COLUMN_TITLE);
            int genreIndex = data.getColumnIndex(Book.COLUMN_GENRE);
            int whereBookIndex = data.getColumnIndex(Book.COLUMN_WHERE_BOOK);
            int chapterIndex = data.getColumnIndex(Book.COLUMN_CHAPTER);
            int rackIndex = data.getColumnIndex(Book.COLUMN_RACK);
            int shelfIndex = data.getColumnIndex(Book.COLUMN_SHELF);
            int centuryIndex = data.getColumnIndex(Book.COLUMN_CENTURY);
            int whoHasBookIndex = data.getColumnIndex(Book.COLUMN_WHO_HAS_BOOK);
            int isReadIndex = data.getColumnIndex(Book.COLUMN_IS_READ);
            int photoIndex = data.getColumnIndex(Book.COLUMN_PHOTO);
            int electVersionIndex = data.getColumnIndex(Book.COLUMN_ELECT_VERSION);
            int isFavouriteIndex = data.getColumnIndex(Book.COLUMN_FAVOURITE);

            String select = data.getString(whereBookIndex);
            // Заполнение TextView полученными данными
            authorTextView.setText(data.getString(authorIndex));
            titleTextView.setText(data.getString(titleIndex));
            genreTextView.setText(data.getString(genreIndex));
            whereBookTextView.setText(select);
            isReadTextView.setText(data.getString(isReadIndex));

            if(select.equals(getResources().getString(R.string.giveTo)))
            {
                whoHasBookDetails.setVisibility(View.VISIBLE);
                whoHasBookTextView.setVisibility(View.VISIBLE);

                whoHasBookTextView.setText(data.getString(whoHasBookIndex));
            }
            else
            {
                whoHasBookDetails.setVisibility(View.GONE);
                whoHasBookTextView.setVisibility(View.GONE);
            }

            if(select.equals(getResources().getString(R.string.atHome)))
            {
                chapterTextViewDetails.setVisibility(View.VISIBLE);
                rackTextViewDetails.setVisibility(View.VISIBLE);
                shelfTextViewDetails.setVisibility(View.VISIBLE);
                centuryTextViewDetails.setVisibility(View.VISIBLE);

                chapterTextView.setVisibility(View.VISIBLE);
                rackTextView.setVisibility(View.VISIBLE);
                shelfTextView.setVisibility(View.VISIBLE);
                centuryTextView.setVisibility(View.VISIBLE);

                chapterTextView.setText(data.getString(chapterIndex));
                rackTextView.setText(data.getString(rackIndex));
                shelfTextView.setText(data.getString(shelfIndex));
                centuryTextView.setText(data.getString(centuryIndex));
            }
            else
            {
                chapterTextViewDetails.setVisibility(View.GONE);
                rackTextViewDetails.setVisibility(View.GONE);
                shelfTextViewDetails.setVisibility(View.GONE);
                centuryTextViewDetails.setVisibility(View.GONE);

                chapterTextView.setVisibility(View.GONE);
                rackTextView.setVisibility(View.GONE);
                shelfTextView.setVisibility(View.GONE);
                centuryTextView.setVisibility(View.GONE);
            }

            try {
                textImageView.setVisibility(View.INVISIBLE);
                File imageFile = new File(data.getString(photoIndex));
                try {
                    imageFile = new Compressor(requireContext()).compressToFile(imageFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()), getResources().getDimensionPixelOffset(R.dimen.widthImage),
                        getResources().getDimensionPixelOffset(R.dimen.heightImage), true);
                photoImageView.setImageBitmap(bitmap);
            }
            catch (NullPointerException np)
            {
                textImageView.setVisibility(View.VISIBLE);
                photoImageView.setBackground(null);
                photoImageView.setImageBitmap(null);
            }
            pdfFileTextView.setText(data.getString(electVersionIndex));
            isFavouriteTextView.setText(data.getString(isFavouriteIndex));
        }
    }

    // Вызывается LoaderManager при сбросе Loader
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
} // окончание класса
