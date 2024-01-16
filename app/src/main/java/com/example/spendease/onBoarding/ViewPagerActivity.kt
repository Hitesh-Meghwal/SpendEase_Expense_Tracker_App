package com.example.spendease.onBoarding

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.spendease.R
import com.example.spendease.databinding.ActivityViewPagerBinding
import com.example.spendease.userAuthentication.Signup
import com.google.android.material.button.MaterialButton


class ViewPagerActivity : AppCompatActivity() {
    lateinit var binding: ActivityViewPagerBinding

    private lateinit var mSLideViewPager: ViewPager
    private lateinit var mDotLayout: LinearLayout
    private lateinit var nextbtn: MaterialButton
    private lateinit var skipbtn: TextView

    private lateinit var dots: Array<TextView?>
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        nextbtn = findViewById(R.id.nextbtn)
        skipbtn = findViewById(R.id.skip)

        nextbtn.setOnClickListener {
            if (getItem(0) < 2) {
                mSLideViewPager.setCurrentItem(getItem(1), true)
            } else {
                val i = Intent(this, Signup::class.java)
                startActivity(i)
                markOnBoardingAsCompleted()
                finish()
            }
        }

        skipbtn.setOnClickListener {
            val i = Intent(this, Signup::class.java)
            startActivity(i)
            markOnBoardingAsCompleted()
            finish()
        }
        mSLideViewPager = findViewById(R.id.viewpager)
        mDotLayout = findViewById(R.id.indicator_layout)

        viewPagerAdapter = ViewPagerAdapter(this)
        mSLideViewPager.adapter = viewPagerAdapter

        setUpIndicator(0)
        mSLideViewPager.addOnPageChangeListener(viewListener)
    }

    private fun setUpIndicator(position: Int) {
        dots = arrayOfNulls(3)
        mDotLayout.removeAllViews()
        for (i in 0 until dots.size) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226")
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(resources.getColor(R.color.red, applicationContext.theme))
            mDotLayout.addView(dots[i])
        }
        dots[position]?.setTextColor(resources.getColor(R.color.black, applicationContext.theme))
    }

    private val viewListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        override fun onPageSelected(position: Int) {
            setUpIndicator(position)
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    private fun getItem(i: Int): Int {
        return mSLideViewPager.currentItem + i
    }
    private fun markOnBoardingAsCompleted(){
        val userDetails = this.getSharedPreferences("OnboardingPrefs", MODE_PRIVATE)
        val editor = userDetails.edit()
        editor.putBoolean("onboarding_completed",true)
        editor.apply()
    }
}
