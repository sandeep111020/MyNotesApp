package com.example.mynotesapp;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class GPlusFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    public static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient googleApiClient;
    private boolean mIntentInProgress;


    private boolean mSignInClicked;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_g_plus, container, false);
        v.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignInClicked = true;
                googleApiClient.connect();
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent,RC_SIGN_IN);
            }

        });
        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient=new GoogleApiClient.Builder((MainActivity)getActivity())
                .enableAutoManage(getActivity(),this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Login");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        Toast.makeText(getActivity(), "User is connected!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mIntentInProgress) {
            if (mSignInClicked && connectionResult.hasResolution()) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                try {
                    connectionResult.startResolutionForResult(((MainActivity)getActivity()), RC_SIGN_IN);
                    mIntentInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mIntentInProgress = false;
                    googleApiClient.connect();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            if (resultCode != MainActivity.RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!googleApiClient.isConnected()) {
                googleApiClient.reconnect();
            }
        }
    }
    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
           // gotoProfile();
            Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getContext(),MainActivity.class);
            startActivity(i);
        }else{
           // Toast.makeText(getApplicationContext(),"Sign in cancel",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button && !googleApiClient.isConnecting()) {
            mSignInClicked = true;
            googleApiClient.connect();
        }
    }
}