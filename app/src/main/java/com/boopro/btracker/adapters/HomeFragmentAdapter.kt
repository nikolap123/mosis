package com.boopro.btracker.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boopro.btracker.data.model.ComplaintModel
import com.boopro.btracker.databinding.SingleHomeItemBinding
import com.boopro.btracker.viewholders.HomeFragmentViewHolder

class HomeFragmentAdapter(
    private val context: Context,
    private var complaintsList: List<ComplaintModel>,
    private val selectListener: SelectListener
) : RecyclerView.Adapter<HomeFragmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFragmentViewHolder {
        val binding = SingleHomeItemBinding.inflate(LayoutInflater.from(context), parent, false)

        return HomeFragmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeFragmentViewHolder, position: Int) {
        holder.binding.singleHomeItemTV.text = complaintsList[position].title

        holder.binding.singleHomeItem.setOnClickListener {
            selectListener.onItemClicked(complaintsList[position])
        }
    }

    override fun getItemCount(): Int {
        return complaintsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(list: List<ComplaintModel>) {
        complaintsList = list
        notifyDataSetChanged()
    }

    fun getItems() : List<ComplaintModel> {
        return complaintsList;
    }
}

