package com.example.companyemployee.data.list

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.companyemployee.data.Employee

@Dao
interface EmployeeListDao {
    @Query ("SELECT * FROM employee ORDER BY name")
    fun getEmployee () : LiveData<List<Employee>>

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmployee (employee : List <Employee>)

    @Query ("SELECT * FROM employee ORDER BY name")
    fun getEmployeeList () : List<Employee>
}