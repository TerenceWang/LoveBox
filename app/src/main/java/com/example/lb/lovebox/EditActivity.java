package com.example.lb.lovebox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;

public class EditActivity extends AppCompatActivity {

    private EditText noteTitleText;
    private EditText noteContentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVOSCloud.initialize(this, "UMCbxIR2qsFpHuL7A9I3iDeG-gzGzoHsz", "blEFXgbKNoGz324Ay6TMtYte");

        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditActivity.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
        });
        noteTitleText = (EditText) findViewById(R.id.note_title);
        noteContentText = (EditText) findViewById(R.id.note_content);

//        note = (Note) getIntent().getSerializableExtra(EXTRA_NOTE); // Recuperar la nota del Intent
//        if (note != null) { // Editar nota existente
//            noteTitleText.setText(note.getTitle());
//            noteContentText.setText(note.getContent());
//        } else { // Nueva nota
//            note = new Note();
//            note.setCreatedAt(new Date());
//        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(EditActivity.this, MainActivity.class));
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_note, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                if(noteTitleText.getText()==null)
                    Toast.makeText(EditActivity.this,"Title could not be empty",Toast.LENGTH_SHORT).show();
                else{
                    AVObject product = new AVObject("Notes");
                    product.put("title", noteTitleText.getText().toString());
                    product.put("content", noteContentText.getText().toString());
                    product.put("user", AVUser.getCurrentUser().getEmail().toString());
                    final ProgressDialog progressDialog = new ProgressDialog(EditActivity.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Saving...");
                    progressDialog.show();
                    product.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                progressDialog.dismiss();
                                startActivity(new Intent(EditActivity.this, MainActivity.class));
                                finish();
                                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(EditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}
