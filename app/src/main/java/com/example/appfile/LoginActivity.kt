package com.example.appfile

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
    private lateinit var btn_forgotPassword:TextView
    private lateinit var btnSend:Button
    private lateinit var btnRegister:Button
    private  lateinit var sharedPreferences:SharedPreferences

    //DIALOG
    private lateinit var editTextCorreo:EditText
    private lateinit var btn_envia:Button
    private lateinit var btn_cancela:Button


    private var correoRecibido=""
    private var contrasenaRecibido=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("datosUsuario",Context.MODE_PRIVATE);
        correoEditText = findViewById(R.id.id_etCorreo)
        contrasenaEditText = findViewById(R.id.id_etContrasena)
        btnSend = findViewById(R.id.id_btn_send)
        btnRegister = findViewById(R.id.btn_register)
        btn_forgotPassword = findViewById(R.id.id_forgotPassword)


        //correoRecibido = getEx

        btnSend.setOnClickListener {
            val strCorreo = correoEditText.text.toString()
            val strContrasena = contrasenaEditText.text.toString()
            if(TextUtils.isEmpty(strCorreo)||TextUtils.isEmpty(strContrasena)){
                Toast.makeText(this, "DEBE INSERTAR CORREO COMO CONTRASENA", Toast.LENGTH_SHORT).show()
            }else{
                sendLogin(strCorreo,strContrasena)
            }


        }
        btnRegister.setOnClickListener {
            //startActivity(Intent(this,SignupActivity::class.java))
            val intent = Intent(this,SignupActivity::class.java)
            startActivity(intent)
        }
        btn_forgotPassword.setOnClickListener {
            openDialod()
        }



    }

    private fun openDialod() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_forgotpassword)
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val heigth = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window?.setLayout(width,heigth)


        editTextCorreo = dialog.findViewById(R.id.editTextCorreo)
        btn_envia = dialog.findViewById(R.id.btn_envia)
        btn_cancela = dialog.findViewById(R.id.btn_cancela)


        btn_cancela.setOnClickListener {
            dialog.dismiss()
        }
        btn_envia.setOnClickListener {
            enviaDatos(editTextCorreo.text.toString())
        }



        dialog.show()
    }

    private fun enviaDatos(correo: String) {
        val queque = Volley.newRequestQueue(this)
        val parametros = JSONObject()

        parametros.put("correo",correo)

        val strRequest = JsonObjectRequest(
            Request.Method.POST,Contants.ADRRESS_IP+"user/forgotPAssword",parametros, {
                    response ->
                try {

                    Toast.makeText(this, "SE ENVIO SU CONTRASENA A SU CORREO", Toast.LENGTH_SHORT).show()

                    finish()

                }   catch (e: JSONException){
                    Toast.makeText(this, "Error de exception"+e.message, Toast.LENGTH_SHORT).show()
                }

            },
            Response.ErrorListener {
                    error ->
                if (error.networkResponse.statusCode==400){
                    Toast.makeText(this, "EL CORREO NO EXISTE", Toast.LENGTH_SHORT).show()
                }

            })
        queque.add(strRequest)

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
                 val data = response.getJSONArray("result")
                 val x = data.getJSONObject(0)
                 val nombre = x.getString("nombre")
                 val id_usuario = x.getString("id_usuario")
                 saveShared(token,id_usuario,nombre)
                 //val dato = response.getJSONObject("")
                 //Toast.makeText(this, "==== "+token+" ", Toast.LENGTH_SHORT).show()
                 //saveShared(token)
                 //Toast.makeText(this, "==="+y, Toast.LENGTH_SHORT).show()
                 Toast.makeText(this, "INICIO DE SESION CON EXITO", Toast.LENGTH_SHORT).show()
                 startActivity(Intent(this,MainActivity::class.java))

             }   catch (e:JSONException){
                 Toast.makeText(this, "Error de exception"+e.message, Toast.LENGTH_SHORT).show()
             }

        },Response.ErrorListener {
            Toast.makeText(this, "Error de servidor", Toast.LENGTH_SHORT).show()
        })
        queque.add(strRequest)
    }

    private fun saveShared(token: String,id:String,nombre:String) {
        val  sharedEdit = sharedPreferences.edit()
        sharedEdit.putString("TOKEN",token)
        sharedEdit.putString("ID",id)
        sharedEdit.putString("NOMBRE",nombre)
        sharedEdit.apply()
    }
    private fun getToken():String?{
        val token = sharedPreferences.getString("TOKEN","")
        return token
    }
}