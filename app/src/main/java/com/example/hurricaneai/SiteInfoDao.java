package com.example.hurricaneai;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
import com.example.climalert.R;


@Dao
public interface SiteInfoDao {

    @Query("SELECT * FROM sites")
    List<SiteInfo> getAll();

    @Insert
    void insertAll(SiteInfo... sites);

    @Query("DELETE FROM sites")
    void deleteAll();

    @Query("DELETE FROM sites WHERE id = :siteId")
    void deleteById(int siteId);
    
    @Query("SELECT COUNT(*) FROM sites")
    int getCount();
}
