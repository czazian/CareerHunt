package com.example.careerhunt.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.careerhunt.dao.CompanyDAO

import com.example.careerhunt.data.*
import java.time.Instant


<<<<<<< HEAD
@Database(entities = [Personal::class], version = 1, exportSchema = false)
=======
@Database(entities = [Job::class, Company::class, Apply_Job::class, Notification::class], version = 1, exportSchema = false)
>>>>>>> 762d4ad86aecfa2684b832ca2223ced83c5b0176
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