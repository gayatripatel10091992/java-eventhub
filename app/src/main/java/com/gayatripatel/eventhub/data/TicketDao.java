package com.gayatripatel.eventhub.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface TicketDao {
    @Query("SELECT * FROM tickets ORDER BY startsAtMillis ASC")
    LiveData<List<TicketEntity>> observeAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TicketEntity ticket);
}
