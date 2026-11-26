package com.gayatripatel.eventhub.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {TicketEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract TicketDao ticketDao();

    public static AppDatabase get(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "eventhub.db").build();
                }
            }
        }
        return instance;
    }
}
