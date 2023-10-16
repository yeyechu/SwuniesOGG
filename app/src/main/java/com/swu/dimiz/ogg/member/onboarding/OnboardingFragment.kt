package com.swu.dimiz.ogg.member.onboarding

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.swu.dimiz.ogg.MainActivity
import com.swu.dimiz.ogg.R

class OnboardingFragment : Fragment() {
    var viewPager: ViewPager? = null
    var adapter: OnboardingAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding_slid, container, false)
        viewPager = view.findViewById<ViewPager>(R.id.viewpager)
        adapter = OnboardingAdapter(requireContext())
        viewPager!!.adapter = adapter

        if (isOpenAlready()) {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            val editor: SharedPreferences.Editor =
                requireContext().getSharedPreferences("slide", Context.MODE_PRIVATE).edit()
            editor.putBoolean("slide", true)
            editor.apply()
        }
        return view
    }

    private fun isOpenAlready(): Boolean {
        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences("slide", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("slide", false)
    }
}
