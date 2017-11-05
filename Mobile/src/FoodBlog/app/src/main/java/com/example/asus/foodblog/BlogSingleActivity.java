package com.example.asus.foodblog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.DecimalFormat;
import java.util.Map;

public class BlogSingleActivity extends AppCompatActivity {

    private String mPost_key = null;
    private DatabaseReference mDatabase;

    //new
    private ImageButton mSingleBlogLikeBtn;
    private ImageButton mSingleBlogDislikeBtn;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabaseDislike;
    private FirebaseAuth mAuth;
    private boolean mSingleBlogProcessLike = false;
    private boolean mSingleBlogProcessDislike = false;
    private boolean mSingleBlogProcessRating = false;

    private ImageView mBlogSingleImage;
    private TextView mBlogSingleTitle;
    private TextView mBlogSingleDesc;

    private TextView mSingleLikeCount;
    private TextView mSingleDislikeCount;
    private ImageButton mDltBtn;

    //newnew
    private ImageView mSingleUserImage;
    private DatabaseReference mDatabaseUser;
    private TextView mBlogSingleusername;

    //newRating

    private RatingBar mBlogSingleAverageRatingBar;
    private RatingBar mBlogSingleRatingBar;
    private DatabaseReference mDatabaseRating;
    private DatabaseReference mDataBaseCurrentPostRatingShow;
    private DatabaseReference mIndividualRating;
    private TextView mRating;

    //avgRating
    private TextView avgRating;

    //checkRating Existance
    private  DatabaseReference mCheckRatingExistance;

    private TextView mAvgRatingInText;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mPost_key =getIntent().getExtras().getString("blog_id");
        mBlogSingleDesc = (TextView) findViewById(R.id.singleBlogDesc);
        mBlogSingleTitle = (TextView) findViewById(R.id.singleBlogTitle);
        mBlogSingleImage = (ImageView) findViewById(R.id.singlBlogImage);


        //new
        mSingleBlogLikeBtn = (ImageButton) findViewById(R.id.singleBlogLikeBtn);
        mSingleBlogDislikeBtn = (ImageButton) findViewById(R.id.singleBlogDislikeBtn);
        mAuth =FirebaseAuth.getInstance();

        mSingleLikeCount = (TextView) findViewById(R.id.singleBlogLikeCount);
        mSingleDislikeCount = (TextView) findViewById(R.id.singleBlogDislikeCount);
        mDltBtn = (ImageButton) findViewById(R.id.dltBtn);
        mDltBtn.setVisibility(View.INVISIBLE);

        //newnew

        mSingleUserImage = (ImageView) findViewById(R.id.singleBlogUserImage) ;
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mBlogSingleusername = (TextView) findViewById(R.id.blogSinglePostUsername);

        //newRatingBar
        mBlogSingleRatingBar = (RatingBar) findViewById(R.id.singleBlogRatingBar) ;
        mBlogSingleAverageRatingBar = (RatingBar) findViewById(R.id.singleBlogAverageRating);

        mRating = (TextView) findViewById(R.id.ratingShow2);


        //ratingAverage
        avgRating = (TextView) findViewById(R.id.textView2);




        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseDislike = FirebaseDatabase.getInstance().getReference().child("Dislikes");

        //Rating
        mDatabaseRating = FirebaseDatabase.getInstance().getReference().child("Rating");
        //mDataBaseCurrentPostRating = mDatabaseRating.child(mPost_key);
        //
        mIndividualRating = FirebaseDatabase.getInstance().getReference().child("IndividualRating");
        //mCheckRatingExistance = FirebaseDatabase.getInstance().getReference().child()

        mAvgRatingInText = (TextView) findViewById(R.id.singleBlogAverageRatingInText);


        mDatabaseLike.keepSynced(true);
        mDatabaseDislike.keepSynced(true);
        mDatabaseUser.keepSynced(true);
        mDatabaseRating.keepSynced(true);
        mIndividualRating.keepSynced(true);
        //mDataBaseCurrentPostRating.keepSynced(true);




