package com.example.companyemployee.ui.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.companyemployee.R
import com.example.companyemployee.data.Employee
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.fragment_employee_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*

const val READ_FILE_REQUEST = 1
const val CREATE_FILE_REQUEST = 2

class EmployeeListFragment : Fragment() {

    private lateinit var viewModel : EmployeeListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true) //to tell the Android framework that we are going show the menu
        viewModel = ViewModelProvider(this).get(EmployeeListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigationDrawer()

        val swipeHandler = object : EmployeeListSwipeToDelete(requireActivity()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapterX = employee_list.adapter as EmployeeAdapter
                adapterX.removeItem(viewHolder.absoluteAdapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(employee_list)

        viewModel.employees.observe(viewLifecycleOwner){
            if(it.isEmpty())
                noDataMSg.visibility = View.VISIBLE
            else
                noDataMSg.visibility = View.INVISIBLE

            with(employee_list){
                layoutManager = LinearLayoutManager(activity)
                adapter = EmployeeAdapter(it){show, id ->
                    if (show){
                        findNavController().navigate(
                            EmployeeListFragmentDirections.actionEmployeeListFragmentToEmployeeShowFragment(id))
                    }
                    else{
                        findNavController().navigate(
                            EmployeeListFragmentDirections.actionEmployeeListFragmentToEmployeeDetailFragment(id))
                    }
                }
            }
        }

//        toolbar.inflateMenu(R.menu.list_menu) //toolbar is id of our toolbar in xml
//        toolbar.setOnMenuItemClickListener{
//            handleMenuItem (it)
//        }

        add_employee.setOnClickListener{
            findNavController().navigate(
                EmployeeListFragmentDirections.actionEmployeeListFragmentToEmployeeDetailFragment(0)
            )
        }
    }

    private fun setupNavigationDrawer() {
        val navController = Navigation.findNavController(requireActivity()!!, R.id.fragmentContainerView)
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout) //from activity_xml
        val navigationView = requireActivity().findViewById<NavigationView>(R.id.navigation_view)

        NavigationUI.setupWithNavController(toolbar,navController, drawerLayout)
        navigationView.setupWithNavController(navController)

        navigationView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawers()

            when(it.itemId){
                R.id.add -> {
                    findNavController().navigate(
                        EmployeeListFragmentDirections.actionEmployeeListFragmentToEmployeeDetailFragment(0)
                    )
                    true
                }
                R.id.contactPage -> {findNavController().navigate(
                    EmployeeListFragmentDirections.actionEmployeeListFragmentToContactFragment())
                    true
                }

                R.id.aboutPage -> {findNavController().navigate(
                    EmployeeListFragmentDirections.actionEmployeeListFragmentToAboutFragment2())
                    true
                }
                R.id.exportEmp -> {
                    GlobalScope.launch {
                        exportEmployees()
                    }
                    true
                }
                R.id.importEmp -> {
                    importEmployees()
                    true
                }
                else -> false
            }
        }
    }

    private fun handleMenuItem(item : MenuItem) : Boolean { ///****..mine
        return when(item.itemId) {
            R.id.menu_import_data -> {
                importEmployees()
                true
            }
            R.id.menu_export_data -> {
                GlobalScope.launch {
                    exportEmployees()
                }
                true
            }
            else -> false
        }
    }

//    private fun handleMenuItem(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.menu_export_data -> {
//                GlobalScope.launch {
//                    exportEmployees()
//                }
//                true
//            }
//            R.id.menu_import_data -> {
//                Intent(Intent.ACTION_GET_CONTENT).also { readFileIntent ->
//                    readFileIntent.addCategory(Intent.CATEGORY_OPENABLE)
//                    readFileIntent.type = "text/*"
//                    readFileIntent.resolveActivity(requireActivity().packageManager)?.also {
//                        startActivityForResult(readFileIntent, READ_FILE_REQUEST)
//                    }
//                }
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == Activity.RESULT_OK) {
//            when (requestCode) {
//                READ_FILE_REQUEST -> {
//                    GlobalScope.launch{
//                        val resolver = requireActivity().applicationContext.contentResolver
//                        resolver.openInputStream(data!!.data!!).use { stream ->
//                            stream?.let{
//                                withContext(Dispatchers.IO) {
//                                    parseCSVFile(stream)
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }


