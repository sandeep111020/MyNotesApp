package com.example.mynotesapp;

import android.content.Context;
import android.content.DialogInterface;
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.mynotesapp.model.Note;
import com.example.mynotesapp.utils.RecyclerTouchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment {


    RecyclerView recyclerView;
    private NotesAdapter mAdapter;
    private DatabaseHelper db;
    LinearLayout fragm;
    private List<Note> notesList = new ArrayList<>();
    private FloatingActionButton fab;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    String s1;

    public NotesFragment() {
        // Required empty public constructor
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
       // recyclerView.setHasFixedSize(true);
        /*recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //initData();

        recyclerView.setAdapter(new ItemAdapter(initData(),getContext()));*/

        mAdapter = new NotesAdapter(getContext(), notesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        //staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

       // mrecyclerview.setLayoutManager(staggeredGridLayoutManager);

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
                                deleteNote(position);
                            }
                        });
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {



                } else {

                }
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

       // toggleEmptyNotes();
    }

}