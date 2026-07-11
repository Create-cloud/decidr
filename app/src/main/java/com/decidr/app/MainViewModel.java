package com.decidr.app;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.decidr.app.data.DecisionEntry;
import com.decidr.app.data.DecisionRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainViewModel extends AndroidViewModel {
    private final DecisionRepository mRepository;
    private final LiveData<List<DecisionEntry>> mAllDecisions;
    private final MutableLiveData<String> mCurrentResult = new MutableLiveData<>();
    private final MutableLiveData<List<String>> mCurrentOptions = new MutableLiveData<>();

    // NEW: Search state
    private final MutableLiveData<String> mCurrentQuery = new MutableLiveData<>("");
    private LiveData<List<DecisionEntry>> mFilteredDecisions;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = new DecisionRepository(application);
        mAllDecisions = mRepository.getAllDecisions();
        mFilteredDecisions = mAllDecisions; // Start with all
    }

    public LiveData<List<DecisionEntry>> getAllDecisions() { return mAllDecisions; }
    public LiveData<String> getCurrentResult() { return mCurrentResult; }
    public LiveData<List<String>> getCurrentOptions() { return mCurrentOptions; }

    // NEW: Get filtered decisions based on current query
    public LiveData<List<DecisionEntry>> getFilteredDecisions() { return mFilteredDecisions; }

    // NEW: Update search query
    public void setSearchQuery(String query) {
        mCurrentQuery.setValue(query);
        if (query == null || query.trim().isEmpty()) {
            mFilteredDecisions = mAllDecisions;
        } else {
            mFilteredDecisions = mRepository.searchDecisions(query.trim());
        }
    }

    public void makeDecision(String prompt, String optionsText) {
        if (optionsText == null || optionsText.trim().isEmpty()) return;

        List<String> options = Arrays.asList(optionsText.split(","));
        if (options.isEmpty()) return;

        Random random = new Random();
        String chosen = options.get(random.nextInt(options.size())).trim();

        mCurrentOptions.setValue(options);
        mCurrentResult.setValue(chosen);

        DecisionEntry entry = new DecisionEntry(
                prompt.isEmpty() ? "Quick Decision" : prompt,
                chosen,
                System.currentTimeMillis()
        );
        mRepository.insert(entry);
    }

    public void clearHistory() {
        mRepository.deleteAll();
    }

    // NEW: Get all decisions for export (non-LiveData version)
    public LiveData<List<DecisionEntry>> getAllDecisionsForExport() {
        return mAllDecisions;
    }
}