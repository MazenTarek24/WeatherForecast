package com.example.weatherforecast.favourite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.ItemFavBinding
import com.example.weatherforecast.localsource.FavoriteLocationEntity

class RvFavAdapter(val favList : ArrayList<FavoriteLocationEntity>, private val itemClick : OnItemClick) :
    RecyclerView.Adapter<RvFavAdapter.ViewHolder>() {



    class ViewHolder(val binding : ItemFavBinding) : RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemFavBinding.inflate
            (LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listFav = favList[position]
        holder.binding.apply {
            locNameFav.text = listFav.name
            holder.binding.root.setOnClickListener {
                itemClick.onItemClick(listFav)
            }
        }
    }

    override fun getItemCount(): Int {
       return favList.size
    }
}