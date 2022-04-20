package com.example.companyemployee.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.companyemployee.data.detail.EmployeeDetailDao
import com.example.companyemployee.data.list.EmployeeListDao
import com.example.companyemployee.data.show.EmployeeShowDao

@Database (entities = [Employee::class], version = 2, exportSchema = false)
abstract class EmployeeDatabase : RoomDatabase(){
    abstract fun employeeListDao() : EmployeeListDao
    abstract fun employeeDetailDao() : EmployeeDetailDao
    abstract fun employeeShowDao() : EmployeeShowDao


    // If we put typeConverter on a Database, all Daos and Entities in that database will be able to use it.

    companion object{
        @Volatile
        private var instance : EmployeeDatabase? = null

        fun getDataBase(context: Context) = instance ?: synchronized(this){
            Room.databaseBuilder(
                context.applicationContext,
                EmployeeDatabase::class.java,
                "movie_database"
            ).fallbackToDestructiveMigration().build().also { instance = it }
        }
    }
}