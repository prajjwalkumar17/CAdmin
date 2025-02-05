package com.thundersharp.cadmin.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.thundersharp.cadmin.R;
import com.thundersharp.cadmin.core.notes.adapters.NotesAdapter;
import com.thundersharp.cadmin.core.notes.callbacks.MainActionModeCallback;
import com.thundersharp.cadmin.core.notes.callbacks.NoteEventListener;
import com.thundersharp.cadmin.core.notes.db.NotesDB;
import com.thundersharp.cadmin.core.notes.db.NotesDao;
import com.thundersharp.cadmin.core.notes.model.Note;
import com.thundersharp.cadmin.core.notes.utils.NoteUtils;
import com.thundersharp.cadmin.ui.activity.EditNoteActivity;
import com.thundersharp.cadmin.ui.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static com.thundersharp.cadmin.ui.activity.EditNoteActivity.NOTE_EXTRA_Key;


public class PNotes extends Fragment implements NoteEventListener {

    public static final String THEME_Key = "app_theme";
    public static final String APP_PREFERENCES = "notepad_settings";
    private static final String TAG = "MainActivity";
    ImageView notfound;
    private RecyclerView recyclerView;
    private ArrayList<Note> notes;
    private NotesAdapter adapter;
    private NotesDao dao;
    private MainActionModeCallback actionModeCallback;
    private int chackedCount = 0;
    private FloatingActionButton fab;
    private SharedPreferences settings;
    private int theme;
    // swipe to right or to left te delete
    private ItemTouchHelper swipeToDeleteHelper = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    // TODO: 28/09/2018 delete note when swipe

