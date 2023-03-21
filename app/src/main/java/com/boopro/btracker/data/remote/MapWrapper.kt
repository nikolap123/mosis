package com.boopro.btracker.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.boopro.btracker.R
import com.boopro.btracker.data.Repository
import com.boopro.btracker.data.model.ComplaintModel
import com.boopro.btracker.helper.Consts
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ktx.utils.sphericalDistance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Class which will handle everything related to Map
object MapWrapper {

    fun userComplaints(context: Context, googleMap: GoogleMap) {
        for (complaint in Consts.currentUserComplaints) {
            googleMap.addMarker(MarkerOptions().position(LatLng(complaint.latitude, complaint.longitude)).title(complaint.title).snippet(null).icon(getBitmapDescriptorFromVector(context, R.drawable.user_complaint_marker)))
        }
    }
    fun friendsComplaints(context: Context, googleMap: GoogleMap) {
        for (complaint in Consts.friendsComplaints.values) {
            googleMap.addMarker(MarkerOptions().position(LatLng(complaint.latitude, complaint.longitude)).title(complaint.title).snippet(getFriendUsernameById(complaint.userId)).icon(getBitmapDescriptorFromVector(context, R.drawable.friend_complaint_marker)))
        }
    }
//    fun friendsComplaints(context: Context, googleMap: GoogleMap,userFriendsComplaints: List<ComplaintModel>) {
//        for (complaint in userFriendsComplaints) {
//            googleMap.addMarker(MarkerOptions().position(LatLng(complaint.latitude, complaint.longitude)).title(complaint.title).snippet(getFriendUsernameById(complaint.userId)).icon(getBitmapDescriptorFromVector(context, R.drawable.friend_complaint_marker)))
//        }
//    }

    fun filterComplaintsByTitle(context: Context, googleMap: GoogleMap, complaints: List<ComplaintModel>) {
        googleMap.clear()

        for (complaint in complaints) {
            if (Consts.currentUserComplaints.contains(complaint)) {
                googleMap.addMarker(MarkerOptions().position(LatLng(complaint.latitude, complaint.longitude)).title(complaint.title).snippet(null).icon(getBitmapDescriptorFromVector(context, R.drawable.user_complaint_marker)))
            } else {
                googleMap.addMarker(MarkerOptions().position(LatLng(complaint.latitude, complaint.longitude)).title(complaint.title).snippet(getFriendUsernameById(complaint.userId)).icon(getBitmapDescriptorFromVector(context, R.drawable.friend_complaint_marker)))
            }
        }
    }

