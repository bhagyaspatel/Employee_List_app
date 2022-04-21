package com.example.companyemployee.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.companyemployee.data.Employee
import com.example.companyemployee.data.list.EmployeeListRepository

class EmployeeListViewModel(application: Application) : AndroidViewModel(application) {
    private val repo : EmployeeListRepository = EmployeeListRepository(application)

    val employees : LiveData<MutableList<Employee>> = repo.getEmployees()

    suspend fun InsertEmployee (employee : List <Employee>){
        return repo.insertEmployee(employee)
    }

    suspend fun GetEmployeeList () : List<Employee>{
        return repo.getEmployeeList()
    }
}