        setmLikeBtn(mPost_key);
        setmDislikeBtn(mPost_key);
        //setmAverageRating(mPost_key);
        checkPostRatingExistance(mPost_key);
        //setOnStartActivityOwnRatingBar(mPost_key);





        mBlogSingleRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                mSingleBlogProcessRating = true;

                final float ratingInFloat = ratingBar.getRating();
                final String ratingInString = Float.toString(ratingInFloat);

                mRating.setText(ratingInString);

                mDatabaseRating.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //checking if user already given rating ;
                      /*  if(dataSnapshot.child(mPost_key).hasChild(mAuth.getCurrentUser().getUid()))
                        {
                            String rating  = dataSnapshot.child(mPost_key).child(mAuth.getCurrentUser().getUid()).getValue().toString();
                            Float ratingInFloat = Float.parseFloat(rating);

                            mBlogSingleRatingBar.setRating(ratingInFloat);
                        }
*/
                        if(mSingleBlogProcessRating)
                        {
                            mDatabaseRating.child(mPost_key).child(mAuth.getCurrentUser().getUid()).setValue(ratingInString);
                            mIndividualRating.child(mPost_key).child(mAuth.getCurrentUser().getUid()).child("Rating").setValue(ratingInString);

                            mSingleBlogProcessRating = false;


                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

/*
                mIndividualRating.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(mSingleBlogProcessRating)
                        {
                            mIndividualRating.child(mPost_key).child("User").setValue(mAuth.getCurrentUser().getUid());
                            mIndividualRating.child(mPost_key).child("Rating").setValue(ratingInString);



                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
*/
            }
        });






        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    String post_title = (String) dataSnapshot.child("title").getValue();
                    String post_desc = (String) dataSnapshot.child("desc").getValue();
                    String post_image = (String) dataSnapshot.child("image").getValue();
                    String post_uid = (String) dataSnapshot.child("uid").getValue();
                    //long likeCount = dataSnapshot.getChildrenCount();

                    setUserinfo(post_uid);


                    mBlogSingleTitle.setText(post_title);
                    mBlogSingleDesc.setText(post_desc);
                    Picasso.with(BlogSingleActivity.this).load(post_image).into(mBlogSingleImage);

                    if (mAuth.getCurrentUser().getUid().equals(post_uid)) {
                        mDltBtn.setVisibility(View.VISIBLE); //user thakle dekhabe
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDltBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(mPost_key).removeValue();
                Intent mainIntent = new Intent(BlogSingleActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });






        mSingleBlogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mSingleBlogProcessLike = true;


                mDatabaseLike.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {




                        try
                        {
                            if (mSingleBlogProcessLike) {
                                mDatabaseDislike.child(mPost_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                if (dataSnapshot.child(mPost_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                    mDatabaseLike.child(mPost_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                    mSingleBlogProcessLike = false;
                                } else {
                                    mDatabaseLike.child(mPost_key).child(mAuth.getCurrentUser().getUid()).setValue("Random");
                                    mSingleBlogProcessLike = false;
                                }
                            }
                        }

                        catch(NullPointerException e)
                        {
                            Intent loginIntent = new Intent(BlogSingleActivity.this,LoginActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(loginIntent);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });



        mSingleBlogDislikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSingleBlogProcessDislike = true;
                mDatabaseDislike.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try
                        {
                            if(mSingleBlogProcessDislike){
                                mDatabaseLike.child(mPost_key).child(mAuth.getCurrentUser().getUid()).removeValue();

                                if(dataSnapshot.child(mPost_key).hasChild(mAuth.getCurrentUser().getUid())){
                                    mDatabaseDislike.child(mPost_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                    mSingleBlogProcessDislike = false;

                                }else {
                                    mDatabaseDislike.child(mPost_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomAgain");
                                    mSingleBlogProcessDislike = false;
                                }
                            }
                        }
                        catch(NullPointerException e)
                        {
                            Intent loginIntent = new Intent(BlogSingleActivity.this,LoginActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(loginIntent);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });


    }

 /*
    private void setOnStartActivityOwnRatingBar(final String mPost_key) {


        mDatabaseRating.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()))
                {
                    String rating  = dataSnapshot.child(mPost_key).child(mAuth.getCurrentUser().getUid()).getValue().toString();
                    Float ratingInFloat = Float.parseFloat(rating);

                    mBlogSingleRatingBar.setRating(ratingInFloat);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
*/

    private void setOnStartActivityOwnRatingBar(final String mPost_key) {


        mIndividualRating.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()))
                {
                    String rating  = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("Rating").getValue().toString();
                    Float ratingInFloat = Float.parseFloat(rating);

                    mBlogSingleRatingBar.setRating(ratingInFloat);
                }

                else
                {
                    float f = (float)0.00;
                    mBlogSingleRatingBar.setRating(f);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void checkPostRatingExistance(final String mPost_key) {

        mIndividualRating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mPost_key))
                {
                    setmAverageRating(mPost_key);
                    setOnStartActivityOwnRatingBar(mPost_key);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setmAverageRating(String mPost_key) {




        mIndividualRating.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                collectRatings((Map<String, Object>) dataSnapshot.getValue());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public  void collectRatings(Map<String,Object> raters)
    {
        Integer childCount=0;
        Float totalRating= (float)0;
        for(Map.Entry<String, Object> entry : raters.entrySet())
        {
            Map singleRater =  (Map) entry.getValue();

            totalRating+=Float.parseFloat((String)singleRater.get("Rating"));
            childCount++;


        }
        //totalRating = totalRating + "   " + childCount.toString();

        avgRating.setText(totalRating.toString()+"  "+childCount);
        Float newFloat = totalRating/childCount;
        String ratingInText = Float.toString(newFloat);

        mBlogSingleAverageRatingBar.setRating(newFloat);

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String formatted = decimalFormat.format(newFloat);

        //String formatted = Float.toString(decimalFormat.format(newFloat));
        mAvgRatingInText.setText(formatted);

    }


    private void setUserinfo(String uid) {

        mDatabaseUser.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String username = dataSnapshot.child("nickName").getValue().toString();
                    String userImage = dataSnapshot.child("image").getValue().toString();


                    Picasso.with(BlogSingleActivity.this).load(userImage).resize(100,100).transform(new CircleTransform()).into(mSingleUserImage);




                    mBlogSingleusername.setText(username);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //new

    public void setmLikeBtn(final String post_key){
        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Counting Likes   :D


                long num_likes = dataSnapshot.child(mPost_key).getChildrenCount();
                String likesCount = Long.toString(num_likes);

                mSingleLikeCount.setText(likesCount);

                try
                {
                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){ //String user_id = mAuth.getCurrentUser().getUid();

                        mSingleBlogLikeBtn.setImageResource(R.mipmap.thumb_up_red);

                    }

                    else
                    {
                        mSingleBlogLikeBtn.setImageResource(R.mipmap.thumb_up_black);
                    }
                }
                catch (NullPointerException E)
                {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });


    }

    public void setmDislikeBtn(final String post_key){
        mDatabaseDislike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                //Counting Dislikes  :D


                long num_dislikes = dataSnapshot.child(mPost_key).getChildrenCount();
                String dislikesCount = Long.toString(num_dislikes);



                mSingleDislikeCount.setText(dislikesCount);





                try
                {
                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        mSingleBlogDislikeBtn.setImageResource(R.mipmap.thumb_down_red);

                    }else
                    {
                        mSingleBlogDislikeBtn.setImageResource(R.mipmap.thumb_down_black);
                    }
                }

                catch (NullPointerException e)
                {

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
