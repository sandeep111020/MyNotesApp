package com.example.mynotesapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynotesapp.model.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private Context context;
    private List<Note> notesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView note;
        public TextView dot;
        public TextView timestamp;
        CircleImageView iconimage;
        LinearLayout lin;
        RelativeLayout linlayt;

        public MyViewHolder(View view) {
            super(view);
            note = view.findViewById(R.id.note);
            timestamp = view.findViewById(R.id.timestamp);
            lin=view.findViewById(R.id.linlay);
            linlayt= view.findViewById(R.id.linlayt);
            iconimage=view.findViewById(R.id.iconsimge);
        }
    }


    public NotesAdapter(Context context, List<Note> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SharedPreferences sh = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);

        String s1= sh.getString("name", "");


        Note note = notesList.get(position);
        if(note.getNote().contains(s1)){
            holder.note.setText(note.getNote().substring(0,3)+"...");
            int colourcode=getRandomColor();

           // holder.iconimage.setText(note.getNote().substring(0,1));
            holder.lin.setBackgroundColor(holder.itemView.getResources().getColor(colourcode,null));



            holder.timestamp.setText("Created on:  "+formatDate(note.getTimestamp()));
        }else {

            holder.itemView.setVisibility(View.GONE);


        }


    }

    private int getRandomColor()
    {
        List<Integer> colorcode=new ArrayList<>();
        colorcode.add(R.color.gray);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.lightgreen);
        colorcode.add(R.color.skyblue);
        colorcode.add(R.color.color1);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);

        colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);
        colorcode.add(R.color.green);

        Random random=new Random();
        int number=random.nextInt(colorcode.size());
        return colorcode.get(number);



    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
}
