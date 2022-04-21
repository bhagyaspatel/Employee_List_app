package com.example.companyemployee.data.list

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.companyemployee.data.Employee
import com.example.companyemployee.data.EmployeeDatabase

class EmployeeListRepository (context : Application) {
    private val employeeListDao : EmployeeListDao = EmployeeDatabase.getDataBase(context).employeeListDao()

    fun getEmployees() : LiveData<MutableList<Employee>>{
        return employeeListDao.getEmployee()
    }

    suspend fun insertEmployee (employee : List <Employee>){
        return employeeListDao.insertEmployee(employee)
    }

    suspend fun getEmployeeList () : List<Employee>{
        return employeeListDao.getEmployeeList()
    }

}