package com.decidr.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.decidr.app.data.DecisionEntry;
import com.decidr.app.databinding.ActivityMainBinding;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private HistoryAdapter adapter;
    private final ExecutorService exportExecutor = Executors.newSingleThreadExecutor();

    // NEW: Export file launcher
    private final ActivityResultLauncher<String> exportLauncher = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("text/plain"),
            this::exportToFile
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setupRecyclerView();
        setupObservers();
        setupListeners();
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter();
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvHistory.setAdapter(adapter);
    }

    private void setupObservers() {
        // Observe the FILTERED decisions (this handles both all + search)
        viewModel.getFilteredDecisions().observe(this, decisions -> {
            adapter.setDecisions(decisions);
            updateEmptyStates(decisions);
        });

        // Observe the current result for the Hero Card
        viewModel.getCurrentResult().observe(this, result -> {
            if (result != null) {
                List<String> options = viewModel.getCurrentOptions().getValue();
                if (options != null && options.size() > 1) {
                    animateSlotMachine(options, result);
                } else {
                    binding.tvResult.setText(result);
                }

                binding.cardResult.animate().scaleX(1.05f).scaleY(1.05f).setDuration(150)
                        .withEndAction(() -> binding.cardResult.animate().scaleX(1f).scaleY(1f).setDuration(150));
            }
        });
    }

    // NEW: Handle empty states (empty vs no search results)
    private void updateEmptyStates(List<DecisionEntry> decisions) {
        boolean isEmpty = decisions == null || decisions.isEmpty();
        String currentQuery = binding.etSearch.getText().toString().trim();
        boolean isSearching = !currentQuery.isEmpty();

        if (isEmpty) {
            binding.rvHistory.setVisibility(android.view.View.GONE);
            if (isSearching) {
                binding.tvEmpty.setVisibility(android.view.View.GONE);
                binding.tvNoResults.setVisibility(android.view.View.VISIBLE);
            } else {
                binding.tvEmpty.setVisibility(android.view.View.VISIBLE);
                binding.tvNoResults.setVisibility(android.view.View.GONE);
            }
        } else {
            binding.rvHistory.setVisibility(android.view.View.VISIBLE);
            binding.tvEmpty.setVisibility(android.view.View.GONE);
            binding.tvNoResults.setVisibility(android.view.View.GONE);
        }
    }

    private void setupListeners() {
        // Decide button
        binding.btnDecide.setOnClickListener(v -> {
            String prompt = binding.etPrompt.getText().toString().trim();
            String options = binding.etOptions.getText().toString().trim();

            if (options.isEmpty()) {
                Toast.makeText(this, "Please enter some options!", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.makeDecision(prompt, options);
        });

        // Clear History button
        binding.btnClearHistory.setOnClickListener(v -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                    .setTitle("Clear History?")
                    .setMessage("Are you sure you want to permanently delete all past decisions?")
                    .setPositiveButton("Clear", (dialog, which) -> {
                        viewModel.clearHistory();
                        binding.etSearch.setText(""); // Clear search too
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Share button
        binding.btnShare.setOnClickListener(v -> {
            String result = binding.tvResult.getText().toString();
            if (result != null && !result.equals("Your destiny awaits...")) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "Decidr chose: " + result + "\n\nTry Decidr - Let the app decide for you!");
                startActivity(Intent.createChooser(shareIntent, "Share your decision via"));
            }
        });

        // NEW: Search input
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setSearchQuery(s.toString());
            }
        });

        // NEW: Export button
        binding.btnExport.setOnClickListener(v -> {
            if (viewModel.getAllDecisions().getValue() == null ||
                    viewModel.getAllDecisions().getValue().isEmpty()) {
                Toast.makeText(this, "No decisions to export!", Toast.LENGTH_SHORT).show();
                return;
            }
            exportLauncher.launch("decidr_history.txt");
        });
    }

    // NEW: Write decisions to file
    private void exportToFile(Uri uri) {
        if (uri == null) return;

        List<DecisionEntry> decisions = viewModel.getAllDecisions().getValue();
        if (decisions == null || decisions.isEmpty()) return;

        exportExecutor.execute(() -> {
            try {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                if (outputStream == null) return;

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                StringBuilder sb = new StringBuilder();
                sb.append("=== DECIDR - DECISION HISTORY ===\n");
                sb.append("Exported on: ").append(sdf.format(new Date())).append("\n\n");

                for (DecisionEntry entry : decisions) {
                    sb.append("Decision: ").append(entry.prompt).append("\n");
                    sb.append("Result:   ").append(entry.result).append("\n");
                    sb.append("Date:     ").append(sdf.format(new Date(entry.timestamp))).append("\n");
                    sb.append("-----------------------------------\n");
                }

                outputStream.write(sb.toString().getBytes());
                outputStream.close();

                // Show success toast on main thread
                runOnUiThread(() ->
                        Toast.makeText(this, "History exported successfully!", Toast.LENGTH_SHORT).show()
                );
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void animateSlotMachine(List<String> options, String finalResult) {
        Handler handler = new Handler(Looper.getMainLooper());
        final int[] currentIndex = {0};
        final int totalFlashes = 15;
        final long delay = 80;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentIndex[0] < totalFlashes) {
                    int randomIndex = new Random().nextInt(options.size());
                    binding.tvResult.setText(options.get(randomIndex).trim());
                    currentIndex[0]++;
                    handler.postDelayed(this, delay);
                } else {
                    binding.tvResult.setText(finalResult);
                }
            }
        };

        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exportExecutor.shutdown();
    }
}