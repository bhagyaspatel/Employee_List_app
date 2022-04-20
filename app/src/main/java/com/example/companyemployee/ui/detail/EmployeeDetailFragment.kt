package com.example.companyemployee.ui.detail

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.companyemployee.BuildConfig
import com.example.companyemployee.R
import com.example.companyemployee.data.Employee
import com.example.companyemployee.data.Gender
import com.example.companyemployee.data.Role
import com.example.companyemployee.ui.createFile
import com.example.companyemployee.ui.detail.EmployeeDetailViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_employee_detail.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.NumberFormatException

const val PERMISSION_REQUEST_CAMERA = 0
const val CAMERA_PHOTO_REQUEST = 1
const val GALLERY_PHOTO_REQUEST = 2

class EmployeeDetailFragment : Fragment() {

    private lateinit var viewModel : EmployeeDetailViewModel
    private var selectedPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(EmployeeDetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val roles = mutableListOf<String>()
        Role.values().forEach {
            roles.add(it.name)
        }
        val arrayAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, roles)
        employee_role2.adapter = arrayAdapter

        val ages = mutableListOf<Int>()
        for (i in 18 until 81) {
            ages.add(i)
        }
        employee_age2.adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, ages)

        val id = EmployeeDetailFragmentArgs.fromBundle(requireArguments()).id
        viewModel.setEmployeeId(id)

        viewModel.employee.observe(viewLifecycleOwner, Observer {
            it?.let { setData(it) }
        })

        save_employee.setOnClickListener {
            saveEmployee()
        }

        delete_employee.setOnClickListener {
            deleteEmployee()
        }

        //reset photo
//        employee_photo2.setOnClickListener {
//            employee_photo2.setImageResource(R.drawable.blank_photo)
            employee_photo2.tag = ""
