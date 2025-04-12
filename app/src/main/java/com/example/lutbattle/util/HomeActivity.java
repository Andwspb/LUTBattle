package com.example.lutbattle.util;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lutbattle.R;
import com.example.lutbattle.model.LUTemon;

import java.util.ArrayList;
import java.util.Iterator;


public class HomeActivity extends AppCompatActivity {
    // Define the RecyclerView and MovieAdapter fields
    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    private Button backButton;

    // Implement the onCreate method to set the content view, initialize the RecyclerView, and load movies
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize the RecyclerView and set the layout manager
        recyclerView = findViewById(R.id.recyclerViewMenu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        backButton = findViewById(R.id.backButton);

        // Set click listeners
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ArrayList<LUTemon> displayedLUT = GameManager.getInstance().getLUTemons();
        Iterator<LUTemon> iterator = displayedLUT.iterator();
        while (iterator.hasNext()) {
            LUTemon lutemon = iterator.next();
            if (GameManager.getInstance().getTrainers().contains(lutemon)) {
                iterator.remove(); // Safely removes the element
            }
        }
        ArrayList<LUTemon> lutemons = GameManager.getInstance().getLUTemons();
        homeAdapter = new HomeAdapter(lutemons);
        recyclerView.setAdapter(homeAdapter);
    }
}
