package com.boopro.btracker.helper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import com.boopro.btracker.R
import com.boopro.btracker.data.Repository
import com.boopro.btracker.data.model.ComplaintModel
import com.boopro.btracker.data.model.UserModel
import com.boopro.btracker.data.remote.MapWrapper
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Dialogs {

    fun showUserComplaintMapDialog(context: Context, marker: Marker) {

        val complaint = marker.title?.let { getUserComplaintByTitle(it) }

        val userComplaintDialog = Dialog(context)

        userComplaintDialog.setContentView(R.layout.home_dialog)
        userComplaintDialog.window?.setLayout(1000, 800)
        userComplaintDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val homeDialogComplaintTitle = userComplaintDialog.findViewById(R.id.homeDialogComplaintTitle) as TextView
        val homeDialogComplaintContent = userComplaintDialog.findViewById(R.id.homeDialogComplaintContent) as TextView
        val homeDialogComplaintCloseBtn = userComplaintDialog.findViewById(R.id.homeDialogCloseBtn) as MaterialButton

        homeDialogComplaintTitle.text = complaint?.title
        homeDialogComplaintContent.text = complaint?.content

        homeDialogComplaintCloseBtn.setOnClickListener { userComplaintDialog.dismiss() }

        userComplaintDialog.show()
    }

    fun showFriendComplaintMapDialog(context: Context, marker: Marker, lifecycleScope: LifecycleCoroutineScope) {

        val complaint = marker.title?.let { getFriendComplaintByTitle(it) }

        val friendComplaintDialog = Dialog(context)

        friendComplaintDialog.setContentView(R.layout.friend_complaint_dialog)
        friendComplaintDialog.window?.setLayout(1000, 800)
        friendComplaintDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val friendDialogComplaintTitle = friendComplaintDialog.findViewById(R.id.friendDialogComplaintTitle) as TextView
        val friendDialogComplaintAuthor = friendComplaintDialog.findViewById(R.id.friendDialogComplaintAuthor) as TextView
        val friendDialogComplaintContent = friendComplaintDialog.findViewById(R.id.friendDialogComplaintContent) as TextView
        val like = friendComplaintDialog.findViewById(R.id.friendDialogComplaintLike) as CheckBox
        val friendDialogComplaintCloseBtn = friendComplaintDialog.findViewById(R.id.friendComplaintDialogCloseBtn) as MaterialButton

        like.isChecked = complaint?.isLiked() == true

        friendDialogComplaintTitle.text = complaint?.title
        friendDialogComplaintAuthor.text = context.getString(R.string.author) + "   " + marker.snippet
        friendDialogComplaintContent.text = complaint?.content

        friendDialogComplaintCloseBtn.setOnClickListener {
            if (complaint != null) {
                when {
                    like.isChecked && !complaint.isLiked() -> Consts.currentUserFriends[complaint.userId]?.let {
                        updateComplaintAndUser(complaint, it, 1, lifecycleScope)
                    }
                    !like.isChecked && complaint.isLiked() -> Consts.currentUserFriends[complaint.userId]?.let {
                        updateComplaintAndUser(complaint, it, -1, lifecycleScope)
                    }
                }
            }

            friendComplaintDialog.dismiss()
        }

        friendComplaintDialog.show()

    }

    fun showAddComplaintDialog(context: Context, currentLocation: LatLng, googleMap: GoogleMap) {
        val addComplaintDialog = Dialog(context)

        addComplaintDialog.setContentView(R.layout.add_complaint_dialog)
        addComplaintDialog.window?.setLayout(1000, 800)
        addComplaintDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val addComplaintDialogTitleText = addComplaintDialog.findViewById(R.id.addComplaintDialogTitleText) as TextView
        val title = addComplaintDialog.findViewById(R.id.addComplaintDialogTitle) as TextInputEditText
        val addComplaintDialogComplaintText = addComplaintDialog.findViewById(R.id.addComplaintDialogComplaintText) as TextView
        val content = addComplaintDialog.findViewById(R.id.addComplaintDialogComplaint) as TextInputEditText
        val postBtn = addComplaintDialog.findViewById(R.id.addComplaintDialogPostBtn) as MaterialButton

        postBtn.setOnClickListener {
            val complaint = ComplaintModel()

            if (!title.text.toString().trim().isEmpty() && !content.text.toString().trim().isEmpty()) {
                complaint.title = title.text.toString()
                complaint.content = content.text.toString()
                complaint.userId = Consts.currentUserId.toString()
                complaint.latitude = currentLocation.latitude
                complaint.longitude = currentLocation.longitude

                Repository.addComplaint(complaint)
                Consts.currentUserComplaints.add(complaint)
                println("Position " + complaint)
                MapWrapper.placeNewComplaintMarker(context, googleMap, complaint)
            }

            addComplaintDialog.dismiss()
        }

        addComplaintDialog.show()

    }

    fun showFilterComplaintsDialog(context: Context, googleMap: GoogleMap, currentLocation: LatLng) {
        val filterComplaintsDialog = Dialog(context)

        filterComplaintsDialog.setContentView(R.layout.filter_complaints_dialog)
        filterComplaintsDialog.window?.setLayout(1000, 900)
        filterComplaintsDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //Remove from view
//        val filterComplaintsDialogRadiusText = filterComplaintsDialog.findViewById(R.id.filterComplaintsDialogRadiusText) as TextView
        val radius = filterComplaintsDialog.findViewById(R.id.filterComplaintsDialogRadius) as TextInputEditText
//        val filterComplaintsDialogTitleText = filterComplaintsDialog.findViewById(R.id.filterComplaintsDialogTitleText) as TextView
//        val title = filterComplaintsDialog.findViewById(R.id.filterComplaintsDialogTitle) as TextInputEditText
//        val filterComplaintsDialogCreatedText = filterComplaintsDialog.findViewById(R.id.filterComplaintsDialogCreatedText) as TextView
//        val created = filterComplaintsDialog.findViewById(R.id.filterComplaintsDialogCreated) as TextInputEditText
        val applyBtn = filterComplaintsDialog.findViewById(R.id.filterComplaintsDialogApplyBtn) as MaterialButton


        applyBtn.setOnClickListener {
            //Set to String()
//            val titleFilter = if (title.text.toString().trim().isNotEmpty()) title.text.toString() else String()
//            val createdFilter = if (created.text.toString().trim().isNotEmpty()) created.text.toString() else String()
            val titleFilter = String()
            val createdFilter = String()
            val radiusFilter = if (radius.text.toString().trim().isNotEmpty()) radius.text.toString().toDouble() else -1.0

            filterComplaintsDialog.dismiss()

            MapWrapper.filterComplaints(context, googleMap, currentLocation, titleFilter, createdFilter, radiusFilter)
        }

        filterComplaintsDialog.show()
    }

    private fun getUserComplaintByTitle(title: String): ComplaintModel {
        lateinit var userComplaint: ComplaintModel

        if (Consts.currentUserComplaints.isEmpty()) {

        }

        for (complaint in Consts.currentUserComplaints) {
            if (complaint.title == title) {
                userComplaint = complaint
                break
            }
        }

        return userComplaint
    }

    private fun getFriendComplaintByTitle(title: String): ComplaintModel {
        lateinit var friendComplaint: ComplaintModel

        for (complaint in Consts.friendsComplaints) {
            if (complaint.value.title == title) {
                friendComplaint = complaint.value
                break
            }
        }

        return friendComplaint
    }

    private fun updateComplaintAndUser(complaint: ComplaintModel, user: UserModel, x: Int, lifecycleScope: LifecycleCoroutineScope) {
        val oldUser = Consts.currentUserFriends[complaint.userId]
        val newUser = UserModel()

        println("oldComplaint" + complaint)
        if (oldUser != null) {
            newUser.firstname = oldUser.firstname
            newUser.lastname = oldUser.lastname
            newUser.username = oldUser.username
            newUser.email = oldUser.email
            newUser.phoneNumber = oldUser.phoneNumber
            newUser.imageURL = oldUser.imageURL
            newUser.friendList = oldUser.friendList
            newUser.points = oldUser.points + x
        }

        val newComplaint = ComplaintModel()

        newComplaint.title = complaint.title
        newComplaint.content = complaint.content
        newComplaint.latitude = complaint.latitude
        newComplaint.longitude = complaint.longitude
        newComplaint.userId = complaint.userId

        val userLikes = complaint.likes

        println("userLikes" + userLikes)

        if (x==-1) {
            userLikes.remove(Consts.currentUserId)
        } else {
            Consts.currentUserId?.let { userLikes.add(it) }
        }

        newComplaint.likes = userLikes


        lifecycleScope.launch(Dispatchers.IO) {
            Repository.updateComplaintAndUser(getComplaintId(complaint), newComplaint, newUser)
            //Repository.updateUser(complaint.userId, newUser)
        }
    }

    private fun getComplaintId(complaint: ComplaintModel): String {

        lateinit var complaintId: String

        for (key in Consts.friendsComplaints.keys) {
            if (Consts.friendsComplaints[key] == complaint) {
                complaintId = key
            }
        }

        return complaintId
    }

}