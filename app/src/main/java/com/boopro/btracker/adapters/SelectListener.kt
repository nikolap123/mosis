package com.boopro.btracker.adapters

import com.boopro.btracker.data.model.ComplaintModel

interface SelectListener {

    fun onItemClicked(complaint: ComplaintModel)
}