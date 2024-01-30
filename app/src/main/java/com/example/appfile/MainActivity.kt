package com.example.appfile

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.appfile.adapter.AdapterFile
import com.example.appfile.model.File
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerview:RecyclerView
    private lateinit var fileList:ArrayList<File>
    private  lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("datosUsuario", Context.MODE_PRIVATE);
        recyclerview = findViewById(R.id.recyclerView)
        recyclerview.layoutManager = GridLayoutManager(this,1)
        fileList = arrayListOf<File>()
        listarArchivos()
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
                          fileList.add(File(item.getString("id_usuario"),item.getString("nombre_arc"),item.getString("fecha"),item.getString("status")))
                          Toast.makeText(this, "====>"+item.getString("id_usuario"), Toast.LENGTH_SHORT).show()
                      }

                      setupFile(fileList)
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

            override fun getParams(): MutableMap<String, String>? {
                val parametros = HashMap<String,String>()
                parametros.put("id_usuario",getID().toString())
                return parametros
            }
        }
        queque.add(stringRequest)
    }

    private fun setupFile(fileList: ArrayList<File>) {
        recyclerview.adapter = AdapterFile(fileList)
    }
    private fun getToken():String?{
        val token = sharedPreferences.getString("TOKEN","")
        return token
    }
    private fun getID():String?{
        val id = sharedPreferences.getString("ID","")
        return id
    }
}