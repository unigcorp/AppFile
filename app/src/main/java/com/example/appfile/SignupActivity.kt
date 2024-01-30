package com.example.appfile

import android.content.Intent
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

class SignupActivity : AppCompatActivity() {

    private lateinit var edtNombre:EditText
    private lateinit var edtApellidos:EditText
    private lateinit var edtCorreo:EditText
    private lateinit var edtContrasena:EditText
    private lateinit var btn_registrar:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        edtNombre = findViewById(R.id.id_etNombres)
        edtApellidos = findViewById(R.id.id_etApellidos)
        edtCorreo = findViewById(R.id.id_etCorreo)
        edtContrasena = findViewById(R.id.id_etContrasena)
        btn_registrar = findViewById(R.id.btn_registrar)
        btn_registrar.setOnClickListener {
            validarDatos(edtNombre.text.toString(),edtApellidos.text.toString(),edtCorreo.text.toString(),edtContrasena.text.toString())
        }
    }

    private fun validarDatos(strnombre: String, strapellido: String, strcorreo: String, strcontrasena: String) {
        if(!TextUtils.isEmpty(strnombre)||!TextUtils.isEmpty(strapellido)||!TextUtils.isEmpty(strcorreo)||!TextUtils.isEmpty(strcontrasena)){
            if(!PatternsCompat.EMAIL_ADDRESS.matcher(strcorreo).matches()){
                Toast.makeText(this, "INSERTA UN CORREO VALIDO", Toast.LENGTH_SHORT).show()
            }else{
                if(strcontrasena.length>=6){
                    enviaDatos(strnombre,strapellido,strcorreo,strcontrasena)
                }else{
                    Toast.makeText(this, "INSERTA UNA CONTRASENA VALIDA", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            Toast.makeText(this, "DEBE COMPLETAR TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show()
        }

    }

    private fun enviaDatos(strnombre: String, strapellido: String, strcorreo: String, strcontrasena: String) {
        val queque = Volley.newRequestQueue(this)
        val parametros = JSONObject()
        parametros.put("nombre",strnombre)
        parametros.put("apellidos",strapellido)
        parametros.put("correo",strcorreo)
        parametros.put("contrasena",strcontrasena)

        val strRequest = JsonObjectRequest(
            Request.Method.POST,"http://192.168.0.15:8080/user/signup",parametros, {
                response ->
            try {

                Toast.makeText(this, "REGISTRO CON EXITO", Toast.LENGTH_SHORT).show()
                vaciarDatos(edtNombre,edtApellidos,edtCorreo,edtContrasena)
                //startActivity(Intent(this,LoginActivity::class.java))
                val intent = Intent(this,LoginActivity::class.java)
                intent.putExtra("correo",strcorreo)
                intent.putExtra("contrasena",strcontrasena)
                startActivity(intent)
                finish()

            }   catch (e: JSONException){
                Toast.makeText(this, "Error de exception"+e.message, Toast.LENGTH_SHORT).show()
            }

        },
            Response.ErrorListener {
                    error ->
                if (error.networkResponse.statusCode==400){
                    Toast.makeText(this, "EL CORREO QUE QUIERE REGISTRAR YA EXISTE", Toast.LENGTH_SHORT).show()
                }

        })
        queque.add(strRequest)
    }


    private fun vaciarDatos(edtNombre: EditText, edtApellidos: EditText, edtCorreo: EditText, edtContrasena: EditText) {
        edtNombre.setText("")
        edtApellidos.setText("")
        edtCorreo.setText("")
        edtContrasena.setText("")
    }
}