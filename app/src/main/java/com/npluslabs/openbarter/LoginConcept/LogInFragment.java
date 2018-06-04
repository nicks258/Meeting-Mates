package com.npluslabs.openbarter.LoginConcept;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;

import com.facebook.AccessTokenTracker;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.npluslabs.openbarter.R;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.annotation.TargetApi;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;

public class LogInFragment extends AuthFragment{
    String emailString,passwordString;
    TextInputEditText email,password;
    private String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String EMAIL = "email";
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    ImageView fbLogin,googleLogin;
    VerticalTextView loginButton;
    private static final int RC_SIGN_IN = 9001;
    @BindViews(value = {R.id.email_input_edit,R.id.password_input_edit})
    protected List<TextInputEditText> views;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(view!=null){
            caption.setText(getString(R.string.log_in_label));
            view.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.color_log_in));
            for(TextInputEditText editText:views){
                if(editText.getId()==R.id.password_input_edit){
                    final TextInputLayout inputLayout=ButterKnife.findById(view,R.id.password_input);
                    Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                    inputLayout.setTypeface(boldTypeface);
                    editText.addTextChangedListener(new TextWatcherAdapter(){
                        @Override
                        public void afterTextChanged(Editable editable) {
                            inputLayout.setPasswordVisibilityToggleEnabled(editable.length()>0);
                        }
                    });
                }
                editText.setOnFocusChangeListener((temp,hasFocus)->{
                    if(!hasFocus){
                        boolean isEnabled=editText.getText().length()>0;
                        editText.setSelected(isEnabled);
                    }
                });
            }
            Logger.addLogAdapter(new AndroidLogAdapter());
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

            // [START initialize_auth]
            mAuth = FirebaseAuth.getInstance();

            loginButton = getActivity().findViewById(R.id.caption);
            email = getActivity().findViewById(R.id.email_input_edit);
            password = getActivity().findViewById(R.id.password_input_edit);
            ActionCodeSettings actionCodeSettings =
                    ActionCodeSettings.newBuilder()
                            // URL you want to redirect back to. The domain (www.example.com) for this
                            // URL must be whitelisted in the Firebase Console.
                            .setUrl("https://www.example.com/finishSignUp?cartId=1234")
                            // This must be true
                            .setHandleCodeInApp(true)
                            .setAndroidPackageName(
                                    "com.npluslabs.meetingmates",
                                    true, /* installIfNotAvailable */
                                    "1.0.0"    /* minimumVersion */)
                            .build();
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emailString = email.getText().toString();
                    passwordString = password.getText().toString();
                    if (emailString.length() != 0 && passwordString.length() != 0) {
                        mAuth.signInWithEmailAndPassword(emailString, passwordString)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            sendEmail();
                                            mAuth.sendSignInLinkToEmail(emailString, actionCodeSettings)
                                                    .addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d(TAG, "Email sent.");
                                                            }
                                                        }
                                                    });
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "signInWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Logger.i("user-> " + user.getEmail());
                                            displayInformation();
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                                            Toast.makeText(getActivity(), "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            Logger.i("Error in login");
                                        }

                                        // ...
                                    }
                                });
                    }
                    else {
                        Toast.makeText(getActivity(),"Enter Email/Password",Toast.LENGTH_LONG).show();
                    }
                }

            });
            mAuth = FirebaseAuth.getInstance();
        }
    }

    private void sendEmail() {

    }

    @Override
    public int authLayout() {
        return R.layout.login_fragment;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void fold() {
        lock=false;
        Rotate transition = new Rotate();
        transition.setEndAngle(-90f);
        transition.addTarget(caption);
        TransitionSet set=new TransitionSet();
        set.setDuration(getResources().getInteger(R.integer.duration));
        ChangeBounds changeBounds=new ChangeBounds();
        set.addTransition(changeBounds);
        set.addTransition(transition);
        TextSizeTransition sizeTransition=new TextSizeTransition();
        sizeTransition.addTarget(caption);
        set.addTransition(sizeTransition);
        set.setOrdering(TransitionSet.ORDERING_TOGETHER);
        final float padding=getResources().getDimension(R.dimen.folded_label_padding)/2;
        set.addListener(new Transition.TransitionListenerAdapter(){
            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionEnd(transition);
                caption.setTranslationX(-padding);
                caption.setRotation(0);
                caption.setVerticalText(true);
                caption.requestLayout();

            }
        });
        TransitionManager.beginDelayedTransition(parent,set);
        caption.setTextSize(TypedValue.COMPLEX_UNIT_PX,caption.getTextSize()/2);
        caption.setTextColor(Color.WHITE);
        ConstraintLayout.LayoutParams params=getParams();
        params.leftToLeft=ConstraintLayout.LayoutParams.UNSET;
        params.verticalBias=0.5f;
        caption.setLayoutParams(params);
        caption.setTranslationX(caption.getWidth()/8-padding);
    }

    private void displayMessage(Profile profile){
        if(profile != null){
            Logger.i("Profile Name->" + profile.getName());
            Logger.i("Info " + profile.getProfilePictureUri(400,400));
//            textView.setText(profile.getName());
        }
    }
    private void displayInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
            Logger.i("name-> " + name + " email-> " + email + " photo-> "+ photoUrl +" verify -> " + emailVerified);
        }
    }

//    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
//        // [START_EXCLUDE silent]
//        Logger.i("Loading");
//        // [END_EXCLUDE]
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            Logger.i("User Info-> " + user.getEmail());
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
////                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
////                            updateUI(null);
//                            Logger.i("Authentication Failed.");
//                        }
//
//                        // [START_EXCLUDE]
//                        Logger.i("Loading Done");
//                        // [END_EXCLUDE]
//                    }
//                });
//    }

    @Override
    public void clearFocus() {
        for(View view:views) view.clearFocus();
    }

}
