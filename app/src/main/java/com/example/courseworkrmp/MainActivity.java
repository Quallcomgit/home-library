package com.example.courseworkrmp;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements CatalogFragment.BooksFragmentListener,
        DetailFragment.DetailFragmentListener,
        AddEditFragment.AddEditFragmentListener,
        FavouriteBookFragment.BooksFragmentListener,
        Serializable {

    public static final String BOOK_URI = "contact_uri";
    private CatalogFragment mainFragment;
    private FavouriteBookFragment favouriteBookFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null &&
                findViewById(R.id.fragmentContainer) != null) {

            mainFragment = new CatalogFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, mainFragment);
            fragmentTransaction.commit();
        } /*else {
            mainFragment =
                    (MainFragment) getSupportFragmentManager().
                            findFragmentById(R.id.contentFragment);
        }*/

    }

    // Отображение фрагмента для добавления или изменения записи
    private void displayAddEditFragment(int viewID, Uri bookUri) {
        AddEditFragment addEditFragment = new AddEditFragment();

        // При изменении передается аргумент bookUri
        if (bookUri != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(BOOK_URI, bookUri);
            addEditFragment.setArguments(arguments);
        }

        // Использование FragmentTransaction для отображения AddEditFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // Приводит к отображению AddEditFragment
    }

    @Override
    public void onBookSelected(Uri bookUri) {

        if (findViewById(R.id.fragmentContainer) != null)
            displayBook(bookUri, R.id.fragmentContainer);
        /*else {
            getSupportFragmentManager().popBackStack();
            displayContact(bookUri, R.id.rightPaneContainer);
        }*/
    }

    public void onAddFavourite()
    {
        if (findViewById(R.id.fragmentContainer) != null) // Телефон
            displayFavouriteFragment(R.id.fragmentContainer, null);
    }

    private void displayFavouriteFragment(int viewID, Uri bookUri) {
        favouriteBookFragment = new FavouriteBookFragment();

        // При изменении передается аргумент bookUri
        if (bookUri != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(BOOK_URI, bookUri);
            favouriteBookFragment.setArguments(arguments);
        }

        // Использование FragmentTransaction для отображения FavouriteBookFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, favouriteBookFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // Приводит к отображению FavouriteBookFragment
    }

    // Отображение информации о записи
    private void displayBook(Uri bookUri, int viewID) {
        DetailFragment detailFragment = new DetailFragment();

        // Передача URI книги в аргументе DetailFragment
        Bundle arguments = new Bundle();
        arguments.putParcelable(BOOK_URI, bookUri);
        detailFragment.setArguments(arguments);

        // Использование FragmentTransaction для отображения
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // Приводит к отображению DetailFragment
    }

    // Отображение AddEditFragment для добавления новой книги
    @Override
    public void onAddBook() {
        if (findViewById(R.id.fragmentContainer) != null) // Телефон
            displayAddEditFragment(R.id.fragmentContainer, null);
        /*else // Планшет
            displayAddEditFragment(R.id.rightPaneContainer, null);*/
    }

    // Возвращение к списку записей при удалении текущей записи
    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onBookDeleted() {
        // Удаление с вершины стека
        getSupportFragmentManager().popBackStack();
        mainFragment.updateBookList(); // Обновление записей
        if(favouriteBookFragment!= null)
        favouriteBookFragment.updateBookList();
    }

    // Отображение AddEditFragment для изменения существующей записи
    @Override
    public void onEditBook(Uri bookUri) {
        if (findViewById(R.id.fragmentContainer) != null) // Телефон
            displayAddEditFragment(R.id.fragmentContainer, bookUri);
        /*else // Планшет
            displayAddEditFragment(R.id.rightPaneContainer, bookUri);*/
    }

    // Обновление GUI после сохранения новой или существующей записи
    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onAddEditCompleted(Uri bookUri) {
        // Удаление вершины стека возврата
        getSupportFragmentManager().popBackStack();
        mainFragment.updateBookList(); // Обновление записей
        if(favouriteBookFragment!= null)
        favouriteBookFragment.updateBookList();

        if (findViewById(R.id.fragmentContainer) == null) { // Планшет
            // Удаление с вершины стека возврата
            getSupportFragmentManager().popBackStack();

            // На планшете выводится добавленная или измененная запись
            /*displayContact(bookUri, R.id.rightPaneContainer);*/
        }
    }

    @Override
    protected void onDestroy() {
        /*Intent intent = this.getIntent();
        this.finish();
        startActivity(intent);
        this.overridePendingTransition(R.anim.animation_in, R.anim.animation_out);*/
        super.onDestroy();
    }
}