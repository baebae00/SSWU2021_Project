package com.example.sw_project.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.sw_project.R;
import com.example.sw_project.WriteInfo;
import com.example.sw_project.adapter.FragmentAdapter;
import com.example.sw_project.fragment.PostCommentFragment;
import com.example.sw_project.fragment.PostDetailFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewPostActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager detailViewPager;
    private PostDetailFragment fragment1;
    private PostCommentFragment fragment2;
    private FragmentAdapter adapter;
    private Bundle bundle;
//    private Activity activity;
    private WriteInfo writeinfo;
    private FirebaseUser user;
    private String TAG = "ViewPostActivity";
    private FirebaseFirestore db;
    private ArrayList<String> commentId, scrapId;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post_main);

        Intent intent = getIntent();// 인텐트 받아오기
        writeinfo = (WriteInfo) intent.getSerializableExtra("writeInfo");

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        tabLayout = findViewById(R.id.viewPostTab);
        detailViewPager = findViewById(R.id.postDetailPager);

        //getFragmentManager을 getSupportFragmentManager대신 사용
        adapter = new FragmentAdapter(getSupportFragmentManager(),1);

        fragment1 = new PostDetailFragment();
        fragment2 = new PostCommentFragment();

        adapter.addFragment(fragment1);
        adapter.addFragment(fragment2);

        detailViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(detailViewPager);
        tabLayout.getTabAt(0).setText("모집글");
        tabLayout.getTabAt(1).setText("댓글");

        //contest 정보 Fragment에도 전달하기
        bundle = new Bundle();
        bundle.putSerializable("writeInfo",writeinfo);
        fragment1.setArguments(bundle);
        fragment2.setArguments(bundle);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (writeinfo.getUserUid().equals(user.getUid())) {
            findViewById(R.id.post_menu).setVisibility(View.VISIBLE);
            findViewById(R.id.post_menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v);
                }
            });
        }else{
            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout.setMargins(60,30,60,30);
            findViewById(R.id.viewPostTab).setLayoutParams(layout);
        }

    }

    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getApplicationContext(), v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        builder = new AlertDialog.Builder(ViewPostActivity.this);
                        dialog = builder.setMessage("작성한 팀원 모집글을 삭제합니다")
                                .setNegativeButton("CANCEL", null)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        postDeleteFunc();
                                    }
                                })
                                .create();
                        dialog.show();
                        return true;
                    case R.id.edit:
                        //posting 이동(writeInfo랑 같이)
                        Intent intent = new Intent(getApplicationContext(), PostingActivity.class);
                        intent.putExtra("writeInfo",writeinfo);
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }

            }
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post_menu, popup.getMenu());
        popup.show();
    }


    private void startToast(String msg){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {

        switch(item.getItemId()) {
            case R.id.delete:
                builder = new AlertDialog.Builder(ViewPostActivity.this);
                dialog = builder.setMessage("작성한 팀원 모집글을 삭제합니다")
                        .setNegativeButton("CANCEL", null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postDeleteFunc();
                            }
                        })
                        .create();
                dialog.show();
                break;
            case R.id.edit:
                //posting 이동(writeInfo랑 같이)
                Intent intent = new Intent(this, PostingActivity.class);
                intent.putExtra("writeInfo",writeinfo);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void postDeleteFunc(){

        db.collection("posts").document(writeinfo.getPostid())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startToast("게시글을 삭제하였습니다");
                        relatedPostDbCheck();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startToast("게시글을 삭제하지 못하였습니다.");
                    }
                });
    }

    private void relatedPostDbCheck(){

        commentId = new ArrayList<>();
        scrapId = new ArrayList<>();

        db.collection("comments")
                .whereEqualTo("postid", writeinfo.getPostid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                commentId.add(document.getId());
                            }
                            deleteRelated(commentId, "comments");
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        db.collection("scrapsPost")
                .whereEqualTo("postId", writeinfo.getPostid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                scrapId.add(document.getId());
                            }
                            deleteRelated(scrapId, "scrapsPost");
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void deleteRelated(final ArrayList<String> list, final String collection){

        for(String id : list){
            db.collection(collection).document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        DocumentReference docRef = db.collection("posts").document(writeinfo.getPostid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                writeinfo = documentSnapshot.toObject(WriteInfo.class);

                bundle = new Bundle();
                bundle.putSerializable("writeInfo",writeinfo);
                fragment1.setArguments(bundle);
                fragment2.setArguments(bundle);

                PostDetailFragment mf = (PostDetailFragment) adapter.getItem(0);
                mf.postDataShow();
            }
        });


    }
}