//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        return inflater.inflate(R.menu.list_menu, menu)
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when(item.itemId){
//            R.id.menu_import_data -> {
//                importEmployees()
//                true
//            }
//            R.id.menu_export_data -> {
//                GlobalScope.launch {
//                    exportEmployees()
//                }
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    private suspend fun exportEmployees() {
        //using storage acceess framework : minSDk version shld be >= 19
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, "employee_list.csv") //employee_list.csv is the name of our file while exporting
            //storage acceess framework also allows us to change the name
        }
        startActivityForResult(intent, CREATE_FILE_REQUEST)
    }

//    private suspend fun exportEmployees(){
//        var csvFile: File? = null
//        withContext(Dispatchers.IO) {
//            csvFile = createFile(requireActivity(), "Documents", "csv")
//            csvFile?.printWriter()?.use { out ->
//                val employees = viewModel.GetEmployeeList()
//                if(employees.isNotEmpty()){
//                    employees.forEach{
//                        out.println(it.name + "," + it.role + "," + it.age + "," + it.gender)
//                    }
//                }
//            }
//        }
//        withContext(Dispatchers.Main){
//            csvFile?.let{
//                val uri = FileProvider.getUriForFile(
//                    requireActivity(), BuildConfig.APPLICATION_ID + ".fileprovider",
//                    it)
//                launchFile(uri, "csv")
//            }
//        }
//    }

//    private suspend fun parseCSVFile(stream: InputStream){
//        val employees = mutableListOf<Employee>()
//
//        BufferedReader(InputStreamReader(stream)).forEachLine {
//            val tokens = it.split(",")
//            employees.add(Employee(id = 0, name = tokens[0], role = tokens[1].toInt(),
//                age = tokens[2].toInt(), gender = tokens[3].toInt(), photo = "", responsibility = "",
//                experience = "", education = "", phone = 0, email = "", address = ""))
//        }
//
//        if(employees.isNotEmpty()){
//            viewModel.InsertEmployee(employees)
//        }
//    }

    private fun importEmployees() {

        //for minSDK version >= 19 : we are using storage acceess framework
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
        }
        intent.resolveActivity(requireActivity().packageManager)?.also {
            startActivityForResult(intent, READ_FILE_REQUEST)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                READ_FILE_REQUEST -> {
                    GlobalScope.launch {
                        data?.data.also { uri ->
                            readFromFile(uri)
                        }
                    }
                }

                CREATE_FILE_REQUEST -> {
                    data?.data?.also {uri ->
                        GlobalScope.launch {
                            if (writeToFile(uri)){
                                withContext(Dispatchers.Main){
                                    Toast.makeText(requireActivity(),"FIle Exported Successfully", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    private suspend fun writeToFile(uri: Uri): Boolean {
        try{
            requireActivity().applicationContext.contentResolver.openFileDescriptor(uri,"w")?.use{pfd->
                FileOutputStream(pfd.fileDescriptor).use{ outStream ->
                    val employee = viewModel.GetEmployeeList()
                    if (employee.isNotEmpty()){
                        employee.forEach{
//                            outStream.write((it.name+","+it.role+","+it.age+","+it.gender+","+it.phone.toString()+","+it.email+"\n").toByteArray())
                            outStream.write((it.name+","+it.role+","+it.age+","+it.gender+"\n").toByteArray())
                        }
                    }
                }
            }
        }catch (e : FileNotFoundException){
            e.printStackTrace()
            return false
        }catch (e : IOException){
            e.printStackTrace()
            return false
        }
        return true
    }

    private suspend fun readFromFile(uri : Uri?) {
        try {
            requireActivity().applicationContext.contentResolver.openFileDescriptor(uri!!, "r")?.use {
                withContext(Dispatchers.IO){
                    FileInputStream (it.fileDescriptor).use{
                        parseCSVFile(it)
                    }
                }
            }
        }catch (ex : FileNotFoundException){
            ex.printStackTrace()
        }catch (ex : IOException){
            ex.printStackTrace()
        }
    }

    private suspend fun parseCSVFile(stream : FileInputStream) {
        val emploee =  mutableListOf<Employee>()

        BufferedReader (InputStreamReader(stream)).forEachLine {
            val token = it.split(",")
//            emploee.add(Employee(id = 0, name = token[0], role = token[1].toInt(),
//            age = token[2].toInt(), gender = token[3].toInt(), photo = "", responsibility = "", experience = "", education = "", phone = token[4].toLong(), email = token[5], address = "" ))
            emploee.add(Employee(id = 0, name = token[0], role = token[1].toInt(),
                age = token[2].toInt(), gender = token[3].toInt(), photo = "", responsibility = "",
                experience = "", education = "", phone = 0, email = "", address = ""))
        }

        if (emploee.isNotEmpty()){
            viewModel.InsertEmployee(emploee)
        }
    }
}