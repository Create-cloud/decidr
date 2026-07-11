package com.decidr.app.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DecisionRepository {
    private final DecisionDao mDao;
    private final ExecutorService mExecutor;

    public DecisionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        mDao = db.decisionDao();
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<DecisionEntry>> getAllDecisions() {
        return mDao.getAllDecisions();
    }

    // NEW: Search method
    public LiveData<List<DecisionEntry>> searchDecisions(String query) {
        return mDao.searchDecisions(query);
    }

    public void insert(DecisionEntry decision) {
        mExecutor.execute(() -> mDao.insert(decision));
    }

    public void deleteAll() {
        mExecutor.execute(mDao::deleteAll);
    }
}