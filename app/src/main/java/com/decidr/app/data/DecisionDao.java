package com.decidr.app.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface DecisionDao {
    @Insert
    void insert(DecisionEntry decision);

    @Query("SELECT * FROM decisions ORDER BY timestamp DESC")
    LiveData<List<DecisionEntry>> getAllDecisions();

    // NEW: Search query (case-insensitive)
    @Query("SELECT * FROM decisions WHERE prompt LIKE '%' || :query || '%' OR result LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    LiveData<List<DecisionEntry>> searchDecisions(String query);

    @Query("DELETE FROM decisions")
    void deleteAll();
}