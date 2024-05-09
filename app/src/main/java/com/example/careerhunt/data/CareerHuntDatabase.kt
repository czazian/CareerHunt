package com.example.careerhunt.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Company::class], version = 1, exportSchema = false)
abstract class CareerHuntDatabase : RoomDatabase() {

    //dao
    abstract fun personalDao() : PersonalDAO

    companion object {
        @Volatile
        private var INSTANT: CareerHuntDatabase? = null
        fun getDatabse(context: Context): CareerHuntDatabase {
            val tempInstance = INSTANT
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CareerHuntDatabase::class.java,
                    "careerhunt_database"
                ).build()
                INSTANT = instance
                return instance
            }
        }

        //add new field, not nessasary first
        //private val migration1to2 = object : Migration(1, 2) {
        //    override fun migrate(database: SupportSQLiteDatabase) {
                // Perform migration SQL statements here, such as adding a new column
        //        database.execSQL("CREATE TABLE IF NOT EXISTS alumni_community_comment (id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT)")
         //   }
        //}
    }

}