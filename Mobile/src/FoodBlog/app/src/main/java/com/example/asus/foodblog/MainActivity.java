package com.example.asus.foodblog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mBlogList;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;  //////////was mDatabaseUsers////////////////

    private boolean mProcessLike = false;
    private boolean mProcessDislike = false;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabaseDislike;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) //User not logged in
                {

                    Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);


                }
            }
        };


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseDislike = FirebaseDatabase.getInstance().getReference().child("Dislikes");


        mDatabaseUsers.keepSynced(true);
        mDatabase.keepSynced(true);
        mDatabaseLike.keepSynced(true);
        mDatabaseDislike.keepSynced(true);
        mBlogList = (RecyclerView) findViewById(R.id.blog_list);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        /////
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));

        //10/2/2017
        //checkUserExist();


    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(

                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {

                final String post_key = getRef(position).getKey();

                //final String post_key = getRef(position);

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setmLikeBtn(post_key);
                viewHolder.setmDislikeBtn(post_key);


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(MainActivity.this,post_key,Toast.LENGTH_SHORT).show();


                        Intent singleBlogIntent = new Intent(MainActivity.this,BlogSingleActivity.class);
                        singleBlogIntent.putExtra("blog_id",post_key);
                        startActivity(singleBlogIntent);
                    }
                });

                viewHolder.mDislikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mProcessDislike = true;
                        mDatabaseDislike.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                try
                                {
                                    if(mProcessDislike){
                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();

                                        if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                                            mDatabaseDislike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                            mProcessDislike = false;

                                        }else {
                                            mDatabaseDislike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomAgain");
                                            mProcessDislike = false;
                                        }
                                    }
                                }
                                catch(NullPointerException e)
                                {
                                    Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
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

                viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mProcessLike = true;


                            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    try
                                    {
                                        if (mProcessLike) {
                                            mDatabaseDislike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                            if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                                mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                                mProcessLike = false;
                                            } else {
                                                mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("Random");
                                                mProcessLike = false;
                                            }
                                        }
                                    }

                                    catch(NullPointerException e)
                                    {
                                        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
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
        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }


    private void checkUserExist() {

        if(mAuth.getCurrentUser()!=null){
            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.hasChild(user_id))
                    {
                        Intent setupIntent = new Intent(MainActivity.this,SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    ///Baire o kora jaito
    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageButton mLikeBtn;
        ImageButton mDislikeBtn;
        DatabaseReference mDatabaseLike;
        DatabaseReference mDatabaseDislike;
        FirebaseAuth mAuth;

        public BlogViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mLikeBtn = (ImageButton)mView.findViewById(R.id.likeBtn);
            mDislikeBtn = (ImageButton)mView.findViewById(R.id.dislikeBtn);

            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth = FirebaseAuth.getInstance();
            mDatabaseDislike = FirebaseDatabase.getInstance().getReference().child("Dislikes");
            mAuth = FirebaseAuth.getInstance();
            mDatabaseLike.keepSynced(true);
            mDatabaseDislike.keepSynced(true);
        }

        public void setmLikeBtn(final String post_key){
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try
                    {
                        if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){ //String user_id = mAuth.getCurrentUser().getUid();

                            mLikeBtn.setImageResource(R.mipmap.thumb_up_red);

                        }

                        else
                        {
                            mLikeBtn.setImageResource(R.mipmap.thumb_up_black);
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
                    try
                    {
                        if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                            mDislikeBtn.setImageResource(R.mipmap.thumb_down_red);

                        }else
                        {
                            mDislikeBtn.setImageResource(R.mipmap.thumb_down_black);
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



        public void setTitle(String title){

            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);

        }

        public void setDesc(String desc)
        {
            TextView post_desc = (TextView) mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }

        public void setImage(Context ctx,String image)
        {
            ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);



        }

        public void setUsername(String username)
        {
            TextView post_username = (TextView) mView.findViewById(R.id.post_username);
            post_username.setText(username);
        }


    }

    ///

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_add)
        {
            startActivity(new Intent(MainActivity.this,PostActivity.class));
        }

        if (item.getItemId() == R.id.action_logout)
        {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {

        mAuth.signOut();


    }
}
