package com.example.mynotesapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mynotesapp.model.Note;
import com.example.mynotesapp.utils.RecyclerTouchListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotesFragment extends Fragment  {


    RecyclerView recyclerView;
    private NotesAdapter mAdapter;
    private DatabaseHelper db;
    LinearLayout fragm;


    private List<Note> notesList = new ArrayList<>();
    private FloatingActionButton fab;
    String s1;


    public NotesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_notes, container, false);

        recyclerView=view.findViewById(R.id.recycler_view);
           fragm   = view.findViewById(R.id.notefragid);
        fab= (FloatingActionButton) view.findViewById(R.id.fab);


        db = new DatabaseHelper(getContext());

        notesList.addAll(db.getAllNotes());
        System.out.println(notesList);
        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);

       s1= sh.getString("name", "");
      mAdapter = new NotesAdapter(getContext(), notesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);


        for (int i=0;i<notesList.size();i++){
            if (!notesList.get(i).getNote().contains(s1)){
                removeother(i);
            }
        }
        recyclerView.setAdapter(mAdapter);




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                recyclerView.setVisibility(View.INVISIBLE);
                fragm.setVisibility(View.INVISIBLE);
                Fragment fragment = new EditFragment();

                getFragmentManager().beginTransaction().replace(R.id.framelayout,fragment).commit();

                //  showNoteDialog(false, null, -1);
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(),
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                if(notesList.get(position).getNote().contains(s1)){
                    showActionsDialog(position);
                }

            }

            @Override
            public void onLongClick(View view, int position) {
                if(notesList.get(position).getNote().contains(s1)){
                    showActionsDialog(position);
                }
            }
        }));


        return view;
    }

    public void removeother(int position){
        notesList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }
    public void deletenotes(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Alert");
        builder.setMessage("Are you sure do you want to delete the note");
        builder
                .setPositiveButton(
                        "YES",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {

                                deleteNote(position);
                            }
                        });
        builder
                .setNegativeButton(
                        "NO",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {




                            }
                        });
        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }









    private void showActionsDialog(final int position) {

        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("View");
        builder.setMessage(notesList.get(position).getNote().replaceAll(s1,""));

        builder
                .setPositiveButton(
                        "EDIT",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {
                                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                Bundle bundle = new Bundle();

                                System.out.println("this is main " );

                                recyclerView.setVisibility(View.INVISIBLE);
                                fragm.setVisibility(View.INVISIBLE);
                                String temp = notesList.get(position).getNote()+"";
                                bundle.putBoolean("one", true);
                                bundle.putString("two",temp);
                                bundle.putInt("three",position);
                                EditFragment EditFrag = new EditFragment();
                                EditFrag.setArguments(bundle);
                                System.out.println(bundle);
                                //getFragmentManager().beginTransaction().add(R.id.framelayout, EditFrag).commit();
                                fragmentTransaction.replace(R.id.framelayout,EditFrag).commit();
                                //showNoteDialog(true, notesList.get(position), position);

                            }
                        });
        builder
                .setNegativeButton(
                        "DELETE",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {

                                // If user click no
                                // then dialog box is canceled.
                                deletenotes(position);
                            }
                        });

        builder
                .setNeutralButton(
                        "SHARE",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {

                                Intent intentShare = new Intent(Intent.ACTION_SEND);
                                intentShare.setType("text/plain");
                                intentShare.putExtra(Intent.EXTRA_SUBJECT,"My Subject Here ... ");
                                intentShare.putExtra(Intent.EXTRA_TEXT,notesList.get(position).getNote().replaceAll(s1,""));

                                startActivity(Intent.createChooser(intentShare, "Shared the text ..."));
                            }
                        });


        builder.show();
    }
    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteNote(notesList.get(position));

        // removing the note from the list
        notesList.remove(position);
        mAdapter.notifyItemRemoved(position);
        Toast.makeText(getContext(),"Notes Deleted Successfully",Toast.LENGTH_LONG).show();



    }

}