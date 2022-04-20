package com.example.companyemployee.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Gender{
    Male,
    Female,
    Other
}

enum class Role{
    Manager,
    Staff,
    Worker
}
@Entity(tableName = "employee")
data class Employee (@PrimaryKey(autoGenerate = true) val id : Long ,
                     val name : String,
                     val role : Int,
                     val age : Int,
                     val gender : Int,
                     val photo : String,
                     val responsibility : String,
                     val experience : String,
                     val education : String,
                     val phone : Long,
                     val email : String,
                     val address : String
                     )

