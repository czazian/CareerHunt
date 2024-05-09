package com.example.careerhunt.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.careerhunt.dao.CompanyDAO

import com.example.careerhunt.data.*
import java.time.Instant


@Database(entities = [Personal::class], version = 1, exportSchema = false)
abstract class CareerHuntDatabase : RoomDatabase() {

    abstract fun companyDAO(): CompanyDAO

    companion object {
        @Volatile
        private var INSTANT: CareerHuntDatabase? = null
        fun getDatabase(context: Context): CareerHuntDatabase {
            val tempInstance = INSTANT
            if(tempInstance != null){
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CareerHuntDatabase::class.java,
                    "CareerHuntDatabase"
                ).build()
                INSTANT = instance
                return instance
            }
        }
    }

}