package com.decidr.app.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "decisions")
public class DecisionEntry {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String prompt;
    public String result;
    public long timestamp;

    public DecisionEntry(String prompt, String result, long timestamp) {
        this.prompt = prompt;
        this.result = result;
        this.timestamp = timestamp;
    }
}