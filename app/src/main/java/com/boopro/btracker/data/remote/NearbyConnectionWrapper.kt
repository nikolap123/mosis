package com.boopro.btracker.data.remote

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.boopro.btracker.R
import com.boopro.btracker.data.Repository
import com.boopro.btracker.data.local.PrefUtility
import com.boopro.btracker.helper.Consts
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

object NearbyConnectionWrapper {
    private fun getConnectionLifecycleCallback(context: Context): ConnectionLifecycleCallback {
        return object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
                AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.accept_connection) + " " + info.endpointName)
                    .setMessage(context.getString(R.string.confirm_devices) + " " + info.authenticationDigits)
                    .setPositiveButton(
                        context.getString(R.string.accept),
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback)
                        }
                    )
                    .setNegativeButton(
                        context.getString(R.string.cancel),
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            Nearby.getConnectionsClient(context).rejectConnection(endpointId)
                        }
                    )
                    .setIcon(R.drawable.ic_nearby_connection)
                    .show()
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        sendPayload(endpointId, context)
                        Nearby.getConnectionsClient(context).stopAdvertising()
                        Nearby.getConnectionsClient(context).stopDiscovery()
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {}
                    ConnectionsStatusCodes.ERROR -> {}
                }
            }

            override fun onDisconnected(endpointId: String) {

            }
        }
    }

    fun startAdvertising(context: Context) {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        val connectionLifecycleCallback = getConnectionLifecycleCallback(context)

        Consts.currentUserId?.let {
            Nearby.getConnectionsClient(context)
                .startAdvertising(it, context.getString(R.string.serviceId), connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener { }
                .addOnFailureListener { }
        }
    }

    fun startDiscovery(context: Context) {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        val connectionLifecycleCallback = getConnectionLifecycleCallback(context)

        val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                Consts.currentUserId?.let {
                    Nearby.getConnectionsClient(context).requestConnection(it, endpointId, connectionLifecycleCallback)
                        .addOnSuccessListener { }
                        .addOnFailureListener { }
                }
            }

            override fun onEndpointLost(endpointId: String) {

            }
        }

        Nearby.getConnectionsClient(context)
            .startDiscovery(context.getString(R.string.serviceId), endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener { }
            .addOnFailureListener { }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                val receivedBytes = payload.asBytes()
                val message = receivedBytes?.let { String(it) }

                if (message != null) {
                    Repository.addFriend(message)
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {

        }
    }

    private fun sendPayload(endpointId: String, context: Context) {
        val message = Consts.currentUserId
        val bytesPayload = message?.let { Payload.fromBytes(it.toByteArray()) }

        if (bytesPayload != null) {
            Nearby.getConnectionsClient(context).sendPayload(endpointId, bytesPayload)
                .addOnSuccessListener {

                }
                .addOnFailureListener { }
        }

    }

    fun startAdvertisingDiscovery(context: Context, advertisingDiscovery: Boolean) {
        if (advertisingDiscovery) {
            startAdvertising(context)
        } else {
            startDiscovery(context)
        }
    }
}