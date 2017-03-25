package com.example.lb.lovebox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lb.lovebox.Adapter.Note;

import java.text.SimpleDateFormat;


public class ViewFragment extends Fragment {
    private Note note;
    public void setNote(Note n){
        note = n;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view,container,false);
        TextView title = (TextView) v.findViewById(R.id.note_title);
        TextView content = (TextView) v.findViewById(R.id.note_content);
        TextView createdate = (TextView) v.findViewById(R.id.note_created_at_date);
        TextView updatedate = (TextView) v.findViewById(R.id.note_updated_at_date);

        if(note!=null) {
            title.setText(note.getTitle());
            content.setText(note.getContent());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            createdate.setText(sdf.format(note.getCreatedAt()));
            updatedate.setText(sdf.format(note.getUpdatedAt()));
        }
        return v;
    }

}
