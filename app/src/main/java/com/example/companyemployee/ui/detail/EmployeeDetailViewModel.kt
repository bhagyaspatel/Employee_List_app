package com.example.companyemployee.ui.detail

import android.app.Application
import androidx.lifecycle.*
import com.example.companyemployee.data.Employee
import com.example.companyemployee.data.detail.EmployeeDetailRepository
import kotlinx.coroutines.launch

class EmployeeDetailViewModel (application: Application) : AndroidViewModel(application) {

    private val repo : EmployeeDetailRepository = EmployeeDetailRepository(application)

    private val _employeeID = MutableLiveData<Long>(0)
    val employeeID : LiveData<Long>
        get() = _employeeID

    //observe employee is of type Employee but _employeeID is of type Long (by Transformations.switchMap())
    val employee : LiveData<Employee> = Transformations
        .switchMap(_employeeID) { id ->
            repo.getEmployee(id)
        }

    fun setEmployeeId (id : Long){
        if (_employeeID.value != id)
            _employeeID.value = id
    }

//jo bhi humare employee ko observe karega use Employee ke through _employeeID milega
    fun saveEmployee (employee: Employee){
        viewModelScope.launch {
            if (_employeeID.value == 0L)
                _employeeID.value = repo.insertEmployee(employee)
            else
                repo.updateEmployee(employee)
        }
    }

    fun deleteEmployee (){
        viewModelScope.launch {
            employee.value?.let {
                repo.deleteEmployee(it)
            }
        }
    }
}