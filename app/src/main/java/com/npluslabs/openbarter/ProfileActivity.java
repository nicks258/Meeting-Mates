package com.npluslabs.openbarter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    String userEmail,userName,userPic;
    TextView userNameTv,userEmailTv;
    CircleImageView userImageView;
    Button inviteEarn;
    private Bundle rawData;
    private MaterialSheetFab materialSheetFab;
    private TextView fabOffer, fabHelp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Logger.addLogAdapter(new AndroidLogAdapter());
        rawData = getIntent().getExtras();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        inviteEarn = findViewById(R.id.share_it);
        if(rawData != null){
            userEmail = rawData.getString("userEmail");
            userName  = rawData.getString("userName");
            userPic   = rawData.getString("userPic");
            Logger.i("Name-> "+ userName + " Email-> " + userEmail +" userPic-> "+ userPic);
        }
        userEmailTv = findViewById(R.id.userEmail);
        userNameTv  = findViewById(R.id.userName);
        userImageView = findViewById(R.id.profile_image);
        Glide.with(this).load(userPic).into(userImageView);
        userNameTv.setText(userName);
        userEmailTv.setText(userEmail);

        Fab fab = (Fab) findViewById(R.id.fab);
        View sheetView = findViewById(R.id.fab_sheet);
        View overlay = findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.white);
        int fabColor = getResources().getColor(R.color.startblue);
        fabHelp = findViewById(R.id.fab_sheet_faq);
//        fabCallus = findViewById(R.id.fab_sheet_item_callus);
//        fabInvite = findViewById(R.id.fab_sheet_item_invite);
        fabOffer = findViewById(R.id.fab_sheet_item_feedbacks);
        fabHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                HelpFragment selectedFragment = new HelpFragment();
                fragmentTransaction2.replace(R.id.container,selectedFragment);
                fragmentTransaction2.addToBackStack(null)
                        .commit();
            }
        });

        fabOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                OfferFragment selectedFragment = new OfferFragment();
                fragmentTransaction2.replace(R.id.container,selectedFragment);
                fragmentTransaction2.addToBackStack(null)
                        .commit();
            }
        });


        // Initialize material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                sheetColor, fabColor);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        inviteEarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "I’m inviting you to join OpenBarter, " +
                        "a simple and secure helping app by nPlusLabs. Here’s my code cS72p - " +
                        "just enter it before your first transaction. Once you’ve sent your first " +
                        "transaction, we’ll each get ₹51! http://www.almaland.net/login");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }

}
