package com.chizoba.journal.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.chizoba.journal.AppExecutors;
import com.chizoba.journal.adapters.HomeRecyclerViewAdapter;
import com.chizoba.journal.R;
import com.chizoba.journal.database.AppDatabase;
import com.chizoba.journal.database.NoteEntry;
import com.chizoba.journal.utils.AuthUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener,
        HomeRecyclerViewAdapter.ItemClickListener {

    private int NO_OF_COLUMNS = 2;
    private AppDatabase db;
    private HomeRecyclerViewAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        checkAuth();
        initUI();
        db = AppDatabase.getInstance(getApplicationContext());
        retrieveNotes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkAuth() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        }
    }

    private void initUI() {
        FloatingActionButton addButton = findViewById(R.id.floatingActionButton);
        addButton.setOnClickListener(this);

        RecyclerView homeRecyclerView = findViewById(R.id.mainRecyclerView);
        homeRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(NO_OF_COLUMNS, StaggeredGridLayoutManager.VERTICAL));
        recyclerAdapter = new HomeRecyclerViewAdapter(this);
        homeRecyclerView.setAdapter(recyclerAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<NoteEntry> noteEntries = recyclerAdapter.getNotes();
                        db.noteDao().deleteNote(noteEntries.get(position));
                    }
                });
            }
        }).attachToRecyclerView(homeRecyclerView);

    }

    private void retrieveNotes() {
        LiveData<List<NoteEntry>> notesLiveData = db.noteDao().getAllNotes();
        notesLiveData.observe(this, new Observer<List<NoteEntry>>() {
            @Override
            public void onChanged(@Nullable List<NoteEntry> noteEntries) {
                recyclerAdapter.setData(noteEntries);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingActionButton:
                startActivity(new Intent(this, DetailActivity.class));
        }
    }

    @Override
    public void onItemClick(int itemId) {
        startActivity(new Intent(this, DetailActivity.class)
                .putExtra(DetailActivity.EXTRA_NOTE_ID, itemId));
    }

    private void signOut() {
        GoogleSignInClient mGoogleSignInClient = AuthUtils.getUser(this);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(HomeActivity.this, "Goodbye", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(HomeActivity.this, AuthActivity.class));
                        finish();
                    }
                });
    }
}