    fun filterComplaints(context: Context, googleMap: GoogleMap, currentLocation: LatLng, title: String, created: String, radius: Double) {
        val filteredComplaints = mutableListOf<ComplaintModel>()

        when {
            title.isNotEmpty() && created.isNotEmpty() && radius != -1.0 -> {
                for (complaint in Consts.currentUserComplaints) {
                    if (complaint.title.contains(title, true) && Consts.currentUser.username.contains(created, true) && LatLng(complaint.latitude, complaint.longitude).sphericalDistance(currentLocation) <= radius) {
                        filteredComplaints.add(complaint)
                    }
                }
                for (complaint in Consts.friendsComplaints.values) {
                    if (complaint.title.contains(title, true) && getFriendUsernameById(complaint.userId)?.contains(created, true) == true && LatLng(complaint.latitude, complaint.longitude).sphericalDistance(currentLocation) <= radius) {
                        filteredComplaints.add(complaint)
                    }
                }
            }
            title.isNotEmpty() && created.isNotEmpty() && radius == -1.0 -> {
                for (complaint in Consts.currentUserComplaints) {
                    if (complaint.title.contains(title, true) && Consts.currentUser.username.contains(created, true)) {
                        filteredComplaints.add(complaint)
                    }
                }
                for (complaint in Consts.friendsComplaints.values) {
                    if (complaint.title.contains(title, true) && getFriendUsernameById(complaint.userId)?.contains(created, true) == true) {
                        filteredComplaints.add(complaint)
                    }
                }
            }
            title.isNotEmpty() && created.isEmpty() && radius == -1.0 -> {
                for (complaint in Consts.currentUserComplaints) {
                    if (complaint.title.contains(title, true)) {
                        filteredComplaints.add(complaint)
                    }
                }
                for (complaint in Consts.friendsComplaints.values) {
                    if (complaint.title.contains(title, true)) {
                        filteredComplaints.add(complaint)
                    }
                }
            }
            title.isEmpty() && created.isEmpty() && radius != -1.0 -> {
                for (complaint in Consts.currentUserComplaints) {
                    if (LatLng(complaint.latitude, complaint.longitude).sphericalDistance(currentLocation) <= radius) {
                        filteredComplaints.add(complaint)
                    }
                }
                for (complaint in Consts.friendsComplaints.values) {

                    if (LatLng(complaint.latitude, complaint.longitude).sphericalDistance(currentLocation) <= radius) {
                        filteredComplaints.add(complaint)
                    }
                }
            }
            title.isEmpty() && created.isNotEmpty() && radius != -1.0 -> {
                for (complaint in Consts.currentUserComplaints) {
                    if (Consts.currentUser.username.contains(created, true) && LatLng(complaint.latitude, complaint.longitude).sphericalDistance(currentLocation) <= radius) {
                        filteredComplaints.add(complaint)
                    }
                }
                for (complaint in Consts.friendsComplaints.values) {
                    if (getFriendUsernameById(complaint.userId)?.contains(created, true) == true && LatLng(complaint.latitude, complaint.longitude).sphericalDistance(currentLocation) <= radius) {
                        filteredComplaints.add(complaint)
                    }
                }
            }
            title.isNotEmpty() && created.isEmpty() && radius != -1.0 -> {
                for (complaint in Consts.currentUserComplaints) {
                    if (complaint.title.contains(title, true) && LatLng(complaint.latitude, complaint.longitude).sphericalDistance(currentLocation) <= radius) {
                        filteredComplaints.add(complaint)
                    }
                }
                for (complaint in Consts.friendsComplaints.values) {
                    if (complaint.title.contains(title, true) && LatLng(complaint.latitude, complaint.longitude).sphericalDistance(currentLocation) <= radius) {
                        filteredComplaints.add(complaint)
                    }
                }
            }
            title.isEmpty() && created.isNotEmpty() && radius == -1.0 -> {
                for (complaint in Consts.currentUserComplaints) {
                    if (Consts.currentUser.username.contains(created, true)) {
                        filteredComplaints.add(complaint)
                    }
                }
                for (complaint in Consts.friendsComplaints.values) {
                    if (getFriendUsernameById(complaint.userId)?.contains(created, true) == true) {
                        filteredComplaints.add(complaint)
                    }
                }
            }
            title.isEmpty() && created.isEmpty() && radius == -1.0 -> {
                for (complaint in Consts.currentUserComplaints) {
                    filteredComplaints.add(complaint)
                }
                for (complaint in Consts.friendsComplaints.values) {
                    filteredComplaints.add(complaint)
                }
            }
        }

        googleMap.clear()

        for (complaint in filteredComplaints) {
            if (Consts.currentUserComplaints.contains(complaint)) {
                googleMap.addMarker(MarkerOptions().position(LatLng(complaint.latitude, complaint.longitude)).title(complaint.title).snippet(null).icon(getBitmapDescriptorFromVector(context, R.drawable.user_complaint_marker)))
            } else {
                googleMap.addMarker(MarkerOptions().position(LatLng(complaint.latitude, complaint.longitude)).title(complaint.title).snippet(getFriendUsernameById(complaint.userId)).icon(getBitmapDescriptorFromVector(context, R.drawable.friend_complaint_marker)))
            }
        }
    }

    fun placeNewComplaintMarker(context: Context, googleMap: GoogleMap, complaint: ComplaintModel) {
        googleMap.addMarker(MarkerOptions().position(LatLng(complaint.latitude, complaint.longitude)).title(complaint.title).snippet(null).icon(getBitmapDescriptorFromVector(context, R.drawable.user_complaint_marker)))
    }

    private fun getBitmapDescriptorFromVector(context: Context, @DrawableRes vectorDrawableResourceId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)

        if (vectorDrawable != null) {
            val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.draw(canvas)

            return BitmapDescriptorFactory.fromBitmap(bitmap)
        } else {
            return BitmapDescriptorFactory.defaultMarker()
        }
    }

    private fun getFriendUsernameById(id: String): String? {
        return Consts.currentUserFriends[id]?.username
    }

}