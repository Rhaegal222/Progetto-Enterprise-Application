package com.example.frontend.model.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [User::class], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase(){
    abstract fun userDao(): UserDao //define the DAOs as abstract function


    companion object{
        @Volatile
        private var instance: AppDatabase?= null
        fun getInstance(context: Context): AppDatabase{
            //guarantee that the database is initialized once
            synchronized(this){
                var localInstance = instance
                if(localInstance == null){
                    localInstance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "database"
                    ).fallbackToDestructiveMigration().build()
                    instance = localInstance
                }
                return localInstance

            }
        }
    }



}