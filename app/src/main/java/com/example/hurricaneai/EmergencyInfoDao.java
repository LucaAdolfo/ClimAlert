package com.example.hurricaneai;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EmergencyInfoDao {

    @Insert
    void insert(EmergencyInfo info);

    @Query("SELECT * FROM emergency_info WHERE content LIKE :keyword")
    List<EmergencyInfo> searchByKeyword(String keyword);

    @Query("SELECT * FROM emergency_info WHERE category = :category")
    List<EmergencyInfo> getByCategory(String category);

    @Query("DELETE FROM emergency_info WHERE timestamp < :oldTimestamp")
    void deleteOldEntries(long oldTimestamp);

    @Query("SELECT COUNT(*) FROM emergency_info")
    int getCount();

    @Query("DELETE FROM emergency_info")
    void deleteAll();
}
