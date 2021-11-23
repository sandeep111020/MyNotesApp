package com.example.mynotesapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.mynotesapp.model.Note;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    public static NotesAdapter mAdapter;
    private List<Note> notesList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
   // private RecyclerView recyclerView;
    private TextView username;

    private DatabaseHelper db;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
    FrameLayout fr1,fr2,fr3;
    CircleImageView img;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fr1=findViewById(R.id.framelayout);
        fr2=findViewById(R.id.fragment2);
        fr3=findViewById(R.id.recycler);
        img=findViewById(R.id.imgview);


        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.fragment2);

        username=findViewById(R.id.username);
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignInOptions gso = new GoogleSignInOptions.
                        Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                        build();

                GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(MainActivity.this,gso);
                googleSignInClient.signOut();
                Intent i = new Intent(MainActivity.this,MainActivity.class);
                startActivity(i);
            }
        });


        if (fragment == null) {
            fragment = new GPlusFragment();
            fm.beginTransaction()
                    .add(R.id.fragment2, fragment)
                    .commit();
        }

        gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,1,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        coordinatorLayout = findViewById(R.id.coordinator_layout);
       // recyclerView = findViewById(R.id.recycler_view);



        db = new DatabaseHelper(this);

        notesList.addAll(db.getAllNotes());







    }


    private void updateNote(String note, int position) {
        Note n = notesList.get(position);
        // updating note text
        n.setNote(note);

        // updating note in db
        db.updateNote(n);

        // refreshing the list
        notesList.set(position, n);
        mAdapter.notifyItemChanged(position);


    }


    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteNote(notesList.get(position));

        // removing the note from the list
        notesList.remove(position);
        mAdapter.notifyItemRemoved(position);


    }







    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr= Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(opr.isDone()){
            GoogleSignInResult result=opr.get();
            handleSignInResult(result);
        }else{
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account=result.getSignInAccount();
            username.setText("Welcome "+account.getDisplayName());
            // Storing data into SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);

// Creating an Editor object to edit(write to the file)
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("name", account.getId());
            myEdit.commit();
            fr2.setVisibility(View.INVISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recycler,new NotesFragment()).commit();

            //userEmail.setText(account.getEmail());
           // userId.setText(account.getId());
            try{
                Glide.with(this).load(account.getPhotoUrl()).into(img);

            }catch (NullPointerException e){
                Toast.makeText(getApplicationContext(),"image not found",Toast.LENGTH_LONG).show();
            }

        }else{
            /*gotoMainActivity();*/
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}