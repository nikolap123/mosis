package com.boopro.btracker.screens.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.boopro.btracker.adapters.HomeFragmentAdapter
import com.boopro.btracker.adapters.SelectListener
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.boopro.btracker.R
import com.boopro.btracker.data.Repository
import com.boopro.btracker.data.model.ComplaintModel
import com.boopro.btracker.databinding.FragmentHomeBinding
import com.bumptech.glide.Glide
import com.boopro.btracker.helper.Consts
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(), SelectListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeFragmentAdapter: HomeFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        lifecycleScope.launch(Dispatchers.IO) {
            Repository.getUser().collect {
                withContext(Dispatchers.Main) {
                    Consts.currentUser = it;
                    Repository.saveUserInfo(requireContext(),it);
                }
            }
        }
        val userInfo = Repository.getUserInfo(requireContext())
        println("UserInfo" + userInfo.toString())
        Consts.currentUser = userInfo
        Consts.currentUserId = Consts.auth.currentUser?.uid
        binding.user = userInfo
        Glide.with(requireContext()).load(userInfo.imageURL).into(binding.homeFragmentImageView)
        getData(lifecycleScope)

        showComplaints()
        return binding.root
    }

    private fun getData(lifecycleScope: LifecycleCoroutineScope) {

        lifecycleScope.launch(Dispatchers.IO) {
            Repository.getUserFriends().collect {
                withContext(Dispatchers.Main) {
                    Consts.currentUserFriends = it
                }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            Repository.getFriendsComplaints().collect {
                withContext(Dispatchers.Main) {
                    Consts.friendsComplaints = it
                }
            }
        }


    }

    private fun showComplaints() {


        binding.homeFragmentRecyclerView.setHasFixedSize(false)
        binding.homeFragmentRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        homeFragmentAdapter = HomeFragmentAdapter(requireContext(), listOf(), this)
        homeFragmentAdapter.setItems(listOf())
        binding.homeFragmentRecyclerView.adapter = homeFragmentAdapter


        lifecycleScope.launch(Dispatchers.IO) {
            Repository.getUserComplaints().collect {
                withContext(Dispatchers.Main) {

                    println("currentUserComplaints")
                    print(homeFragmentAdapter.getItems())
                    Consts.currentUserComplaints.clear()
                    Consts.currentUserComplaints.addAll(it)
                    homeFragmentAdapter.setItems(it)

                }

            }


        }


    }

    override fun onItemClicked(complaint: ComplaintModel) {
        showHomeDialog(complaint)
    }

    private fun showHomeDialog(complaint: ComplaintModel) {
        val homeDialog = Dialog(requireActivity())

        homeDialog.setContentView(R.layout.home_dialog)
        homeDialog.window?.setLayout(1000, 800)
        homeDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val homeDialogTitle = homeDialog.findViewById(R.id.homeDialogComplaintTitle) as TextView
        val homeDialogComplaintContent = homeDialog.findViewById(R.id.homeDialogComplaintContent) as TextView
        val closeBtn = homeDialog.findViewById(R.id.homeDialogCloseBtn) as MaterialButton

        homeDialogTitle.text = complaint.title
        homeDialogComplaintContent.text = complaint.content

        closeBtn.setOnClickListener { homeDialog.dismiss() }

        homeDialog.show()
    }
}