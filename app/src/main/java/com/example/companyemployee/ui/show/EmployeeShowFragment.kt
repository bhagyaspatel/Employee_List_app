package com.example.companyemployee.ui.show

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.companyemployee.R
import com.example.companyemployee.data.Employee
import com.example.companyemployee.data.Gender
import com.example.companyemployee.data.Role
import kotlinx.android.synthetic.main.fragment_employee_show.*

class EmployeeShowFragment : Fragment() {

    private lateinit var viewModel : EmployeeShowViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(EmployeeShowViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_show, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        employee_photoShow.setImageResource(R.drawable.blank_photo)
        employee_photoShow.tag = ""

        val id = EmployeeShowFragmentArgs.fromBundle(requireArguments()).id
        viewModel.setEmployeeId(id)

        viewModel.employee.observe(viewLifecycleOwner) {
            if(it != null)
            setData(it)
        }
    }

    private fun setData(employee : Employee) {
        with(employee.photo){
            if(employee.photo.isNotEmpty()) {
                employee_photoShow.setImageURI(Uri.parse(employee.photo))
                employee_photoShow.tag = employee.photo
            }else{
                Log.d ("ShowFrag", "photo not available")
                employee_photoShow.setImageResource(R.drawable.blank_photo)
                employee_photoShow.tag = ""
            }
        }

        collapsing_toolbar.title = employee.name

        employee_ageShow.text = "${employee.age - 18} years"

        run loop@{
            Role.values().forEach {
                if (it.ordinal == employee.role){
                    employee_RoleShow.text = it.name
                }
            }
        }

        when(employee.gender) {
            Gender.Male.ordinal -> {
               employee_GenderShow.text = Gender.Male.name
            }
            Gender.Female.ordinal -> {
                employee_GenderShow.text = Gender.Female.name
            }
            Gender.Female.ordinal -> {
                employee_GenderShow.text = Gender.Other.name
            }
        }

        employee_responsibilityShow.text = employee.responsibility
        employee_educationShow.text = employee.education
        employee_experienceShow.text = employee.experience
        if (employee.phone > 0){
            employee_phoneTextShow.text = employee.photo
        }
        employee_emailTextShow.text = employee.email
        employee_addressTextShow.text = employee.address
    }
}