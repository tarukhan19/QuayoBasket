package com.project.quayobasket.RoomPersistanceLibrary;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {QuayoBasketDTO.class}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}
