package com.example.companyemployee.data.detail

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.companyemployee.data.Employee
import com.example.companyemployee.data.EmployeeDatabase

class EmployeeDetailRepository(context : Application) {

    private val employeeDetailDao : EmployeeDetailDao = EmployeeDatabase.getDataBase(context).employeeDetailDao()

    fun getEmployee (id : Long) : LiveData<Employee>{
        return employeeDetailDao.getEmployee(id)
    }

    suspend fun insertEmployee (employee: Employee) : Long{
        return employeeDetailDao.insertEmployee(employee)
    }

    suspend fun updateEmployee (employee: Employee) {
        return employeeDetailDao.updateEmployee(employee)
    }

    suspend fun deleteEmployee (employee: Employee){
        return employeeDetailDao.deleteEmployee(employee)
    }
}