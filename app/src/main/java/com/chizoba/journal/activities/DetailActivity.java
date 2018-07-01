package com.chizoba.journal.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.chizoba.journal.AppExecutors;
import com.chizoba.journal.R;
import com.chizoba.journal.database.AppDatabase;
import com.chizoba.journal.database.NoteEntry;

import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    // Extra for the note ID to be received after rotation
    public static final String INSTANCE_NOTE_ID = "instanceNoteId";
    // Extra for the note ID to be received in the intent
    public static final String EXTRA_NOTE_ID = "extraNoteId";
    // Constant for default note id to be used when not in update mode
    private static final int DEFAULT_NOTE_ID = -1;

    public static final String NOTE_TITLE = "NOTE_TITLE";
    public static final String NOTE_DETAIL = "NOTE_DETAIL";

    private AppDatabase db;
    private EditText titleEditText, detailEditText;

    private int mNoteId = DEFAULT_NOTE_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        getSupportActionBar().setTitle(R.string.add_note);

        initUI();
        db = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_NOTE_ID)) {
            mNoteId = savedInstanceState.getInt(INSTANCE_NOTE_ID, DEFAULT_NOTE_ID);
            titleEditText.setText(savedInstanceState.getString(NOTE_TITLE));
            detailEditText.setText(savedInstanceState.getString(NOTE_DETAIL));
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_NOTE_ID)) {
            if (mNoteId == DEFAULT_NOTE_ID) {
                // populate the UI
                mNoteId = intent.getIntExtra(EXTRA_NOTE_ID, DEFAULT_NOTE_ID);
                Log.d("MINE", mNoteId + "");
                Log.d(DetailActivity.class.getSimpleName(), "Actively retrieving a specific note from the DataBase");
                final LiveData<NoteEntry> noteLiveData = db.noteDao().getNote(mNoteId);
                noteLiveData.observe(this, new Observer<NoteEntry>() {
                    @Override
                    public void onChanged(@Nullable NoteEntry noteEntry) {
                        noteLiveData.removeObserver(this);
                        Log.d(DetailActivity.class.getSimpleName(), "Receiving database update from LiveData");
                        populateUI(noteEntry);
                    }
                });
            }
        }
    }

    private void initUI() {
        titleEditText = findViewById(R.id.titleEditText);
        detailEditText = findViewById(R.id.detailEditText);
    }

    private void populateUI(NoteEntry note) {
        if (note == null) {
            return;
        }

        titleEditText.setText(note.getTitle());
        detailEditText.setText(note.getBody());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_NOTE_ID, mNoteId);
        outState.putString(NOTE_TITLE, titleEditText.getText().toString());
        outState.putString(NOTE_DETAIL, detailEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                    saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem saveItem = menu.findItem(R.id.save);
        if(mNoteId == DEFAULT_NOTE_ID){
            saveItem.setTitle(R.string.save);
        } else {
            saveItem.setTitle(R.string.update);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void saveNote() {
        String title = titleEditText.getText().toString();
        String body = detailEditText.getText().toString();

        final NoteEntry noteEntry = new NoteEntry(title, body, new Date());

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mNoteId == DEFAULT_NOTE_ID) {
                    //save
                    db.noteDao().saveNote(noteEntry);
                } else {
                    //update
                    noteEntry.setId(mNoteId);
                    db.noteDao().updateNote(noteEntry);
                }
                finish();
            }
        });
    }
}
