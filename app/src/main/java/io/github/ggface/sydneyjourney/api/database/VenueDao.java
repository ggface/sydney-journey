package io.github.ggface.sydneyjourney.api.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.github.ggface.sydneyjourney.api.pojo.Venue;

/**
 * @author Ivan Novikov on 2018-11-01.
 */
@Dao
public interface VenueDao {

    @Query("SELECT * FROM venues")
    List<Venue> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Venue entity);

    @Update
    void update(Venue entity);

    @Delete
    void delete(Venue entity);
}