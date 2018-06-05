package com.npluslabs.openbarter.LoginConcept;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.DeepLinkHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.errors.LIDeepLinkError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.listeners.DeepLinkListener;
import com.linkedin.platform.utils.Scope;
import com.npluslabs.openbarter.MainActivity;
import com.npluslabs.openbarter.ProfileActivity;
import com.npluslabs.openbarter.R;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import com.facebook.appevents.AppEventsLogger;

import java.util.Arrays;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.BindViews;
import android.support.annotation.ColorRes;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private static final String EMAIL = "email";

    //    private static final String topCardUrl = "https://" + host + "/v1/people/~:(first-name,last-name,email-address,formatted-name,phone-numbers,public-profile-url,picture-url,picture-urls::(original))";
    private static final int RC_SIGN_IN = 9001;
    //    VerticalTextView loginButton;
    @BindViews(value = {R.id.logo, R.id.facebook_button, R.id.second, R.id.last})
    protected List<ImageView> sharedElements;
    ImageView fbLogin, googleLogin,linkdinLogin;
    String emailString, passwordString;
    TextInputEditText email, password;
    private CallbackManager callbackManager;
    private TextView textView;
    private String TAG = "LoginActivity";
    private String userEmail;
    private String userName;
    private String userPic;
    private Bundle rawData;
    private String host = "https://api.linkedin.com";
    private String topCardUrl =  host + "/v1/people/~:(num-connections,main-address,first-name,last-name,email-address,formatted-name,phone-numbers,public-profile-url,picture-url,picture-urls::(original))";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;


    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE,Scope.R_EMAILADDRESS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        rawData = new Bundle();
        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();



//        loginButton = findViewById(R.id.caption);
        email = findViewById(R.id.email_input_edit);
        password = findViewById(R.id.password_input_edit);
        linkdinLogin = findViewById(R.id.second);
        linkdinLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLinkedin();

//                LISessionManager.getInstance(getApplicationContext()).init(LoginActivity.this, buildScope(), new AuthListener() {
//                    @Override
//                    public void onAuthSuccess() {
////                        setUpdateState();
////                        LISessionManager.getInstance(getApplicationContext()).getSession()
//                        Toast.makeText(getApplicationContext(), "success" + LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().toString(), Toast.LENGTH_LONG).show();
//                    }
//                    @Override
//                    public void onAuthError(LIAuthError error) {
////                        setUpdateState();
//                        Log.i("Error",error.toString());
//                        Toast.makeText(getApplicationContext(), "failed " + error.toString(), Toast.LENGTH_LONG).show();
//                    }
//                }, true);
            }
        });
        googleLogin = findViewById(R.id.last);
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                emailString = email.getText().toString();
//                passwordString = password.getText().toString();
//                if (emailString.length() != 0 && passwordString.length() != 0) {
//                    mAuth.signInWithEmailAndPassword(emailString, passwordString)
//                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    if (task.isSuccessful()) {
//                                        // Sign in success, update UI with the signed-in user's information
//                                        Log.d(TAG, "signInWithEmail:success");
//                                        FirebaseUser user = mAuth.getCurrentUser();
//                                        Logger.i("user-> " + user.getEmail());
//                                        displayInformation();
//                                    } else {
//                                        // If sign in fails, display a message to the user.
//                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
//                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
//                                                Toast.LENGTH_SHORT).show();
//                                        Logger.i("Error in login");
//                                    }
//
//                                    // ...
//                                }
//                            });
//                }
//                else {
//                    Toast.makeText(LoginActivity.this,"Enter Email/Password",Toast.LENGTH_LONG).show();
//                }
//            }
//
//        });
        mAuth = FirebaseAuth.getInstance();

        fbLogin = findViewById(R.id.facebook_button);
        fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(
                        LoginActivity.this,
                        Arrays.asList("user_photos", "email", "user_birthday", "public_profile")
                );
            }
        });

