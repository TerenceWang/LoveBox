package com.example.lb.lovebox;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.lb.lovebox.Adapter.Note;
import com.example.lb.lovebox.Adapter.NotesAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.InjectView;

import static com.example.lb.lovebox.Util.user;

/**
 * Created by terence on 3/18/17.
 */

public class MainActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private ArrayList<Integer> selectedPositions;
    private ArrayList<NotesAdapter.NoteViewWrapper> notesData;
    private NotesAdapter listAdapter;
    private ActionMode.Callback actionModeCallback;
    private ActionMode actionMode;
    private TextView emptyListTextView;
    private ListView listView;
    private FloatingActionButton addNoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AVOSCloud.initialize(this, "UMCbxIR2qsFpHuL7A9I3iDeG-gzGzoHsz", "blEFXgbKNoGz324Ay6TMtYte");
        user = AVUser.getCurrentUser();
        if(user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
//        if(user == null){
//
//        }else{
//            user.logOut();
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//        }
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        ((TextView) header.findViewById(R.id.email)).setText(user.getEmail());
        ((TextView) header.findViewById(R.id.username)).setText(user.getUsername());
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.randomselect:
                        Toast.makeText(getApplicationContext(), "Random Selected", Toast.LENGTH_SHORT).show();
                        ViewFragment fragment = new ViewFragment();
                        Random random = new Random();
                        int pos = random.nextInt(Util.notes.size());
                        if(pos == Util.notes.size())
                            pos = pos - 1;
                        fragment.setNote(Util.notes.get(pos).getNote());
                        fragmentTransaction.replace(R.id.frame,fragment);
                        fragmentTransaction.commit();
//                        ContentFragment fragment = new ContentFragment();
//                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                        fragmentTransaction.replace(R.id.frame,fragment);
//                        fragmentTransaction.commit();
                        return true;
                    // For rest of the options we just show a toast on click

                    case R.id.allnote:

                        Toast.makeText(getApplicationContext(),"All Notes",Toast.LENGTH_SHORT).show();
                        AllNoteFragment allNoteFragment = new AllNoteFragment();
                        fragmentTransaction.replace(R.id.frame,allNoteFragment);
                        fragmentTransaction.commit();

                        return true;

                    case R.id.logout:
                        user.logOut();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                        return true;
//                    case R.id.sent_mail:
//                        Toast.makeText(getApplicationContext(),"Send Selected",Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.drafts:
//                        Toast.makeText(getApplicationContext(),"Drafts Selected",Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.allmail:
//                        Toast.makeText(getApplicationContext(),"All Mail Selected",Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.trash:
//                        Toast.makeText(getApplicationContext(),"Trash Selected",Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.spam:
//                        Toast.makeText(getApplicationContext(),"Spam Selected",Toast.LENGTH_SHORT).show();
//                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
//        addNoteButton = (FloatingActionButton) findViewById(R.id.add_note_btn);
//        addNoteButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
////                startActivityForResult(EditNoteActivity.buildIntent(MainActivity.this), NEW_NOTE_RESULT_CODE);
//            }
//        });
//        listView = (ListView) findViewById(android.R.id.list);
//        emptyListTextView = (TextView) findViewById(android.R.id.empty);
        actionBarDrawerToggle.syncState();
        AllNoteFragment fragment = new AllNoteFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame,fragment);
        fragmentTransaction.commit();
//        selectedPositions = new ArrayList<>();
//        setupNotesAdapter();
//        setupActionModeCallback();
//        setListOnItemClickListenersWhenNoActionMode();
//        updateView();
        //        AVUser.logOut();
    }
//    private void setupActionModeCallback() {
//        actionModeCallback = new ActionMode.Callback() {
//
//            @Override
//            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                setListOnItemClickListenersWhenActionMode();
//                mode.getMenuInflater().inflate(R.menu.context_note, menu);
//                return true;
//            }
//
//            @Override
//            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                return false;
//            }
//
//            @Override
//            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.action_delete:
//                        if (!selectedPositions.isEmpty()) {
//                            new AlertDialog.Builder(MainActivity.this)
//                                    .setMessage(getString(R.string.delete_notes_alert, selectedPositions.size()))
//                                    .setNegativeButton(R.string.no, null)
//                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            deleteNotes(selectedPositions);
//                                            mode.finish();
//                                        }
//                                    })
//                                    .show();
//                        } else mode.finish();
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//
//            /** {@inheritDoc} */
//            @Override
//            public void onDestroyActionMode(ActionMode mode) {
//                setListOnItemClickListenersWhenNoActionMode();
//                resetSelectedListItems();
//            }
//        };
//    }

