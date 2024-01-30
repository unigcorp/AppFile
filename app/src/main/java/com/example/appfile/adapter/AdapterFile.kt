package com.example.appfile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appfile.R
import com.example.appfile.model.File

class AdapterFile(private val fileList:ArrayList<File>):RecyclerView.Adapter<AdapterFile.MyviewFile>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterFile.MyviewFile {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file,parent,false)
        return MyviewFile(view)
    }

    override fun onBindViewHolder(holder: AdapterFile.MyviewFile, position: Int) {
        val dato = fileList[position]
        holder.nombre.text = dato.nombre+"====>"
        holder.fecha.text = dato.fecha
        holder.status.text = dato.status
    }

    override fun getItemCount(): Int {
        return fileList.size
    }
    class MyviewFile(itemView: View):RecyclerView.ViewHolder(itemView) {
        val nombre:TextView = itemView.findViewById(R.id.id_nombre)
        val fecha:TextView = itemView.findViewById(R.id.id_fecha)
        val status:TextView = itemView.findViewById(R.id.id_status)
    }
}