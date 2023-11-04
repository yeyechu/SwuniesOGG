package com.swu.dimiz.ogg

import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.swu.dimiz.ogg.databinding.ItemToastBinding

class OggSnackbar(view: View, private val message: String) {

    companion object {
        fun make(view: View, message: String) = OggSnackbar(view, message)
    }

    private val context = view.context
    private val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
    private val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

    private val inflater = LayoutInflater.from(context)
    private val binding: ItemToastBinding = DataBindingUtil.inflate(inflater, R.layout.item_toast, null, false)

    init {
        initView()
        initData()
    }

    private fun initView() {
        with(snackbarLayout) {
            removeAllViews()
            setPadding(0, 0, 0, 0)
            setBackgroundColor(ContextCompat.getColor(context, R.color.transparency_transparent))
            addView(binding.root, 0)
        }
    }

    private fun initData() {
        binding.textToast.text = message
    }

    fun show() {
        snackbar.show()
    }
}