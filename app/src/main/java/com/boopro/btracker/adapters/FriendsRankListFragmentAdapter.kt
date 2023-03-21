package com.boopro.btracker.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boopro.btracker.data.model.UserModel
import com.boopro.btracker.databinding.SingleFriendRankListItemBinding
import com.boopro.btracker.viewholders.FriendsRankListFragmentViewHolder
import com.bumptech.glide.Glide

class FriendsRankListFragmentAdapter(
    private val context: Context,
    private var friendsList: List<UserModel>,
    private val showRank: Boolean
) : RecyclerView.Adapter<FriendsRankListFragmentViewHolder>() {
    private var rank = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsRankListFragmentViewHolder {
        val binding = SingleFriendRankListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        rank = 1;

        return FriendsRankListFragmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendsRankListFragmentViewHolder, position: Int) {
        lateinit var friendsListSorted: List<UserModel>

        if (showRank) {
            friendsListSorted = friendsList.sortedByDescending { it.points }
            holder.binding.singleFriendRankListRank.text = (position + 1).toString()
        } else {
            friendsListSorted = friendsList
            holder.binding.singleFriendRankListRank.text = ""
        }

        holder.binding.singleFriendRankListFirstname.text = friendsListSorted[position].firstname
        holder.binding.singleFriendRankListLastname.text = friendsListSorted[position].points.toString()

        Glide.with(context).load(friendsListSorted[position].imageURL).into(holder.binding.singleFriendRankListImage)
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(list: List<UserModel>) {
        friendsList = list
        notifyDataSetChanged()
    }
}