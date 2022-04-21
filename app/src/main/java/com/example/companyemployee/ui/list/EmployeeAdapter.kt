package com.example.companyemployee.ui.list

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.companyemployee.R
import com.example.companyemployee.data.Employee
import com.example.companyemployee.data.Gender
import com.example.companyemployee.data.Role
import kotlinx.android.extensions.LayoutContainer

class EmployeeAdapter(list : MutableList<Employee> , private val listener : (Boolean, Long) -> Unit) : RecyclerView.Adapter<EmployeeAdapter.employeeViewHolder>() {

    val employeeList = list

    inner class employeeViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer{
        val employeePhoto = containerView.findViewById<ImageView>(R.id.employee_photo)
        val employeeAge = containerView.findViewById<TextView>(R.id.employee_age)
        val employeeName = containerView.findViewById<TextView>(R.id.employee_name)
        val employeeGender = containerView.findViewById<TextView>(R.id.employee_gender)
        val employeeRole = containerView.findViewById<TextView>(R.id.employee_role)
        val editEmployeeBtn = containerView.findViewById<ImageButton>(R.id.edit_employeeBtn)

        val responsibility = containerView.findViewById<TextView>(R.id.employee_responsibilityTextLand)

        init{
            itemView.setOnClickListener {
                listener.invoke (true, employeeList[absoluteAdapterPosition].id)
            }
            editEmployeeBtn.setOnClickListener{
                listener.invoke(false, employeeList[absoluteAdapterPosition].id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): employeeViewHolder {
        val itemLayout = LayoutInflater.from (parent.context).inflate(R.layout.list_item, parent, false)
        return employeeViewHolder(itemLayout)
    }

    override fun onBindViewHolder(holder: employeeViewHolder, position: Int) {
        val currentItem = employeeList[position]
        holder.employeeName.text = currentItem.name
        holder.employeeAge.text = currentItem.age.toString()
        holder.employeeGender.text = Gender.values()[currentItem.gender].name
        holder.employeeRole.text = Role.values()[currentItem.role].name

        with(currentItem.photo){
            if(isNotEmpty()){
                holder.employeePhoto.setImageURI(Uri.parse(this))
            }
            else{
//                Log.d ("Adapter", "Photo uri not taken")
                holder.employeePhoto.setImageResource(R.drawable.blank_photo)
            }
        }

        holder.responsibility?.text = currentItem.responsibility
    }

    override fun getItemCount(): Int {
        return employeeList.size
    }

    fun removeItem(position :Int){
        employeeList.removeAt(position) //to use removeAt function we need mutable list
        notifyItemRemoved(position)
    }
}