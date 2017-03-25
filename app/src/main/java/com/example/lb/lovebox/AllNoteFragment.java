package com.example.lb.lovebox;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.lb.lovebox.Adapter.Note;
import com.example.lb.lovebox.Adapter.NotesAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AllNoteFragment extends Fragment {
    private Note note;
    private ArrayList<Integer> selectedPositions;
    private ArrayList<NotesAdapter.NoteViewWrapper> notesData;
    private NotesAdapter listAdapter;
    private ActionMode.Callback actionModeCallback;
    private ActionMode actionMode;
    private TextView emptyListTextView;
    private ListView listView;
    private FloatingActionButton addNoteButton;
    public void setNote(Note n){
        note = n;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_note,container,false);
        listView = (ListView) v.findViewById(android.R.id.list);
        emptyListTextView = (TextView) v.findViewById(android.R.id.empty);
        addNoteButton = (FloatingActionButton) v.findViewById(R.id.add_note_btn);
        addNoteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), EditActivity.class);
                startActivity(intent);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                startActivityForResult(EditNoteActivity.buildIntent(MainActivity.this), NEW_NOTE_RESULT_CODE);
            }
        });
        selectedPositions = new ArrayList<>();
        setupNotesAdapter();
        setupActionModeCallback();
        setListOnItemClickListenersWhenNoActionMode();
        updateView();
        return v;
    }
    private void setupActionModeCallback() {
        actionModeCallback = new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                setListOnItemClickListenersWhenActionMode();
                mode.getMenuInflater().inflate(R.menu.context_note, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        if (!selectedPositions.isEmpty()) {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage(getString(R.string.delete_notes_alert, selectedPositions.size()))
                                    .setNegativeButton(R.string.no, null)
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteNotes(selectedPositions);
                                            mode.finish();
                                        }
                                    })
                                    .show();
                        } else mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            /** {@inheritDoc} */
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                setListOnItemClickListenersWhenNoActionMode();
                resetSelectedListItems();
            }
        };
    }

    private void deleteNotes(ArrayList<Integer> selectedPositions) {
        ArrayList<NotesAdapter.NoteViewWrapper> toRemoveList = new ArrayList<>(selectedPositions.size());
        for (int position : selectedPositions) {
            NotesAdapter.NoteViewWrapper noteViewWrapper = notesData.get(position);
            toRemoveList.add(noteViewWrapper);
        }
        for (NotesAdapter.NoteViewWrapper noteToRemove : toRemoveList) notesData.remove(noteToRemove);
        updateView();
        listAdapter.notifyDataSetChanged();
    }

    private void updateView() {
        if (notesData.isEmpty()) {
            listView.setVisibility(View.GONE);
            emptyListTextView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            emptyListTextView.setVisibility(View.GONE);
        }
    }

    public void setupNotesAdapter(){
        notesData = new ArrayList<>();
        AVQuery<AVObject> avQuery = new AVQuery<>("Notes");
        avQuery.orderByDescending("createdAt");
        avQuery.whereEqualTo("user", AVUser.getCurrentUser().getEmail());
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    for (AVObject i : list) {
                        Log.e("ERROR",(String)i.get("content"));
                        Note note = new Note();
                        int id = (Integer) i.get("id");
                        note.setId((long)id);
                        note.setTitle((String)i.get("title"));
                        note.setContent((String)i.get("content"));
                        note.setCreatedAt((Date) i.get("createdAt"));
                        note.setUpdatedAt((Date) i.get("updatedAt"));
                        NotesAdapter.NoteViewWrapper noteViewWrapper = new NotesAdapter.NoteViewWrapper(note);
                        notesData.add(noteViewWrapper);
                    }
                    Util.notes = notesData;
                    listAdapter = new NotesAdapter(notesData);
                    listView.setAdapter(listAdapter);
                    listAdapter.notifyDataSetChanged();
                    updateView();
//
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void resetSelectedListItems() {
        for (NotesAdapter.NoteViewWrapper noteViewWrapper : notesData) noteViewWrapper.setSelected(false);
        selectedPositions.clear();
        listAdapter.notifyDataSetChanged();
    }


    private void setListOnItemClickListenersWhenNoActionMode() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity().getApplicationContext(), "lala", Toast.LENGTH_SHORT).show();
//                startActivityForResult(ViewNoteActivity.buildIntent(MainActivity.this, notesData.get(position).getNote()), 8);
                ViewFragment fragment = new ViewFragment();
                fragment.setNote(notesData.get(position).getNote());
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.frame,fragment);
                fragmentTransaction.commit();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                notesData.get(position).setSelected(true);
                listAdapter.notifyDataSetChanged();
                selectedPositions.add(position);
                ActionBarActivity activity=(ActionBarActivity) getActivity();
                actionMode = activity.startSupportActionMode(actionModeCallback);
                actionMode.setTitle(String.valueOf(selectedPositions.size()));
                return true;
            }
        });
    }


    private void setListOnItemClickListenersWhenActionMode() {
        listView.setOnItemLongClickListener(null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedPositions.contains(position)) {
                    selectedPositions.remove((Object)position); // no quiero el índice sino el objeto
                    if (selectedPositions.isEmpty()) actionMode.finish();
                    else {
                        actionMode.setTitle(String.valueOf(selectedPositions.size()));
                        notesData.get(position).setSelected(false);
                        listAdapter.notifyDataSetChanged();
                    }
                } else {
                    notesData.get(position).setSelected(true);
                    listAdapter.notifyDataSetChanged();
                    selectedPositions.add(position);
                    actionMode.setTitle(String.valueOf(selectedPositions.size()));
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
