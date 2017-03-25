package com.example.lb.lovebox.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lb.lovebox.R;

import java.text.DateFormat;
import java.util.List;

/**
 * Created by terence on 3/19/17.
 */

public class NotesAdapter extends BaseAdapter {

    /** Wrapper para notas. Util para cambiar el fondo de los item seleccionados. */
    public static class NoteViewWrapper {

        private final Note note;
        private boolean isSelected;

        /**
         * Contruye un nuevo NoteWrapper con la nota dada.
         *
         * @param note una nota.
         */
        public NoteViewWrapper(Note note) {
            this.note = note;
        }

        public Note getNote() {
            return note;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }
    }

    private static final DateFormat DATETIME_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

    private final List<NoteViewWrapper> data;

    /**
     * Constructor.
     *
     * @param data la lista de notas a usar como fuente de datos para este adaptador.
     */
    public NotesAdapter(List<NoteViewWrapper> data) {
        this.data = data;
    }

    /** @return cuantos datos hay en la lista de notas. */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * @param position la posición de la nota que se quiere
     * @return la nota en la posición dada.
     */
    @Override
    public NoteViewWrapper getItem(int position) {
        return data.get(position);
    }

    /**
     * @param position una posición
     * @return la misma posición dada
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) { // inflar componente visual
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag(); // ya existe, solo es reciclarlo
        // Inicializa la vista con los datos de la nota
        NoteViewWrapper noteViewWrapper = data.get(position);
        holder.noteIdText.setText(String.valueOf(noteViewWrapper.note.getId()));
        holder.noteTitleText.setText(noteViewWrapper.note.getTitle());
        // Corta la cadena a 80 caracteres y le agrega "..."
        holder.noteContentText.setText(noteViewWrapper.note.getContent().length() >= 80 ? noteViewWrapper.note.getContent().substring(0, 80).concat("...") : noteViewWrapper.note.getContent());
        holder.noteDateText.setText(DATETIME_FORMAT.format(noteViewWrapper.note.getUpdatedAt()));
        // Cambia el color del fondo si es seleccionado
        if (noteViewWrapper.isSelected) holder.parent.setBackgroundColor(parent.getContext().getResources().getColor(R.color.iron));
            // Sino lo regresa a transparente
        else holder.parent.setBackgroundColor(parent.getContext().getResources().getColor(android.R.color.transparent));
        return convertView;
    }

    /** Almacena componentes visuales para acceso rápido sin necesidad de buscarlos muy seguido.*/
    private static class ViewHolder {

        private TextView noteIdText;
        private TextView noteTitleText;
        private TextView noteContentText;
        private TextView noteDateText;

        private View parent;

        /**
         * Constructor. Encuentra todas los componentes visuales en el componente padre dado.
         *
         * @param parent un componente visual.
         */
        private ViewHolder(View parent) {
            this.parent = parent;
            noteIdText = (TextView) parent.findViewById(R.id.note_id);
            noteTitleText = (TextView) parent.findViewById(R.id.note_title);
            noteContentText = (TextView) parent.findViewById(R.id.note_content);
            noteDateText = (TextView) parent.findViewById(R.id.note_date);
        }
    }
}