                    if (notes != null) {
                        // get swiped note
                        Note swipedNote = notes.get(viewHolder.getAdapterPosition());
                        if (swipedNote != null) {
                            swipeToDelete(swipedNote, viewHolder);

                            /*
                            Snackbar snackbar = Snackbar.make(getView(),"Deleted",Snackbar.LENGTH_LONG);
                            snackbar.setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //TODO undo deleted notes
                                }
                            });
                            snackbar.show();
                            */

                        }

                    }
                }
            });
    private ItemTouchHelper swipeToDeleteHelperright = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    // TODO: 28/09/2018 delete note when swipe

                    if (notes != null) {
                        // get swiped note
                        Note swipedNote = notes.get(viewHolder.getAdapterPosition());
                        if (swipedNote != null) {
                            swipeToshare(swipedNote, viewHolder);

                        }

                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_p_notes, container, false);
        MainActivity.container.setBackground(getResources().getDrawable(R.drawable.bg, getActivity().getTheme()));
        MainActivity.floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_playlist_add_24, getActivity().getTheme()));
        settings = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        theme = settings.getInt(THEME_Key, R.style.AppTheme);
        getActivity().setTheme(theme);
        notfound = root.findViewById(R.id.notfound);


        // init recyclerView
        recyclerView = root.findViewById(R.id.notes_list);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        // init fab Button
        //fab = (FloatingActionButton) findViewById(R.id.fab);
        MainActivity.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 13/05/2018  add new note
                onAddNewNote();
            }
        });

        dao = NotesDB.getInstance(getContext()).notesDao();

        return root;
    }

    private void loadNotes() {
        this.notes = new ArrayList<>();
        List<Note> list = dao.getNotes();
        this.notes.addAll(list);
        this.adapter = new NotesAdapter(getActivity(), notes);
        // set listener to adapter
        this.adapter.setListener(this);
        this.recyclerView.setAdapter(adapter);
        showEmptyView();
        // add swipe helper to recyclerView

        swipeToDeleteHelper.attachToRecyclerView(recyclerView);
        swipeToDeleteHelperright.attachToRecyclerView(recyclerView);
    }

    /**
     * when no notes show msg in main_layout
     */
    private void showEmptyView() {
        if (notes.size() == 0) {
            //Add empty view
            notfound.setVisibility(View.VISIBLE);
        } else {
            notfound.setVisibility(View.GONE);
        }
    }

    private void onAddNewNote() {
        startActivity(new Intent(getActivity(), EditNoteActivity.class));
    }

    @Override
    public void onResume() {

        loadNotes();
        super.onResume();

    }

    @Override
    public void onNoteClick(Note note) {
        // TODO: 22/07/2018  note clicked : edit note
        Intent edit = new Intent(getActivity(), EditNoteActivity.class);
        edit.putExtra(NOTE_EXTRA_Key, note.getId());
        startActivity(edit);

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onNoteLongClick(Note note) {
        // TODO: 22/07/2018 note long clicked : delete , share ..
        note.setChecked(true);
        chackedCount = 1;
        adapter.setMultiCheckMode(true);

        // set new listener to adapter intend off MainActivity listener that we have implement
        adapter.setListener(new NoteEventListener() {
            @Override
            public void onNoteClick(Note note) {
                note.setChecked(!note.isChecked()); // inverse selected
                if (note.isChecked())
                    chackedCount++;
                else chackedCount--;

                if (chackedCount > 1) {
                    actionModeCallback.changeShareItemVisible(false);
                } else actionModeCallback.changeShareItemVisible(true);

                if (chackedCount == 0) {
                    //  finish multi select mode wen checked count =0
                    actionModeCallback.getAction().finish();
                }

                actionModeCallback.setCount(chackedCount + "/" + notes.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNoteLongClick(Note note) {

            }
        });

        actionModeCallback = new MainActionModeCallback() {
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete_notes)
                    onDeleteMultiNotes();
                else if (menuItem.getItemId() == R.id.action_share_note)
                    onShareNote();

                actionMode.finish();
                return false;
            }

        };

        // start action mode
        getActivity().startActionMode(actionModeCallback);
        // hide fab button
        fab.setVisibility(View.GONE);
        actionModeCallback.setCount(chackedCount + "/" + notes.size());
    }

    /*@SuppressLint("RestrictedApi")
    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);

        adapter.setMultiCheckMode(false); // uncheck the notes
        adapter.setListener(this); // set back the old listener
        floatingActionButton.setVisibility(View.VISIBLE);
    }*/

    private void onShareNote() {
        // TODO: 22/07/2018  we need share just one Note not multi

        Note note = adapter.getCheckedNotes().get(0);
        // TODO: 22/07/2018 do your logic here to share note ; on social or something else
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String notetext = note.getNoteText() + "\n\n Create on : " +
                NoteUtils.dateFromLong(note.getNoteDate()) + "\n  By :" +
                getString(R.string.app_name);
        share.putExtra(Intent.EXTRA_TEXT, notetext);
        startActivity(share);


    }

    private void onDeleteMultiNotes() {
        // TODO: 22/07/2018 delete multi notes

        List<Note> chackedNotes = adapter.getCheckedNotes();
        if (chackedNotes.size() != 0) {
            for (Note note : chackedNotes) {
                dao.deleteNote(note);
            }
            // refresh Notes
            loadNotes();
            Toast.makeText(getActivity(), chackedNotes.size() + " Note(s) Delete successfully !", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(getActivity(), "No Note(s) selected", Toast.LENGTH_SHORT).show();

        adapter.setMultiCheckMode(false);
    }

    private void swipeToDelete(final Note swipedNote, final RecyclerView.ViewHolder viewHolder) {
        new AlertDialog.Builder(getActivity())
                .setMessage("Delete Note?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: 28/09/2018 delete note
                        dao.deleteNote(swipedNote);
                        notes.remove(swipedNote);
                        adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        showEmptyView();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: 28/09/2018  Undo swipe and restore swipedNote
                        recyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());


                    }
                })
                .setCancelable(false)
                .create().show();

    }

    private void swipeToshare(final Note swipedNote, final RecyclerView.ViewHolder viewHolder) {
        new AlertDialog.Builder(getActivity())
                .setMessage("Share note?")
                .setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        recyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                })
                .setCancelable(false)
                .create().show();

    }
}