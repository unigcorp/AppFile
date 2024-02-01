package com.example.appfile.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.appfile.Contants
import com.example.appfile.R
import com.example.appfile.interfaz.Interfaz
import com.example.appfile.model.File

class AdapterFile(private val context:Context,private val interfaz:Interfaz,private val fileList:ArrayList<File>):RecyclerView.Adapter<AdapterFile.MyviewFile>() {

    private  var sharedPreferences: SharedPreferences = context.getSharedPreferences("datosUsuario", Context.MODE_PRIVATE)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterFile.MyviewFile {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file,parent,false)
        return MyviewFile(view)
    }

    override fun onBindViewHolder(holder: AdapterFile.MyviewFile, position: Int) {
        val dato = fileList[position]
        holder.nombre.text = dato.nombre
        holder.fecha.text = dato.fecha
        holder.status.text = dato.status
        holder.btn_editar.setOnClickListener {
         //   Toast.makeText(context, "hola desde el item "+dato.nombre, Toast.LENGTH_SHORT).show()
            interfaz.onClick(position, dato.nombre,fileList.get(position).id,fileList.get(position).id_solicitud)
        }
        holder.btn_eliminar.setOnClickListener {
            dialogDeleteItem(position,dato.nombre,fileList.get(position).id_solicitud)
        }
    }

    private fun dialogDeleteItem(position: Int, nombre: String, idSolicitud: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Confirma!!")
        builder.setMessage("Esta seguro que desea eliminar "+nombre)
        builder.setPositiveButton("Si"){
            diag,which->
            deleteItem(position,idSolicitud)
            Toast.makeText(context, "id_solicitud "+idSolicitud, Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No",null)
        val dialog = builder.create()
        dialog.show()

    }

    private fun deleteItem(position: Int, idSolicitud: String) {
        val queque = Volley.newRequestQueue(context)
        val stringRequest = object :
            StringRequest(Request.Method.DELETE, Contants.ADRRESS_IP+"file/delete/"+idSolicitud, Response.Listener {
                response ->
            Toast.makeText(context, "SOLICITUD ELIMINADA CORRECTAMENTE", Toast.LENGTH_SHORT).show()
                fileList.removeAt(position)
                notifyDataSetChanged()


        }, Response.ErrorListener {
                error ->
            Toast.makeText(context, "Error "+error.message, Toast.LENGTH_SHORT).show()
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

    override fun getItemCount(): Int {
        return fileList.size
    }
    class MyviewFile(itemView: View):RecyclerView.ViewHolder(itemView) {
        val nombre:TextView = itemView.findViewById(R.id.id_nombre)
        val fecha:TextView = itemView.findViewById(R.id.id_fecha)
        val status:TextView = itemView.findViewById(R.id.id_status)
        val btn_eliminar:ImageButton = itemView.findViewById(R.id.id_delete)
        val btn_editar:ImageButton = itemView.findViewById(R.id.id_edit)

    }
    private fun getToken():String?{
        val token = sharedPreferences.getString("TOKEN","")
        return token
    }
}