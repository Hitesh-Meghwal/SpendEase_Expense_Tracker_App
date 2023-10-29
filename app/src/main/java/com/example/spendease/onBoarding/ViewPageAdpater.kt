package com.example.spendease.onBoarding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.airbnb.lottie.LottieAnimationView
import com.example.spendease.R

class ViewPagerAdapter(private val context: Context) : PagerAdapter() {

    private val images = intArrayOf(
        R.raw.growthanalysis,
        R.raw.chart,
        R.raw.monthly_chart
    )

    private val headings = intArrayOf(
        R.string.easy_expense_tracking,
        R.string.categorised_data_chart,
        R.string.filter_monthly_yearly_expense_and_make_pdf
    )

    private val description = intArrayOf(
        R.string.screenonetext,
        R.string.screentwotext,
        R.string.screenthree
    )

    override fun getCount(): Int {
        return headings.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.fragment_onboarding, container, false)

        val slideTitleImage = view.findViewById<LottieAnimationView>(R.id.lottieAnimationView2)
        val slideHeading = view.findViewById<TextView>(R.id.text)
        val slideDescription = view.findViewById<TextView>(R.id.textView4)

        slideTitleImage.setAnimation(images[position])
        slideHeading.setText(headings[position])
        slideDescription.setText(description[position])

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}