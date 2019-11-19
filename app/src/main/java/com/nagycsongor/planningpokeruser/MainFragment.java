package com.nagycsongor.planningpokeruser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    private RecyclerView recyclerView;
    public VoteAdapter adapter;
    private ArrayList<String> numbers;
    private TextView problem;
    private Button voteButton;
    private String voteTo;
    private String key;
    private boolean b;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private User user;

    public MainFragment(String key, User user) {
        this.key = key;
        this.user = user;
        numbers = new ArrayList<>();
        addNumbers();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        voteButton = view.findViewById(R.id.voteButton);
        problem = view.findViewById(R.id.voteProblem);
        recyclerView = view.findViewById(R.id.gridRecyclerView);
        adapter = new VoteAdapter(getContext(), numbers);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        return view;
    }
    private void addNumbers() {

        numbers.add("1");
        numbers.add("3");
        numbers.add("5");
        numbers.add("7");
        numbers.add("10");
        numbers.add("20");
        numbers.add("50");
        numbers.add("100");
        numbers.add("?");


    }
    @Override
    public void onResume() {
        super.onResume();
        reference.child("problems").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                problem.setText("No more questions :)");
                voteButton.setClickable(false);
                b = false;
                for (DataSnapshot problemSnapshot : dataSnapshot.getChildren()){
                    final Problem p = problemSnapshot.getValue(Problem.class);
                    if (p.isAvailable()){
                        voteTo = p.getName();

                        reference.child("groups").child(key).child("members").child(user.id).child("votes").child(voteTo).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String s = (String) dataSnapshot.getValue();
                                if ( s ==  null){
                                    b = true;
                                    voteTo = p.getName();
                                    problem.setText(voteTo);

                                    voteButton.setClickable(true);
                                    voteButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            int voteNumber = adapter.getVoteNumber();
                                            if (voteNumber == -1){
                                                Toast.makeText(getContext(), "Please select a card!", Toast.LENGTH_SHORT).show();
                                            }else{
                                                String mark;
                                                if (voteNumber == -2){
                                                    mark = "?";
                                                }else {
                                                    mark = String.valueOf(voteNumber);
                                                }
                                                String voteId = user.id;
                                                Vote vote = new Vote(user.id,user.username,mark);

                                                reference.child("groups").child(key).child("members").child(user.id).child("votes").child(voteTo).setValue(voteTo);

                                                reference.child("votes").child(key).child(voteTo).child(voteId).setValue(vote).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getContext(), "Vote added!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                                fragmentTransaction.replace(R.id.fragment_frameLayout, new ListFragment(key,voteTo,user));
                                                fragmentTransaction.addToBackStack(null);
                                                fragmentTransaction.commit();

                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        if (b){
                            break;
                        }

                    }
                }
                adapter = new VoteAdapter(getContext(), numbers);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        if (voteTo.isEmpty()) {
//            problem.setText("Nincs tobb task");
//            voteButton.setClickable(false);
//        } else {
//            problem.setText(voteTo);
//
//            voteButton.setClickable(true);
//            voteButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int voteNumber = adapter.getVoteNumber();
//                    if (voteNumber == -1){
//                        Toast.makeText(getContext(), "Please change a value!", Toast.LENGTH_SHORT).show();
//                    }else{
//
//                    }
//                }
//            });
        }
//    }

}
