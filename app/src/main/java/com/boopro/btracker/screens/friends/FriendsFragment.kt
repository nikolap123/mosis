package com.boopro.btracker.screens.friends

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.boopro.btracker.R
import com.boopro.btracker.adapters.FriendsRankListFragmentAdapter
import com.boopro.btracker.adapters.HomeFragmentAdapter
import com.boopro.btracker.data.Repository
import com.boopro.btracker.data.model.UserModel
import com.boopro.btracker.databinding.FragmentFriendsBinding
import com.boopro.btracker.helper.Consts
import com.boopro.btracker.helper.Permissions
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendsFragment : Fragment() {
    private lateinit var binding: FragmentFriendsBinding
    private lateinit var friendsFragmentAdapter: FriendsRankListFragmentAdapter
    private var friendList: List<UserModel> = listOf()
    private var advertisingDiscovery = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFriendsBinding.inflate(inflater, container, false)
        friendList = Consts.currentUserFriends.values.toList()
        showFriends()

        binding.friendsFragmentAddFriendImage.setOnClickListener {
            showAddFriendDialog()
        }

        binding.friendsFragmentSearch.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchFriends(newText)
                return false
            }

        })

        return binding.root
    }

    private fun searchFriends(newText: String) {
        val filteredFriendsList: MutableList<UserModel> = mutableListOf()

        for (friend in friendList) {
            if (friend.firstname.contains(newText, true) || friend.lastname.contains(newText, true)) {
                filteredFriendsList.add(friend)
            }
        }

        friendsFragmentAdapter.setItems(filteredFriendsList)
    }

    private fun showFriends() {

        lifecycleScope.launch(Dispatchers.IO) {
            Repository.getUserFriends().collect {
                withContext(Dispatchers.Main) {
                    Consts.currentUserFriends = it
                    friendsFragmentAdapter.setItems(it.values.toList())

                }
            }
        }



        binding.friendsFragmentRecyclerView.setHasFixedSize(true)
        binding.friendsFragmentRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        friendsFragmentAdapter = FriendsRankListFragmentAdapter(requireContext(), Consts.currentUserFriends.values.toList(), true)

        binding.friendsFragmentRecyclerView.adapter = friendsFragmentAdapter
    }

    private fun showAddFriendDialog() {
        val addFriendDialog = Dialog(requireActivity())

        addFriendDialog.setContentView(R.layout.add_friend_dialog)
        addFriendDialog.window?.setLayout(1000, 800)
        addFriendDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val addFriendImage = addFriendDialog.findViewById(R.id.addFriendDialogImage) as ImageView
        val advertisingBtn = addFriendDialog.findViewById(R.id.addFriendDialogStartAdvertisingBtn) as MaterialButton
        val discoveryBtn = addFriendDialog.findViewById(R.id.addFriendDialogStartDiscoveryBtn) as MaterialButton


        advertisingBtn.setOnClickListener {
            advertisingDiscovery = true

            getPermissions()

            addFriendDialog.dismiss()
        }

        discoveryBtn.setOnClickListener {
            advertisingDiscovery = false

            getPermissions()

            addFriendDialog.dismiss()
        }

        addFriendDialog.show()
    }

    private fun getPermissions() {
        requestPermissions(Permissions.REQUIRED_NEARBY_PERMISSIONS, Permissions.REQUEST_CODE_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Permissions.REQUEST_CODE_PERMISSIONS) {
            if (Permissions.allPermissionGranted(requireContext())) {
                checkLocation()
            }
        }
    }

    private fun checkLocation() {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!location) {
            AlertDialog.Builder(requireContext())
                .setTitle(requireContext().getString(R.string.location))
                .setPositiveButton(
                    requireContext().getString(R.string.turn_on),
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), Permissions.REQUEST_CODE_LOCATION)
                    }
                )
                .setNegativeButton(requireContext().getString(R.string.cancel), null)
                .setIcon(requireContext().getDrawable(R.drawable.ic_location))
                .show()
        } else {
            Repository.startAdvertisingDiscovery(requireContext(), advertisingDiscovery)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Permissions.REQUEST_CODE_LOCATION) {
            Repository.startAdvertisingDiscovery(requireContext(), advertisingDiscovery)
        }
    }
}