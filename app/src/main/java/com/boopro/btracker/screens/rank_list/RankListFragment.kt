package com.boopro.btracker.screens.rank_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.boopro.btracker.adapters.FriendsRankListFragmentAdapter
import com.boopro.btracker.data.Repository
import com.boopro.btracker.databinding.FragmentRankListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RankListFragment : Fragment() {
    private lateinit var binding: FragmentRankListBinding
    private lateinit var rankListFragmentAdapter: FriendsRankListFragmentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRankListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showRankList()
    }

    private fun showRankList() {
        binding.rankListFragmentRecyclerView.setHasFixedSize(true)
        binding.rankListFragmentRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        rankListFragmentAdapter = FriendsRankListFragmentAdapter(requireContext(), emptyList(), true)

        binding.rankListFragmentRecyclerView.adapter = rankListFragmentAdapter

        lifecycleScope.launch(Dispatchers.IO) {
            Repository.getAllUsers().collect {
                withContext(Dispatchers.Main) {
                    rankListFragmentAdapter.setItems(it)
                }
            }
        }
    }
}