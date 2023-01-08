package com.example.courseworkrmp;

import static android.app.Activity.RESULT_OK;
import static android.widget.ImageView.ScaleType.CENTER_CROP;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.courseworkrmp.data.BookDatabaseHelper;
import com.example.courseworkrmp.data.DatabaseDescription.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import id.zelory.compressor.Compressor;

public class AddEditFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Определяет метод обратного вызова, реализованный MainActivity
    public interface AddEditFragmentListener {
        // Вызывается при сохранении записи
        void onAddEditCompleted(Uri bookUri);
    }

    public static boolean isAddBook;
    // Константа для идентификации Loader
    private static final int BOOK_LOADER = 0;

    private AddEditFragmentListener listener; // MainActivity
    private Uri bookUri; // Uri выбранной записи
    private boolean addingNewBook = true; // Добавление (true) или изменение

    private BookDatabaseHelper dbHelper;

    private static final int SELECT_PICTURE = 1;
    private static final int SAVE_IMAGE_PERMISSION_REQUEST_CODE = 1;
    private String selectedImagePath;

    // Компоненты EditText для информации записи
    private AutoCompleteTextView authorEditText;
    private AutoCompleteTextView titleEditText;
    private AutoCompleteTextView genreEditText;
    private Spinner whereBookSpinner;
    private Spinner chapterSpinner;
    private Spinner rackSpinner;
    private Spinner shelfSpinner;
    private Spinner centurySpinner;
    private CheckBox isReadCheckBox;
    private AutoCompleteTextView whoHasBookEditText;
    private ImageButton imageButtonImageButton;
    private TextView imageButtonTextView;
    private Button pdfButtonButton;
    private CheckBox isFavouriteCheckBox;
    /*private String eatStatus;
    private CheckBox brokeDietEditText;*/
    private FloatingActionButton saveBookFAB;

    private CoordinatorLayout coordinatorLayout; // Для SnackBar

    // Назначение AddEditFragmentListener при присоединении фрагмента
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AddEditFragmentListener) context;
    }

    // Вызывается при создании представлений фрагмента
    @Override
    public View onCreateView(

            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // У фрагмента есть команды меню

        // Заполнение GUI и получение ссылок на компоненты EditText
        View view =
                inflater.inflate(R.layout.add_change_book_main, container, false);
        authorEditText =
                (AutoCompleteTextView) view.findViewById(R.id.editTextAuthor);
        String[] dataAuthor = getColumnData(Book.COLUMN_AUTHOR);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(), R.layout.dropdown, dataAuthor);
        authorEditText.setAdapter(adapter);
        authorEditText.setThreshold(1);
        authorEditText.addTextChangedListener(
                editTextChangedListener);

        titleEditText =
                (AutoCompleteTextView) view.findViewById(R.id.editTextTitle);
        String[] dataTitle = getColumnData(Book.COLUMN_TITLE);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(requireActivity(), R.layout.dropdown, dataTitle);
        titleEditText.setAdapter(adapter1);
        titleEditText.setThreshold(1);
        titleEditText.addTextChangedListener(
                editTextChangedListener);

        genreEditText =
                (AutoCompleteTextView) view.findViewById(R.id.editTextGenre);
        String[] dataGenre = getColumnData(Book.COLUMN_GENRE);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(requireActivity(), R.layout.dropdown, dataGenre);
        genreEditText.setAdapter(adapter2);
        genreEditText.setThreshold(1);
        genreEditText.addTextChangedListener(
                editTextChangedListener);

        whereBookSpinner =
                (Spinner) view.findViewById(R.id.spinnerWhereBook);
        chapterSpinner =
                (Spinner) view.findViewById(R.id.spinnerChooseChapter);
        rackSpinner =
                (Spinner) view.findViewById(R.id.spinnerChooseRack);
        shelfSpinner =
                (Spinner) view.findViewById(R.id.spinnerChooseShelf);
        centurySpinner =
                (Spinner) view.findViewById(R.id.spinnerChooseCentury);
        isReadCheckBox =
                (CheckBox) view.findViewById(R.id.checkBoxIsRead);

        whoHasBookEditText =
                (AutoCompleteTextView) view.findViewById(R.id.editTextWhoHasBook);
        String[] dataWhoHasBook = getColumnData(Book.COLUMN_WHO_HAS_BOOK);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(requireActivity(), R.layout.dropdown, dataWhoHasBook);
        whoHasBookEditText.setAdapter(adapter3);
        whoHasBookEditText.setThreshold(1);
        whoHasBookEditText.addTextChangedListener(
                editTextChangedListener);

        imageButtonTextView = (TextView) view.findViewById(R.id.imageButtonText);
        imageButtonImageButton =
                (ImageButton) view.findViewById(R.id.imageButton);

        imageButtonImageButton.setOnClickListener(view1 -> {
            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, SELECT_PICTURE);
            } else {

                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_PICTURE);
            }

        });

        pdfButtonButton =
                (Button) view.findViewById(R.id.button);
        /*pdfButtonButton.setOnClickListener(view1 ->
        {

        });*/

        isFavouriteCheckBox =
                (CheckBox) view.findViewById(R.id.checkBoxFavourite);

        TextView whoHasBook = (TextView) view.findViewById(R.id.textViewWhoHasBook);
        TextView chapter = (TextView) view.findViewById(R.id.textViewChapter);
        TextView rack = (TextView) view.findViewById(R.id.textViewRack);
        TextView shelf = (TextView) view.findViewById(R.id.textViewShelf);
        TextView century = (TextView) view.findViewById(R.id.textViewCentury);
        whereBookSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String select = ((TextView) whereBookSpinner.getSelectedView())
                        .getText().toString();

                if (select.equals(getResources().getString(R.string.giveTo))) {
                    whoHasBook.setVisibility(View.VISIBLE);
                    whoHasBookEditText.setVisibility(View.VISIBLE);
                } else {
                    whoHasBook.setVisibility(View.GONE);
                    whoHasBookEditText.setVisibility(View.GONE);
                }

                if (select.equals(getResources().getString(R.string.atHome))) {
                    chapter.setVisibility(View.VISIBLE);
                    rack.setVisibility(View.VISIBLE);
                    shelf.setVisibility(View.VISIBLE);
                    century.setVisibility(View.VISIBLE);

                    chapterSpinner.setVisibility(View.VISIBLE);
                    rackSpinner.setVisibility(View.VISIBLE);
                    shelfSpinner.setVisibility(View.VISIBLE);
                    centurySpinner.setVisibility(View.VISIBLE);
                } else {
                    chapter.setVisibility(View.GONE);
                    rack.setVisibility(View.GONE);
                    shelf.setVisibility(View.GONE);
                    century.setVisibility(View.GONE);

                    chapterSpinner.setVisibility(View.GONE);
                    rackSpinner.setVisibility(View.GONE);
                    shelfSpinner.setVisibility(View.GONE);
                    centurySpinner.setVisibility(View.GONE);
                }

                updateSaveButtonFAB();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Назначение слушателя событий FloatingActionButton
        saveBookFAB = (FloatingActionButton) view.findViewById(
                R.id.floatingActionButtonSaveBook);
        saveBookFAB.setOnClickListener(saveBookButtonClicked);
        updateSaveButtonFAB(); // метод описан ниже

        // Используется для отображения SnackBar с короткими сообщениями
        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(
                R.id.fragmentContainer);

        Bundle arguments = getArguments(); // null при создании записи

        if (arguments != null) {
            addingNewBook = false;
            bookUri = arguments.getParcelable(MainActivity.BOOK_URI);
        }

        // При изменении существующей записи создать Loader
        if (bookUri != null)
            LoaderManager.getInstance(this).initLoader(BOOK_LOADER, null, this);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        if (isAddBook) {
            actionBar.setTitle(getResources().getString(R.string.addBook));
        } else
            actionBar.setTitle(getResources().getString(R.string.changeBook));

        return view;
    }


    @SuppressLint("Range")
    private String[] getColumnData(String columnName) {
        dbHelper = new BookDatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                Book.TABLE_NAME,
                new String[]{columnName},
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<String> arrayList = new ArrayList<String>();

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            arrayList.add(c.getString(c.getColumnIndex(columnName)));
        }

        //убираем повтор
        Set<String> set = new HashSet<>(arrayList);
        arrayList.clear();
        arrayList.addAll(set);

        return arrayList.toArray(new String[arrayList.size()]);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                Uri selectedImageUri = data.getData();

                //MEDIA GALLERY
                selectedImagePath = getPath(selectedImageUri);

                if (selectedImagePath == null || selectedImagePath.contains(".mp4")) {
                    imageButtonTextView.setVisibility(View.VISIBLE);
                    imageButtonImageButton.setBackground(getResources().getDrawable(R.drawable.color_image_button));
                    imageButtonImageButton.setImageBitmap(null);
                    imageButtonImageButton.setEnabled(true);
                    Toast.makeText(requireContext(), getString(R.string.choosePhoto), Toast.LENGTH_SHORT).show();
                } else {
                    imageButtonTextView.setVisibility(View.INVISIBLE);
                    File imageFile = new File(selectedImagePath);
                    try {
                        imageFile = new Compressor(requireContext()).compressToFile(imageFile);
                        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()), getResources().getDimensionPixelOffset(R.dimen.widthImage),
                                getResources().getDimensionPixelOffset(R.dimen.heightImage), true);
                        imageButtonImageButton
                                .setImageBitmap(bitmap);
                        imageButtonImageButton.setBackground(null);
                        imageButtonImageButton.setScaleType(CENTER_CROP);
                        imageButtonImageButton.setEnabled(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = requireActivity().managedQuery(uri, projection, null, null, null);
        if(cursor!=null)
        {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }

    // Обнаруживает изменения в тексте поля EditTex для отображения или скрытия saveButtonFAB
    private final TextWatcher editTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        // Вызывается при изменении текста в EditTex
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            updateSaveButtonFAB(); // метод описан ниже
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    // Кнопка saveButtonFAB видна, если EditTex не пусто
    private void updateSaveButtonFAB() {
        String inputAuthor =
                authorEditText.getText().toString();
        String inputTitle =
                titleEditText.getText().toString();
        String inputGenre =
                genreEditText.getText().toString();
        String inputWhoHasBook =
                whoHasBookEditText.getText().toString();


        if (inputAuthor.trim().length() != 0
                && inputTitle.trim().length() != 0
                && inputGenre.trim().length() != 0
                && ((whoHasBookEditText.getVisibility() == View.VISIBLE && inputWhoHasBook.trim().length() != 0)
                || whoHasBookEditText.getVisibility() == View.GONE))

            saveBookFAB.show();
        else
            saveBookFAB.hide();
    }

    // Реагирует на событие, генерируемое при сохранении записи
    private final View.OnClickListener saveBookButtonClicked =
            v -> {
                // Скрыть виртуальную клавиатуру
                ((InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                        getView().getWindowToken(), 0);
                saveBook(); // Сохранение книги в базе данных

            };

    // Сохранение информации записи в базе данных
    private void saveBook() {
        // Создание объекта ContentValues с парами "ключ—значение"
        ContentValues contentValues = new ContentValues();
        contentValues.put(Book.COLUMN_AUTHOR,
                authorEditText.getText().toString());
        contentValues.put(Book.COLUMN_TITLE,
                titleEditText.getText().toString());
        contentValues.put(Book.COLUMN_GENRE,
                genreEditText.getText().toString());
        contentValues.put(Book.COLUMN_WHERE_BOOK,
                ((TextView) whereBookSpinner.getSelectedView())
                        .getText().toString());
        contentValues.put(Book.COLUMN_CHAPTER,
                ((TextView) chapterSpinner.getSelectedView())
                        .getText().toString());
        contentValues.put(Book.COLUMN_RACK,
                ((TextView) rackSpinner.getSelectedView())
                        .getText().toString());
        contentValues.put(Book.COLUMN_SHELF,
                ((TextView) shelfSpinner.getSelectedView())
                        .getText().toString());
        contentValues.put(Book.COLUMN_CENTURY,
                ((TextView) centurySpinner.getSelectedView())
                        .getText().toString());
        contentValues.put(Book.COLUMN_WHO_HAS_BOOK,
                whoHasBookEditText.getText().toString());
        contentValues.put(Book.COLUMN_IS_READ,
                isReadCheckBox.isChecked()
                        ?getResources().getString(R.string.chooseYes):
                        getResources().getString(R.string.chooseNo));
        contentValues.put(Book.COLUMN_PHOTO,
                selectedImagePath);
        contentValues.put(Book.COLUMN_ELECT_VERSION,
               "Nothing");
        contentValues.put(Book.COLUMN_FAVOURITE,
                isFavouriteCheckBox.isChecked()
                        ?getResources().getString(R.string.chooseYes):
                        getResources().getString(R.string.chooseNo));

        if (addingNewBook) {
            // Использовать объект ContentResolver активности для вызова
            // insert для объекта AddressBookContentProvider
            Uri newBookUri = getActivity().getContentResolver().insert(
                    Book.BOOK_URI, contentValues);

            if (newBookUri != null) {
                Snackbar.make(coordinatorLayout,
                        R.string.entryAdded, Snackbar.LENGTH_LONG).show();
                listener.onAddEditCompleted(newBookUri);

            } else {
                Snackbar.make(coordinatorLayout,
                        R.string.entryNotAdded, Snackbar.LENGTH_LONG).show();

            }

        } else {
            // Использовать объект ContentResolver активности для вызова
            // update для объекта AddressBookContentProvider
            int updatedRows = getActivity().getContentResolver().update(
                    bookUri, contentValues, null, null);

            if (updatedRows > 0) {
                listener.onAddEditCompleted(bookUri);
                Snackbar.make(coordinatorLayout,
                        R.string.entryUpdated, Snackbar.LENGTH_LONG).show();

            } else {
                Snackbar.make(coordinatorLayout,
                        R.string.entryNotUpdated, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    // Вызывается LoaderManager для создания Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Создание CursorLoader на основании аргумента id; в этом
        // фрагменте только один объект Loader, и команда switch не нужна
        switch (id) {
            case BOOK_LOADER:
                return new CursorLoader(getActivity(),
                        bookUri, // Uri отображаемой книги
                        null, // Все столбцы
                        null, // Все записи
                        null, // Без аргументов
                        null); // Порядок сортировки
            default:
                return null;
        }
    }

    // Вызывается LoaderManager при завершении загрузки
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Если книга существует в базе данных, вывести ее информацию
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

            // Заполнение компонентов EditText полученными данными
            authorEditText.setText(
                    data.getString(authorIndex));
            titleEditText.setText(
                    data.getString(titleIndex));
            genreEditText.setText(
                    data.getString(genreIndex));

            int whereBookSpinnerPosition = data.getString(whereBookIndex).equals(getResources().getString(R.string.atHome))?
                    0:data.getString(whereBookIndex).equals(getResources().getString(R.string.lost))?
                    1:2;
            whereBookSpinner.setSelection(whereBookSpinnerPosition);

            int chapterSpinnerPosition = data.getString(chapterIndex).equals(getResources().getString(R.string.artisticChapter))?
                    0:data.getString(chapterIndex).equals(getResources().getString(R.string.childrenChapter))?
                    1:data.getString(chapterIndex).equals(getResources().getString(R.string.educationChapter))?
                    2:data.getString(chapterIndex).equals(getResources().getString(R.string.psychologyChapter))?
                    3:data.getString(chapterIndex).equals(getResources().getString(R.string.philosophyChapter))?
                    4:data.getString(chapterIndex).equals(getResources().getString(R.string.foreignChapter))?
                    5:6;
            chapterSpinner.setSelection(chapterSpinnerPosition);

            int rackSpinnerPosition = data.getInt(rackIndex);
            rackSpinner.setSelection(rackSpinnerPosition - 1);

            int shelfSpinnerPosition = data.getInt(shelfIndex) - 1;
            shelfSpinner.setSelection(shelfSpinnerPosition);

            int centurySpinnerPosition = data.getString(centuryIndex).equals(getResources().getString(R.string.twentyOneCentury))?
                    0:data.getString(centuryIndex).equals(getResources().getString(R.string.twentyCentury))?
                    1:data.getString(centuryIndex).equals(getResources().getString(R.string.nineteenCentury))?
                    2:data.getString(centuryIndex).equals(getResources().getString(R.string.eighteenCentury))?
                    3:data.getString(centuryIndex).equals(getResources().getString(R.string.seventeenCentury))?
                    4:data.getString(centuryIndex).equals(getResources().getString(R.string.sixteenCentury))?
                    5:data.getString(centuryIndex).equals(getResources().getString(R.string.fifteenCentury))?
                    6:7;

            centurySpinner.setSelection(centurySpinnerPosition);

            if(data.getString(isReadIndex).equals(getResources().getString(R.string.chooseYes)))
            {
                isReadCheckBox.setChecked(true);
            }
            else
                isReadCheckBox.setChecked(false);

            whoHasBookEditText.setText(
                    data.getString(whoHasBookIndex));

            if(selectedImagePath == null)
            {
                selectedImagePath = data.getString(photoIndex);
                try {
                    imageButtonTextView.setVisibility(View.INVISIBLE);
                    File imageFile = new File(selectedImagePath);
                    imageFile = new Compressor(requireContext()).compressToFile(imageFile);
                    Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()), getResources().getDimensionPixelOffset(R.dimen.widthImage),
                            getResources().getDimensionPixelOffset(R.dimen.heightImage), true);
                    imageButtonImageButton.setImageBitmap(bitmap);
                    imageButtonImageButton.setBackground(null);
                    imageButtonImageButton.setScaleType(CENTER_CROP);
                    imageButtonImageButton.setEnabled(true);
                }
                catch (NullPointerException np)
                {
                    imageButtonTextView.setVisibility(View.VISIBLE);
                    imageButtonImageButton.setBackground(getResources().getDrawable(R.drawable.color_image_button));
                    imageButtonImageButton.setImageBitmap(null);
                    imageButtonImageButton.setEnabled(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /*pdfButtonButton.setText(data.getString(electVersionIndex));*/

            if(data.getString(isFavouriteIndex).equals(getResources().getString(R.string.chooseYes)))
            {
                isFavouriteCheckBox.setChecked(true);
            }
            else
                isFavouriteCheckBox.setChecked(false);



            updateSaveButtonFAB();

        }
    }

    // Вызывается LoaderManager при сбросе Loader
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    // Удаление AddEditFragmentListener при отсоединении фрагмента
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        authorEditText.removeTextChangedListener(editTextChangedListener);
        titleEditText.removeTextChangedListener(editTextChangedListener);
        genreEditText.removeTextChangedListener(editTextChangedListener);
        whoHasBookEditText.removeTextChangedListener(editTextChangedListener);
        imageButtonImageButton.setOnClickListener(null);
        saveBookFAB.setOnClickListener(null);
        whereBookSpinner.setOnItemSelectedListener(null);
    }
} // окончание класса
