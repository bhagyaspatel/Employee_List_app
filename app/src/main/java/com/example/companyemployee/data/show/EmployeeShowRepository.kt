package com.example.companyemployee.data.show

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.companyemployee.data.Employee
import com.example.companyemployee.data.EmployeeDatabase
import com.example.companyemployee.data.list.EmployeeListDao

class EmployeeShowRepository (context : Application) {
    private val employeeShowDao : EmployeeShowDao = EmployeeDatabase.getDataBase(context).employeeShowDao()

    fun getEmployees(id : Long) : LiveData<Employee> {
        return employeeShowDao.getEmployee(id)
    }
}