package com.swu.dimiz.ogg.member.onboarding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.swu.dimiz.ogg.R

class OnboardingAdapter(private val ctx: Context) : PagerAdapter() {

    override fun getCount(): Int {
        return 4
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.layer_onboarding_screen, container, false)
        val logo = view.findViewById<ImageView>(R.id.logo)
        val ind1 = view.findViewById<ImageView>(R.id.ind1)
        val ind2 = view.findViewById<ImageView>(R.id.ind2)
        val ind3 = view.findViewById<ImageView>(R.id.ind3)
        val ind4 = view.findViewById<ImageView>(R.id.ind4)
        val title = view.findViewById<TextView>(R.id.title)
        val desc = view.findViewById<TextView>(R.id.desc)

        when (position) {
            0 -> {
                logo.setImageResource(R.drawable.onboarding_image_1)
                ind1.setImageResource(R.drawable.common_indicator_selected)
                ind2.setImageResource(R.drawable.common_indicator_default)
                ind3.setImageResource(R.drawable.common_indicator_default)
                ind4.setImageResource(R.drawable.common_indicator_default)
                title.setText (R.string.onboarding_title_1)
                desc.setText (R.string.onboarding_subtext_1)

            }
            1 -> {
                logo.setImageResource(R.drawable.onboarding_image_2)
                ind1.setImageResource(R.drawable.common_indicator_default)
                ind2.setImageResource(R.drawable.common_indicator_selected)
                ind3.setImageResource(R.drawable.common_indicator_default)
                ind4.setImageResource(R.drawable.common_indicator_default)

                title.setText (R.string.onboarding_title_2)
                desc.setText (R.string.onboarding_subtext_2)

            }
            2 -> {
                logo.setImageResource(R.drawable.onboarding_image_3)
                ind1.setImageResource(R.drawable.common_indicator_default)
                ind2.setImageResource(R.drawable.common_indicator_default)
                ind3.setImageResource(R.drawable.common_indicator_selected)
                ind4.setImageResource(R.drawable.common_indicator_default)
                title.setText (R.string.onboarding_title_3)
                desc.setText (R.string.onboarding_subtext_3)

            }

            3 -> {
                logo.setImageResource(R.drawable.onboarding_image_4)
                ind1.setImageResource(R.drawable.common_indicator_default)
                ind2.setImageResource(R.drawable.common_indicator_default)
                ind3.setImageResource(R.drawable.common_indicator_default)
                ind4.setImageResource(R.drawable.common_indicator_selected)
                title.setText (R.string.onboarding_title_4)
                desc.setText (R.string.onboarding_subtext_4)

            }
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