//        logger.logPurchase(BigDecimal.valueOf(4.32), Currency.getInstance("USD"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Logger.i("Starttdd");
                        setFacebookData(loginResult);
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Logger.i("Starttdd" + exception);
                    }
                });
        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Logger.i("Login Succes");
                // App code
                Logger.i("perffff");
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                Logger.i("Exxx" + exception);
                // App code
            }
        });


        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Logger.i("Perfect");
                        setFacebookData(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Logger.i("Errr" + exception);
                    }
                });
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                 displayMessage(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        final AnimatedViewPager pager = ButterKnife.findById(this, R.id.pager);
        final ImageView background = ButterKnife.findById(this, R.id.scrolling_background);
        int[] screenSize = screenSize();

        for (ImageView element : sharedElements) {
            @ColorRes int color = element.getId() != R.id.logo ? R.color.white_transparent : R.color.color_logo_log_in;
            DrawableCompat.setTint(element.getDrawable(), ContextCompat.getColor(this, color));
        }
        //load a very big image and resize it, so it fits our needs
        Glide.with(this)
                .load(R.drawable.busy)
                .asBitmap()
                .override(screenSize[0] * 2, screenSize[1])
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(new ImageViewTarget<Bitmap>(background) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        background.setImageBitmap(resource);
                        background.post(() -> {
                            //we need to scroll to the very left edge of the image
                            //fire the scale animation
                            background.scrollTo(-background.getWidth() / 2, 0);
                            ObjectAnimator xAnimator = ObjectAnimator.ofFloat(background, View.SCALE_X, 4f, background.getScaleX());
                            ObjectAnimator yAnimator = ObjectAnimator.ofFloat(background, View.SCALE_Y, 4f, background.getScaleY());
                            AnimatorSet set = new AnimatorSet();
                            set.playTogether(xAnimator, yAnimator);
                            set.setDuration(getResources().getInteger(R.integer.duration));
                            set.start();
                        });
                        pager.post(() -> {
                            AuthAdapter adapter = new AuthAdapter(getSupportFragmentManager(), pager, background, sharedElements);
                            pager.setAdapter(adapter);
                        });
                    }
                });
    }

    private int[] screenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new int[]{size.x, size.y};
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);



        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                Logger.i("Google sign in failed");
                // [END_EXCLUDE]
            }
        }

    }

    private void displayMessage(Profile profile) {
        if (profile != null) {
            Logger.i("Profile Name->" + profile.getName());
//            userEmail = profile.get
//            userName = profile.getName();
            Logger.i("Info " + profile.getProfilePictureUri(400, 400));
//            textView.setText(profile.getName());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        displayMessage(profile);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        Logger.i("User Info-> " + currentUser.getEmail());
//        updateUI(currentUser);
    }

    public void RequestData() {
        Log.i("RequestData()-> ", "Working");
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                JSONObject json = response.getJSONObject();
                System.out.println("Json data :" + json);
                ;
                try {
                    if (json != null) {
                        Logger.json(json.toString());
                        String text = "<b>Name :</b> " + json.getString("name") + "<br><br><b>Email :</b> " + json.getString("email") + "<br><br><b>Profile link :</b> " + json.getString("link");
//                        details_txt.setText();

                        Logger.i("data-> " + Html.fromHtml(text));
//                        profile.setProfileId(json.getString("id"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void displayInformation() {
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
            Logger.i("name-> " + name + " email-> " + email + " photo-> " + photoUrl + " verify -> " + emailVerified);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        Logger.i("Loading");
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Logger.i("User Info-> " + user.getEmail() + " -> " + user.getProviderData() +" " + user.getPhotoUrl() + " user -> " + user.getDisplayName() + user.toString());
                            userEmail = user.getEmail();
                            userName = user.getDisplayName();
                            userPic = user.getPhotoUrl().toString();
                            userLoggedIn();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                            Logger.i("Authentication Failed.");
                        }

                        // [START_EXCLUDE]
                        Logger.i("Loading Done");
                        // [END_EXCLUDE]
                    }
                });
    }

    public void loginLinkedin() {
        LISessionManager.getInstance(getApplicationContext()).init(this,
                buildScope(), new AuthListener() {
                    @Override
                    public void onAuthSuccess() {

                        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                        apiHelper.getRequest(LoginActivity.this, topCardUrl, new ApiListener() {
                            @Override
                            public void onApiSuccess(ApiResponse s) {
                                Log.i("info",s.getResponseDataAsJson().toString());

                                try {
                                    JSONObject profileInfo = s.getResponseDataAsJson();
                                    JSONObject profilePic  = profileInfo.getJSONObject("pictureUrls");
                                    JSONArray imageUrl = profilePic.getJSONArray("values");
                                    Log.i("Name-> " , profileInfo.getString("formattedName"));
                                    userName = profileInfo.getString("formattedName");
                                    userEmail = profileInfo.getString("emailAddress");
                                    userPic = profileInfo.getString("emailAddress");
                                    userLoggedIn();
                                    Log.i("Image->" , imageUrl.getString(0));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
//                                openUserProfile();
                            }

                            @Override
                            public void onApiError(LIApiError error) {
                                Log.w("Error",error.toString());
                            }
                        });

                    }

                    @Override
                    public void onAuthError(LIAuthError error) {

                    }
                }, true);
    }

    public void openUserProfile(){
        DeepLinkHelper deepLinkHelper = DeepLinkHelper.getInstance();
        deepLinkHelper.openCurrentProfile(LoginActivity.this, new DeepLinkListener() {
            @Override
            public void onDeepLinkSuccess() {
            }

            @Override
            public void onDeepLinkError(LIDeepLinkError error) {
            }
        });
    }

    private void setFacebookData(final LoginResult loginResult)
    {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            Log.i("Response",response.toString());

                            String email = response.getJSONObject().getString("email");
                            String firstName = response.getJSONObject().getString("first_name");
                            String lastName = response.getJSONObject().getString("last_name");

                            Profile profile = Profile.getCurrentProfile();
                            String id = profile.getId();
                            String link = profile.getLinkUri().toString();
                            Log.i("Link",link);
                            if (Profile.getCurrentProfile()!=null)
                            {
                                Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                            }
                            if (object.has("friends")) {
                                JSONObject friend = object.getJSONObject("friends");
                                JSONArray data = friend.getJSONArray("data");
                                for (int i=0;i<data.length();i++){
                                    Log.i("idddd",data.getJSONObject(i).getString("id"));
                                }
                            }

                            Log.i("Login" + "Email", email);
                            Log.i("Login"+ "FirstName", firstName);
                            Log.i("Login" + "LastName", lastName);
                            userEmail = email;
                            userName = firstName + " " + lastName;
                            userPic = Profile.getCurrentProfile().getProfilePictureUri(200, 200).toString();
                            userLoggedIn();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void mainActivityIntent(){
        Intent mainActivityIntent = new Intent(LoginActivity.this, ProfileActivity.class);
        startActivity(mainActivityIntent);
    }

    private void userLoggedIn(){
        rawData.putString("userName",userName);
        rawData.putString("userEmail",userEmail);
        rawData.putString("userPic",userPic);

    }
}



