package com.example.mynotesapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import static com.example.mynotesapp.MainActivity.mAdapter;

import com.example.mynotesapp.model.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class EditFragment extends Fragment {

   View view;
   Button savebtn;
    Boolean paone=false;
    String patwo;
    int pathree;
    private List<Note> notesList = new ArrayList<>();
    private DatabaseHelper db;
    EditText inputNote;
    Button speak;
    String s1;
    private TextToSpeech textToSpeech;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_edit, container, false);



        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);

         s1= sh.getString("name", "");


        TextView tv = view.findViewById(R.id.dialog_title1);
        inputNote = view.findViewById(R.id.note1);
        savebtn= view.findViewById(R.id.buttonsave);
        speak=view.findViewById(R.id.buttonspeak);
        db = new DatabaseHelper(getActivity());

        notesList.addAll(db.getAllNotes());
        System.out.println(notesList);
        Bundle bundle = this.getArguments();
        System.out.println(bundle);
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");


        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                if(i!=TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = inputNote.getText().toString();
                if (text!=null && text.length()>0) {

                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH,null);

                }else{
                    Toast.makeText(getContext(),"Please enter text",Toast.LENGTH_LONG).show();

                }
            }
        });
        if(bundle!=null){

            paone= (getArguments().getBoolean("one"));
             patwo = (getArguments().getString("two"));
             pathree = (getArguments().getInt("three"));
             savebtn.setText("UPDATE");
            patwo=patwo.replaceAll(s1,"");
        }

        System.out.println("note");
        tv.setText("Write your notes");
        inputNote.setText(patwo);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(paone==true){
                    showNoteDialog(paone,patwo,pathree);

                }else{
                    showNoteDialog(false,null,-1);

                }

            }
        });
        return view;
    }
    private void createNote(String note) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(note);

        // get the newly inserted note from db
        Note n = db.getNote(id);
        System.out.println(note);
        System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");


        if (n != null) {
            // adding new note to array list at 0 position
            notesList.add(0, n);

            // refreshing the list

        }
    }


    private void updateNote(String note, int position) {
        Note n = notesList.get(position);
        // updating note text
        n.setNote(note);
        System.out.println("jjjjjjjjjjjj");
        // updating note in db
        db.updateNote(n);

        // refreshing the list
        notesList.set(position, n);


    }


    public void showNoteDialog(final boolean shouldUpdate, final String note, final int position) {

        if (shouldUpdate ==true && note != null) {


            if(inputNote.getText().toString().isEmpty()){
                Toast.makeText(getContext(),"Please enter text",Toast.LENGTH_LONG).show();
            }else{

                updateNote(inputNote.getText().toString()+""+s1, position);
                Toast.makeText(getContext(),"Notes Updated Successfully",Toast.LENGTH_LONG).show();

                Intent i = new Intent(getContext(),MainActivity.class);
                startActivity(i);

            }

        } else {
            // create new note

            if(inputNote.getText().toString().isEmpty()){
                Toast.makeText(getContext(),"Please enter text",Toast.LENGTH_LONG).show();
            }else{
                createNote(inputNote.getText().toString()+""+s1);
                Toast.makeText(getContext(),"Notes Created Successfully",Toast.LENGTH_LONG).show();

                Intent i = new Intent(getContext(),MainActivity.class);
                startActivity(i);

            }

        }




    }


}