//    private void deleteNotes(ArrayList<Integer> selectedPositions) {
//        ArrayList<NotesAdapter.NoteViewWrapper> toRemoveList = new ArrayList<>(selectedPositions.size());
//        for (int position : selectedPositions) {
//            NotesAdapter.NoteViewWrapper noteViewWrapper = notesData.get(position);
//            toRemoveList.add(noteViewWrapper);
//        }
//        for (NotesAdapter.NoteViewWrapper noteToRemove : toRemoveList) notesData.remove(noteToRemove);
//        updateView();
//        listAdapter.notifyDataSetChanged();
//    }

//    private void updateView() {
//        if (notesData.isEmpty()) {
//            listView.setVisibility(View.GONE);
//            emptyListTextView.setVisibility(View.VISIBLE);
//        } else {
//            listView.setVisibility(View.VISIBLE);
//            emptyListTextView.setVisibility(View.GONE);
//        }
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
//    public void setupNotesAdapter(){
//        notesData = new ArrayList<>();
//        AVQuery<AVObject> avQuery = new AVQuery<>("Notes");
//        avQuery.orderByDescending("createdAt");
//        avQuery.whereEqualTo("user", AVUser.getCurrentUser().getEmail());
//        avQuery.findInBackground(new FindCallback<AVObject>() {
//            @Override
//            public void done(List<AVObject> list, AVException e) {
//                if (e == null) {
//                    for (AVObject i : list) {
//                        Log.e("ERROR",(String)i.get("content"));
//                        Note note = new Note();
//                        int id = (Integer) i.get("id");
//                        note.setId((long)id);
//                        note.setTitle((String)i.get("title"));
//                        note.setContent((String)i.get("content"));
//                        note.setCreatedAt((Date) i.get("createdAt"));
//                        note.setUpdatedAt((Date) i.get("updatedAt"));
//                        NotesAdapter.NoteViewWrapper noteViewWrapper = new NotesAdapter.NoteViewWrapper(note);
//                        notesData.add(noteViewWrapper);
//                    }
//                    listAdapter = new NotesAdapter(notesData);
//                    listView.setAdapter(listAdapter);
//                    listAdapter.notifyDataSetChanged();
//                    updateView();
////
//                } else {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

//    private void resetSelectedListItems() {
//        for (NotesAdapter.NoteViewWrapper noteViewWrapper : notesData) noteViewWrapper.setSelected(false);
//        selectedPositions.clear();
//        listAdapter.notifyDataSetChanged();
//    }


//    private void setListOnItemClickListenersWhenNoActionMode() {
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(), "lala", Toast.LENGTH_SHORT).show();
////                startActivityForResult(ViewNoteActivity.buildIntent(MainActivity.this, notesData.get(position).getNote()), 8);
//                ViewFragment fragment = new ViewFragment();
//                fragment.setNote(notesData.get(position).getNote());
//                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//
//                fragmentTransaction.replace(R.id.frame,fragment);
//                fragmentTransaction.commit();
//            }
//        });
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                notesData.get(position).setSelected(true);
//                listAdapter.notifyDataSetChanged();
//                selectedPositions.add(position);
//                actionMode = startSupportActionMode(actionModeCallback);
//                actionMode.setTitle(String.valueOf(selectedPositions.size()));
//                return true;
//            }
//        });
//    }


//    private void setListOnItemClickListenersWhenActionMode() {
//        listView.setOnItemLongClickListener(null);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (selectedPositions.contains(position)) {
//                    selectedPositions.remove((Object)position); // no quiero el índice sino el objeto
//                    if (selectedPositions.isEmpty()) actionMode.finish();
//                    else {
//                        actionMode.setTitle(String.valueOf(selectedPositions.size()));
//                        notesData.get(position).setSelected(false);
//                        listAdapter.notifyDataSetChanged();
//                    }
//                } else {
//                    notesData.get(position).setSelected(true);
//                    listAdapter.notifyDataSetChanged();
//                    selectedPositions.add(position);
//                    actionMode.setTitle(String.valueOf(selectedPositions.size()));
//                }
//            }
//        });
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
