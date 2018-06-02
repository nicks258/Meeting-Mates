package com.npluslabs.meetingmates.loginconcept;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.npluslabs.meetingmates.R;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import java.util.List;
import android.support.annotation.Nullable;
import android.annotation.TargetApi;
import android.widget.Toast;

import butterknife.BindViews;
import butterknife.ButterKnife;


public class SignUpFragment extends AuthFragment{

    @BindViews(value = {R.id.email_input_edit,
            R.id.password_input_edit,
            R.id.confirm_password_edit})
    protected List<TextInputEditText> views;
    TextInputEditText email,password;
    String emailString,passwordString;
    VerticalTextView signUpButton;
    ActionCodeSettings actionCodeSettings;
    private FirebaseAuth mAuth;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(view!=null){
            view.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.color_sign_up));
            caption.setText(getString(R.string.sign_up_label));
            Logger.addLogAdapter(new AndroidLogAdapter());

            for(TextInputEditText editText:views){
                if(editText.getId()==R.id.password_input_edit){
                    final TextInputLayout inputLayout= ButterKnife.findById(view,R.id.password_input);
                    final TextInputLayout confirmLayout=ButterKnife.findById(view,R.id.confirm_password);
                    Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                    inputLayout.setTypeface(boldTypeface);
                    confirmLayout.setTypeface(boldTypeface);
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
            mAuth = FirebaseAuth.getInstance();
            actionCodeSettings =
                    ActionCodeSettings.newBuilder()
                            // URL you want to redirect back to. The domain (www.example.com) for this
                            // URL must be whitelisted in the Firebase Console.
                            .setUrl("https://www.example.com/finishSignUp?cartId=1234")
                            // This must be true
                            .setHandleCodeInApp(true)
                            .setIOSBundleId("com.example.ios")
                            .setAndroidPackageName(
                                    "com.example.android",
                                    true, /* installIfNotAvailable */
                                    "1.0.0"    /* minimumVersion */)
                            .build();
            email= ButterKnife.findById(view,R.id.email_input_edit);
            password= ButterKnife.findById(view,R.id.password_input_edit);
//            email = getActivity().findViewById(R.id.email_input_edit);
//            password = getActivity().findViewById(R.id.password_input_edit);
            signUpButton = getActivity().findViewById(R.id.caption);
            caption .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emailString = email.getText().toString();
                    passwordString = password.getText().toString();
                    Logger.i("Button Clicked " + passwordString + emailString);
//                    if(emailString.length()!=0 && passwordString.length()!=0)
                    {
                        mAuth.createUserWithEmailAndPassword(emailString, passwordString)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d("SignUpFragment", "createUserWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Logger.i("user-> " + user.getDisplayName());
                                            FirebaseAuth auth = FirebaseAuth.getInstance();
                                            mAuth.sendSignInLinkToEmail(emailString, actionCodeSettings)
                                                    .addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            Logger.i("Email Sent");
                                                        }
                                                    });
                                            displayInformation();
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w("SignUpFragment", "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(getContext(), "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            Logger.i("user-> " + "Error");
                                        }

                                        // ...
                                    }
                                });
                    }
                }
            });
            caption.setVerticalText(true);
            foldStuff();
            caption.setTranslationX(getTextPadding());
        }
    }

    @Override
    public int authLayout() {
        return R.layout.sign_up_fragment;
    }

    @Override
    public void clearFocus() {
        for(View view:views) view.clearFocus();
    }

    @Override
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
        set.addListener(new Transition.TransitionListenerAdapter(){
            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionEnd(transition);
                caption.setTranslationX(getTextPadding());
                caption.setRotation(0);
                caption.setVerticalText(true);
                caption.requestLayout();

            }
        });
        TransitionManager.beginDelayedTransition(parent,set);
        foldStuff();
        caption.setTranslationX(-caption.getWidth()/8+getTextPadding());
    }

    private void foldStuff(){
        caption.setTextSize(TypedValue.COMPLEX_UNIT_PX,caption.getTextSize()/2f);
        caption.setTextColor(Color.WHITE);
        ConstraintLayout.LayoutParams params=getParams();
        params.rightToRight=ConstraintLayout.LayoutParams.UNSET;
        params.verticalBias=0.5f;
        caption.setLayoutParams(params);
    }

    private float getTextPadding(){
        return getResources().getDimension(R.dimen.folded_label_padding)/2.1f;
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
}
