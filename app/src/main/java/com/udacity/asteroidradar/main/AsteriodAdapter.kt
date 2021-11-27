package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.ItemAsteroidListBinding

class AsteroidAdapter : RecyclerView.Adapter<AsteroidViewHolder>(){

    var asteroids: List<Asteroid> = emptyList()
        set(value) {
            field = value
            // For an extra challenge, update this to use the paging library.

            // Notify any registered observers that the data set has changed. This will cause every
            // element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        val withDataBinding: ItemAsteroidListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), AsteroidViewHolder.LAYOUT, parent, false)

        return AsteroidViewHolder(withDataBinding)

    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.asteroid =asteroids[position]
            //it.videoCallback = callback
        }
    }

    override fun getItemCount(): Int {
        return asteroids.size
    }
}

class AsteroidViewHolder(val viewDataBinding: ItemAsteroidListBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item_asteroid_list
    }
}