//        }

        cameraBtn.setOnClickListener{
            clickPhotoAfterPermission(it)
        }

        galleryBtn.setOnClickListener{
            pickPhoto(it)
        }
    }

    private fun clickPhotoAfterPermission(view: View) {
//        we see if permission is already granted or not if not we ask permission else we click photo
        if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.CAMERA ) ==
            PackageManager.PERMISSION_GRANTED)
            clickPhoto()
        else
            requestCameraPermission(view)
    }

    private fun requestCameraPermission(view: View) {
//        if app ask for permission and user denies it if (...==true) and we show the snack to give more
//        details about the permission

        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.CAMERA)){
            val snack = Snackbar.make(view, "We need your permission to take a photo. Please provide permission when asked for."
            ,Snackbar.LENGTH_INDEFINITE)
            snack.setAction("OK", View.OnClickListener {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CAMERA)
            })
            snack.show()
        }else{
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CAMERA)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CAMERA){
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                clickPhoto()
            }else{
                Toast.makeText(requireActivity(), "Permission Denied For Using Camera", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun clickPhoto() {
        //storing the photo in our app's external storage
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile : File? = try{
                    createFile(requireActivity(), Environment.DIRECTORY_PICTURES, "jpg")
                }catch (ex : IOException){
                    Toast.makeText(requireActivity(), "Error occurred while creating File. ${ex.message}",
                    Toast.LENGTH_SHORT).show()
                    null
                }
//providing the uri to whatever app that is going to take the photo so that they can save the photo
//in that perticular uri
                photoFile?.also{
                    selectedPhotoPath = it.absolutePath
                    val photoUri : Uri = FileProvider.getUriForFile(
                        requireActivity(),
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        it
                    )
                    //".fileprovider" shld be matching to authorities in our manifest
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, CAMERA_PHOTO_REQUEST)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_OK){
            when(requestCode){

                //here we are creating uri from the file
                CAMERA_PHOTO_REQUEST -> {
                    val uri = Uri.fromFile(File(selectedPhotoPath))
                    employee_photo2.setImageURI(uri)
                    employee_photo2.tag = uri.toString()
                }

                //here we are making a file using the uri
                GALLERY_PHOTO_REQUEST -> {
                    data?.data?.also{uri ->
                        val photoFile : File ? = try{
                            createFile(requireActivity(), Environment.DIRECTORY_PICTURES, "jpg")
                        }catch (ex : IOException){
                            Toast.makeText(requireActivity(), "Error occurred while creating a file.", Toast.LENGTH_SHORT).show()
                            null
                        }
                        photoFile?.also {
                            //here we are copying the selected file (photo:input stream) to our file (photoFile:output stream)
                            try{
                                val resolver = requireActivity().applicationContext.contentResolver
                                resolver.openInputStream(uri).use { stream ->
                                    val output = FileOutputStream(photoFile)
                                    stream!!.copyTo(output)
                                }
                                val fileUri = Uri.fromFile(photoFile)
                                employee_photo2.setImageURI(fileUri)
                                employee_photo2.tag = fileUri.toString()
                            }catch (ex : FileNotFoundException){
                                ex.printStackTrace()
                            }catch (ex : IOException){
                                ex.printStackTrace()
                            }
                        }
                    }
                }

            }
        }
    }


    private fun pickPhoto(view: View) {
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPhotoIntent.resolveActivity(requireActivity().packageManager).also{
            startActivityForResult(pickPhotoIntent, GALLERY_PHOTO_REQUEST)
        }
    }


    private fun deleteEmployee() {
        viewModel.deleteEmployee()
        requireActivity().onBackPressed()
    }

    private fun saveEmployee() {
        val name = employee_name2.text.toString()
        val age = employee_age2.selectedItemPosition + 18
        val role = employee_role2.selectedItemPosition

        val selectedStatusBtn = gender_group.findViewById<RadioButton>(gender_group.checkedRadioButtonId)
        var gender = Gender.Other.ordinal
        if (selectedStatusBtn.text == Gender.Male.name)
            gender = Gender.Male.ordinal
        else if (selectedStatusBtn.text == Gender.Male.name)
            gender = Gender.Female.ordinal

        val photo = employee_photo2.tag as String

        val responsibility = employee_responsibilityEdit.text.toString()
        val education = employee_educationEdit.text.toString()
        val experience = employee_experianceEdit.text.toString()
        val email = employee_email.text.toString()
        val address = employee_address.text.toString()
        
        //doing validation
        if (name.isBlank()){
            Toast.makeText(requireActivity(), "Please Enter Name of Employee", Toast.LENGTH_SHORT).show()
            return
        }

        if (experience.isBlank()){
            Toast.makeText(requireActivity(), "Please Enter Experience of Employee", Toast.LENGTH_SHORT).show()
            return
        }

        if (responsibility.isBlank()){
            Toast.makeText(requireActivity(), "Please Enter Responsibilities of Employee", Toast.LENGTH_SHORT).show()
            return
        }

        if (education.isBlank()){
            Toast.makeText(requireActivity(), "Please Enter Education of Employee", Toast.LENGTH_SHORT).show()
            return
        }

        val phone : Long
        try{
            phone = employee_phone.text.toString().toLong()
        }catch (ex : NumberFormatException){
            Toast.makeText(requireActivity(), "Please Enter Valid Phone Number of Employee", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isBlank()){
            Toast.makeText(requireActivity(), "Please Enter Email-id of Employee", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.indexOf("@") < 0){
            Toast.makeText(requireActivity(), "Please Enter Valid Email-id of Employee", Toast.LENGTH_SHORT).show()
            return
        }

        if (address.isBlank()){
            Toast.makeText(requireActivity(), "Please Enter Address of Employee", Toast.LENGTH_SHORT).show()
            return
        }

        val employee = Employee(viewModel.employeeID.value!!, name, role, age, gender, photo,
            responsibility, experience, education, phone, email, address)
//        val employee = Employee(viewModel.employeeID.value!!, name, role, age, gender, photo)

        viewModel.saveEmployee(employee)

        requireActivity().onBackPressed()
    }

    private fun setData(employee : Employee) {
        with(employee.photo){
            if(employee.photo.isNotEmpty()) {
                employee_photo2.setImageURI(Uri.parse(employee.photo))
                employee_photo2.tag = employee.photo
            }else{
                Log.d ("DetailFrag", "photo not available")
                employee_photo2.setImageResource(R.drawable.blank_photo)
                employee_photo2.tag = ""
            }
        }

        employee_name2.setText(employee.name)

        employee_role2.setSelection(employee.role)
        employee_name2.setSelection(employee.age-18)

        when(employee.gender) {
            Gender.Male.ordinal ->{
                gender_male.isChecked = true
            }
            Gender.Female.ordinal ->{
                gender_female.isChecked = true
            }
            Gender.Female.ordinal ->{
                gender_other.isChecked = true
            }
        }

//        employee_educationEdit.setText(employee.education)
//        employee_responsibilityEdit.setText(employee.responsibility)
//        employee_experianceEdit.setText(employee.experience)
//        if (employee.phone > 0 )
//            employee_phone.setText(employee.phone.toString())
//        employee_email.setText(employee.email)
//        employee_address.setText(employee.address)
    }

}