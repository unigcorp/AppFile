package com.example.appfile

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.appfile.adapter.AdapterFile
import com.example.appfile.interfaz.Interfaz
import com.example.appfile.model.File
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity(), Interfaz {

    private lateinit var recyclerview:RecyclerView
    private lateinit var fileList:ArrayList<File>
    private lateinit var fileNameList:ArrayList<File>
    private  lateinit var sharedPreferences: SharedPreferences
    private lateinit var floatingActionButton:FloatingActionButton
    private var id_archivox:String=""


    private lateinit var editTextCorreo: EditText
    private lateinit var btn_envia: Button
    private lateinit var btn_cancela: Button
    private lateinit var spinner:Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("datosUsuario", Context.MODE_PRIVATE);
        recyclerview = findViewById(R.id.recyclerView)
        floatingActionButton = findViewById(R.id.floatingActionButton)
        recyclerview.layoutManager = GridLayoutManager(this,1)
        fileList = arrayListOf<File>()
        fileNameList = arrayListOf()

        floatingActionButton.setOnClickListener {
            openDilaogAdd()
        }
        listarArchivos()
        listarNombresArchivos()
    }

    private fun openDilaogAdd() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_file)
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val heigth = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window?.setLayout(width,heigth)


        btn_envia = dialog.findViewById(R.id.btn_envia)
        btn_cancela = dialog.findViewById(R.id.btn_cancela)
        spinner = dialog.findViewById(R.id.spinner)

        val adaptador = ArrayAdapter(this,android.R.layout.simple_list_item_1,fileNameList)
        spinner.adapter = adaptador

        spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Toast.makeText(applicationContext, "id archivo "+fileNameList.get(position).id+" id_usuario "+getID(), Toast.LENGTH_SHORT).show()
                id_archivox = fileNameList.get(position).id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        btn_cancela.setOnClickListener {
            dialog.dismiss()
        }
        btn_envia.setOnClickListener {
            enviaDatosAdicionar(id_archivox,getID().toString(),dialog)
        }



        dialog.show()
    }

    private fun enviaDatosAdicionar(idArchivox: String, id_usuario: String, dialog: Dialog) {
        val queque = Volley.newRequestQueue(this)
        val stringRequest = object :StringRequest(Request.Method.POST,Contants.ADRRESS_IP+"file/add",Response.Listener {
                response ->

            Toast.makeText(this, "LOS DATOS SE ENVIARON CORRECTAMENTE", Toast.LENGTH_SHORT).show()
            listarArchivos()
            dialog.dismiss()
        },Response.ErrorListener {
                error ->
            Toast.makeText(this, "Error "+error.message, Toast.LENGTH_SHORT).show()
        })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val parametros = HashMap<String,String>()
                parametros.put("Authorization","Token "+getToken().toString())
                return parametros
            }

            override fun getParams(): MutableMap<String, String>? {
                val parametros = HashMap<String,String>()
                parametros.put("id_usuario",id_usuario)
                parametros.put("id_archivo",idArchivox)
                return parametros
            }

        }
        queque.add(stringRequest)
    }

    private fun listarArchivos() {
        val queque = Volley.newRequestQueue(this)
        val stringRequest = object :StringRequest(Request.Method.POST,"http://192.168.0.15:8080/file/request",Response.Listener {
            response ->

                  try {
                      fileList.clear()
                      val datos = JSONObject(response)
                      val arrays = datos.getJSONArray("result")
                      for (i in 0 until arrays.length()){
                          val item = arrays.getJSONObject(i)
                          fileList.add(File(item.getString("id_usuario"),item.getString("nombre_arc"),item.getString("fecha"),item.getString("status"),item.getString("id_sol")))
                          Toast.makeText(this, "====>"+item.getString("id_usuario"), Toast.LENGTH_SHORT).show()
                      }

                      setupFile(fileList)
                }catch (e:JSONException){
                      Toast.makeText(this, "Error "+e.message, Toast.LENGTH_SHORT).show()
                }
        },Response.ErrorListener {
            error ->
            Toast.makeText(this, "Error "+error.message, Toast.LENGTH_SHORT).show()
        })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val parametros = HashMap<String,String>()
                parametros.put("Authorization","Token "+getToken().toString())
                return parametros
            }

            override fun getParams(): MutableMap<String, String>? {
                val parametros = HashMap<String,String>()
                parametros.put("id_usuario",getID().toString())
                return parametros
            }
        }
        queque.add(stringRequest)
    }

    private fun setupFile(fileList: ArrayList<File>) {
        recyclerview.adapter = AdapterFile(this,this,fileList)
    }
    private fun getToken():String?{
        val token = sharedPreferences.getString("TOKEN","")
        return token
    }
    private fun getID():String?{
        val id = sharedPreferences.getString("ID","")
        return id
    }

    override fun onClick(posicion: Int, nombre: String,id_usuario:String,id_solicitud:String) {
        //Toast.makeText(this, "hola desde main "+nombre, Toast.LENGTH_SHORT).show()
        openDialod(nombre,id_usuario,id_solicitud)
    }
    private fun openDialod(
        nombre: String,
        id_usuario: String,
        id_solicitud: String
    ) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_file)
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val heigth = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window?.setLayout(width,heigth)


        btn_envia = dialog.findViewById(R.id.btn_envia)
        btn_cancela = dialog.findViewById(R.id.btn_cancela)
        spinner = dialog.findViewById(R.id.spinner)

        val adaptador = ArrayAdapter(this,android.R.layout.simple_list_item_1,fileNameList)
        spinner.adapter = adaptador

        spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Toast.makeText(applicationContext, "id "+fileNameList.get(position).id+" id sol "+id_solicitud+" id_usuario "+id_usuario, Toast.LENGTH_SHORT).show()
                id_archivox = fileNameList.get(position).id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        btn_cancela.setOnClickListener {
            dialog.dismiss()
        }
        btn_envia.setOnClickListener {
            enviaDatos(id_usuario,id_archivox,id_solicitud,dialog)
        }



        dialog.show()
    }

    private fun enviaDatos(idUsuario: String, idArchivo: String, idSolicitud: String,dialog: Dialog) {
        val queque = Volley.newRequestQueue(this)
        val stringRequest = object :StringRequest(Request.Method.PATCH,Contants.ADRRESS_IP+"file/update",Response.Listener {
                response ->

            Toast.makeText(this, "SOLICITUD ACTUALIZADO CORRECTAMENTE", Toast.LENGTH_SHORT).show()
            listarArchivos()
            dialog.dismiss()
        },Response.ErrorListener {
                error ->
            Toast.makeText(this, "Error "+error.message, Toast.LENGTH_SHORT).show()
        })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val parametros = HashMap<String,String>()
                parametros.put("Authorization","Token "+getToken().toString())
                return parametros
            }

            override fun getParams(): MutableMap<String, String>? {
                val parametros = HashMap<String,String>()
                parametros.put("id_usuario",idUsuario)
                parametros.put("id_archivo",idArchivo)
                parametros.put("id_sol",idSolicitud)
                return parametros
            }

        }
        queque.add(stringRequest)
    }

    private fun listarNombresArchivos() {
        val queque = Volley.newRequestQueue(this)
        val stringRequest = object :StringRequest(Request.Method.GET,Contants.ADRRESS_IP+"file/get",Response.Listener {
                response ->

            try {
                fileNameList.clear()
                val datos = JSONObject(response)
                val arrays = datos.getJSONArray("result")
                for (i in 0 until arrays.length()){
                    val item = arrays.getJSONObject(i)
                    fileNameList.add(File(item.getString("id_archivo"),item.getString("nombre_arc")))

                }


            }catch (e:JSONException){}
        },Response.ErrorListener {
                error ->
            Toast.makeText(this, "Error "+error.message, Toast.LENGTH_SHORT).show()
        })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val parametros = HashMap<String,String>()
                parametros.put("Authorization","Token "+getToken().toString())
                return parametros
            }

        }
        queque.add(stringRequest)
    }

}