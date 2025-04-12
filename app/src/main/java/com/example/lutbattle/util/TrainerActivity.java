package com.example.lutbattle.util;

import com.example.lutbattle.R;
import com.example.lutbattle.model.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class TrainerActivity extends AppCompatActivity {

    private Spinner lutemonSpinner;
    private TextView lutemonStatsText;
    private Button trainButton, backButtonTrain, homeButton;

    private GameManager gameManager;
    private LUTemon selectedLutemon;
    private List<LUTemon> trainingLutemons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer);

        // Initialize GameManager instance
        gameManager = GameManager.getInstance();

        // Link UI components to variables
        lutemonSpinner = findViewById(R.id.lutemonSpinner);
        lutemonStatsText = findViewById(R.id.lutemonStatsText);
        trainButton = findViewById(R.id.trainButton);
        backButtonTrain = findViewById(R.id.backButtonTrain);
        homeButton = findViewById(R.id.homeButton);

        // Load LUTemons available for training
        loadTrainingLutemons();

        // Configure the spinner to display LUTemons
        setupLutemonSpinner();

        // Set up button click listeners
        setupButtonListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh LUTemon data and update the spinner when returning to this activity
        loadTrainingLutemons();
        updateSpinner();
    }

    // Load LUTemons from GameManager
    private void loadTrainingLutemons() {
        trainingLutemons = gameManager.getLUTemons();
        updateButtonState();
    }

    // Configure the spinner to display LUTemon names
    private void setupLutemonSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, getLutemonNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lutemonSpinner.setAdapter(adapter);

        // Handle spinner item selection
        lutemonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (trainingLutemons.size() > position) {
                    selectedLutemon = trainingLutemons.get(position);
                    updateLutemonStats();
                    updateButtonState();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedLutemon = null;
                lutemonStatsText.setText("No LUTemon selected");
                updateButtonState();
            }
        });
    }

    // Refresh the spinner with updated LUTemon data
    private void updateSpinner() {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) lutemonSpinner.getAdapter();
        adapter.clear();
        adapter.addAll(getLutemonNames());
        adapter.notifyDataSetChanged();

        if (trainingLutemons.isEmpty()) {
            lutemonStatsText.setText("No LUTemons in training area");
            selectedLutemon = null;
        } else if (lutemonSpinner.getSelectedItemPosition() >= 0 &&
                lutemonSpinner.getSelectedItemPosition() < trainingLutemons.size()) {
            selectedLutemon = trainingLutemons.get(lutemonSpinner.getSelectedItemPosition());
            updateLutemonStats();
        }

        updateButtonState();
    }

    // Retrieve LUTemon names for the spinner
    private List<String> getLutemonNames() {
        ArrayList<String> names = new ArrayList<>();
        for (LUTemon lutemon : trainingLutemons) {
            names.add(lutemon.getName());
        }
        return names;
    }

    // Display the selected LUTemon's stats
    private void updateLutemonStats() {
        if (selectedLutemon != null) {
            String type = getLutemonType(selectedLutemon);
            lutemonStatsText.setText(
                    "Name: " + selectedLutemon.getName() + "\n" +
                            "Type: " + type + "\n" +
                            "Level: " + selectedLutemon.getLevel() + "\n" +
                            "HP: " + selectedLutemon.getHp() + "\n" +
                            "Attack: " + selectedLutemon.getAttack() + "\n" +
                            "Defense: " + selectedLutemon.getDefense()
            );
        } else {
            lutemonStatsText.setText("No LUTemon selected");
        }
    }

    // Determine the type of the selected LUTemon
    private String getLutemonType(LUTemon lutemon) {
        if (lutemon instanceof FireLUTemon) {
            return "Fire";
        } else if (lutemon instanceof WaterLUTemon) {
            return "Water";
        } else if (lutemon instanceof GrassLUTemon) {
            return "Grass";
        } else if (lutemon instanceof ElectricLUTemon) {
            return "Electric";
        } else if (lutemon instanceof AirLUTemon) {
            return "Air";
        }
        return "Unknown";
    }

    // Set up button click actions
    private void setupButtonListeners() {
        trainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainSelectedLutemon();
            }
        });

        backButtonTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainerActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendSelectedLutemonHome();
            }
        });
    }

    // Train the selected LUTemon and handle cooldown logic
    private void trainSelectedLutemon() {
        if (selectedLutemon != null) {
            long currentTime = System.currentTimeMillis();
            long lastTrainedTime = selectedLutemon.getLastTrainedTime();
            long oneHourInMillis = 60; // 1 hour in milliseconds

            if (lastTrainedTime == 0 || currentTime - lastTrainedTime >= oneHourInMillis) {
                selectedLutemon.LevelUp();
                gameManager.addTrainer(selectedLutemon);
                selectedLutemon.setLastTrainedTime(currentTime);

                Toast.makeText(this, selectedLutemon.getName() + " trained! Level up to " +
                        selectedLutemon.getLevel(), Toast.LENGTH_SHORT).show();

                updateTrainingAvailability();
                updateLutemonStats();
                updateButtonState();
                updateSpinner();
            } else {
                long remainingTimeMillis = oneHourInMillis - (currentTime - lastTrainedTime);
                int remainingMinutes = (int) (remainingTimeMillis / (60 * 1000));
                int remainingSeconds = (int) ((remainingTimeMillis / 1000) % 60);
                updateTrainingAvailability();
                Toast.makeText(this, selectedLutemon.getName() + " is still recovering from training! " +
                                "Available in " + remainingMinutes + " min " + remainingSeconds + " sec",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "No LUTemon selected", Toast.LENGTH_SHORT).show();
        }
    }


    private void SendSelectedLutemonHome() {
        if (selectedLutemon != null) {
            long currentTime = System.currentTimeMillis();
            long lastTrainedTime = selectedLutemon.getLastTrainedTime();
            long oneHourInMillis = 60; // 1 hour in milliseconds

            if (lastTrainedTime == 0 || currentTime - lastTrainedTime >= oneHourInMillis) {
                gameManager.removeTrainer(selectedLutemon);

                Toast.makeText(this, selectedLutemon.getName() + " was sent home" +
                        selectedLutemon.getLevel(), Toast.LENGTH_SHORT).show();

                updateTrainingAvailability();
            } else {
                long remainingTimeMillis = oneHourInMillis - (currentTime - lastTrainedTime);
                int remainingMinutes = (int) (remainingTimeMillis / (60 * 1000));
                int remainingSeconds = (int) ((remainingTimeMillis / 1000) % 60);
                updateTrainingAvailability();
                Toast.makeText(this, selectedLutemon.getName() + " is still recovering from training! " +
                                "Available in " + remainingMinutes + " min " + remainingSeconds + " sec",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "No LUTemon selected", Toast.LENGTH_SHORT).show();
        }
    }

    // Update the training button based on cooldown status
    private void updateTrainingAvailability() {
        if (selectedLutemon != null) {
            long currentTime = System.currentTimeMillis();
            long lastTrainedTime = selectedLutemon.getLastTrainedTime();
            long oneHourInMillis = 60;

            if (lastTrainedTime == 0 || currentTime - lastTrainedTime >= oneHourInMillis) {
                trainButton.setEnabled(true);
                trainButton.setText("Train");
                homeButton.setEnabled(true);

            } else {
                long remainingTimeMillis = oneHourInMillis - (currentTime - lastTrainedTime);
                int remainingMinutes = (int) (remainingTimeMillis / (60 * 1000));

                trainButton.setEnabled(false);
                homeButton.setEnabled(false);
                trainButton.setText("Train (Available in " + remainingMinutes + " min)");
            }
        }
    }

    // Enable or disable buttons based on LUTemon selection
    private void updateButtonState() {
        boolean hasSelection = selectedLutemon != null;
        trainButton.setEnabled(hasSelection);
    }
}