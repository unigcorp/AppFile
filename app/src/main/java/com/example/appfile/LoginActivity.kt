package com.example.appfile

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.util.PatternsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var correoEditText:EditText
    private lateinit var contrasenaEditText:EditText
    private lateinit var btnSend:Button
    private  lateinit var sharedPreferences:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("datosUsuario",Context.MODE_PRIVATE);
        correoEditText = findViewById(R.id.id_etCorreo)
        contrasenaEditText = findViewById(R.id.id_etContrasena)
        btnSend = findViewById(R.id.id_btn_send)

        btnSend.setOnClickListener {
            val strCorreo = correoEditText.text.toString()
            val strContrasena = contrasenaEditText.text.toString()
            if(TextUtils.isEmpty(strCorreo)||TextUtils.isEmpty(strContrasena)){
                Toast.makeText(this, "DEBE INSERTAR CORREO COMO CONTRASENA", Toast.LENGTH_SHORT).show()
            }else{
                sendLogin(strCorreo,strContrasena)
            }


        }



    }

    private fun sendLogin(strCorreo: String, strContrasena: String) {
        if(!PatternsCompat.EMAIL_ADDRESS.matcher(strCorreo).matches()){
            Toast.makeText(this, "Debe introducir un correo valido", Toast.LENGTH_SHORT).show()
        }else{
            login(strCorreo,strContrasena)
        }
    }

    private fun login(strCorreo: String, strContrasena: String) {
        val queque = Volley.newRequestQueue(this)
        val parametros = JSONObject()
        parametros.put("correo",strCorreo)
        parametros.put("contrasena",strContrasena)

        val strRequest = JsonObjectRequest(Request.Method.POST,"http://192.168.0.15:8080/user/login",parametros, {
            response ->
             try {
                 //val data = response.getJSONObject("result")
                 /*val dato = response.getJSONArray("result")
                 val x = dato.getJSONObject(0)
                 val y = x.getString("nombre")*/

                 val token = response.getString("token")
                 //val dato = response.getJSONObject("")
                 //Toast.makeText(this, "==== "+token+" ", Toast.LENGTH_SHORT).show()
                 //saveShared(token)
                 //Toast.makeText(this, "==="+y, Toast.LENGTH_SHORT).show()
                 Toast.makeText(this, "==="+token.toString(), Toast.LENGTH_SHORT).show()
             }   catch (e:JSONException){
                 Toast.makeText(this, "Error de exception"+e.message, Toast.LENGTH_SHORT).show()
             }

        },Response.ErrorListener {
            Toast.makeText(this, "Error de servidor", Toast.LENGTH_SHORT).show()
        })
        queque.add(strRequest)
    }

    private fun saveShared(token: String) {
        val  sharedEdit = sharedPreferences.edit()
        sharedEdit.putString("TOKEN",token)
        sharedEdit.apply()
    }
    private fun getToken():String?{
        val token = sharedPreferences.getString("TOKEN","")
        return token
    }
}