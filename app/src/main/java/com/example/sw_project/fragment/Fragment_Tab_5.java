package com.example.sw_project.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.sw_project.Activity.LogInActivity;
import com.example.sw_project.Activity.Setting2Activity;
import com.example.sw_project.BackPressCloseHandler;
import com.example.sw_project.MySharedPreferences;
import com.example.sw_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Fragment_Tab_5 extends Fragment {

    View view;

    private Button logout;
    private Button dropout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String TAG = "Fragment_Tab_5";
    private BackPressCloseHandler backPress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = (View) inflater.inflate(R.layout.activity_setting,container,false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Button passwordresetButton = (Button) view.findViewById(R.id.passwordresetbtn);
        passwordresetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Setting2Activity.class);
                startActivity(intent);
            }
        });

        logout = view.findViewById((R.id.logout));
        logout.setOnClickListener(View -> {
            new AlertDialog.Builder(getActivity())
                    .setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //AppDatabase.removeLoginAuthKey();
                            mAuth.signOut(); // logout
                            Toast.makeText(getContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                            new MySharedPreferences(getActivity()).clearUser();
                            moveToLogin();
                            dialog.dismiss(); //팝업창 종료
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //"아니오" 버튼 클릭 시 할 동작
                            dialog.dismiss(); //팝업창 종료
                        }
                    }).show();
        });

        dropout = view.findViewById((R.id.dropout));
        dropout.setOnClickListener(View -> {
            new AlertDialog.Builder(getActivity())
                    .setMessage("탈퇴 하시겠습니까?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //AppDatabase.removeLoginAuthKey();
                            deleteUsersFile();
                            mAuth.getCurrentUser().delete(); // logout
                            Toast.makeText(getContext(), "회원 탈퇴 되었습니다.", Toast.LENGTH_SHORT).show();
                            new MySharedPreferences(getActivity()).clearUser();
                            moveToLogin();
                            dialog.dismiss(); //팝업창 종료
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //"아니오" 버튼 클릭 시 할 동작
                            dialog.dismiss(); //팝업창 종료
                        }
                    }).show();
        });

        backPress = new BackPressCloseHandler(getActivity());
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                backPress.onBackPressed();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        return view;
    }

    private void deleteUsersFile(){
        db.collection("users").document(mAuth.getUid())
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

        db.collection("posts")
                .whereEqualTo("userUid", mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> documentId = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                documentId.add(document.getData().get("postid").toString());
                            }
                            removeUserPosts(documentId);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void removeUserPosts(ArrayList<String> documentList){
        for(String postId : documentList){
            db.collection("posts").document(postId)
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

    private void moveToLogin() {
        Intent intent = new Intent(getActivity(), LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
}
