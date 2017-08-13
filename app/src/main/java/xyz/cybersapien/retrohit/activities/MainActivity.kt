package xyz.cybersapien.retrohit.activities

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Headers
import okhttp3.ResponseBody
import xyz.cybersapien.retrohit.R
import xyz.cybersapien.retrohit.activities.fragments.RequestFragment
import xyz.cybersapien.retrohit.activities.fragments.ResultsFragment

class MainActivity : AppCompatActivity() {

    val LOG_TAG = "MainActivity"
    private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter
    lateinit var resultHeaders: Headers
    lateinit var responseBody: ResponseBody
    lateinit var floatingActionButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
        container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                when (position) {
                    0 -> {
                        floatingActionButton.show()
                    }
                    else -> {
                        floatingActionButton.hide()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                // Kucch na kr bhai, optional kyu nhi hai tu
            }

            override fun onPageSelected(position: Int) {
                // Kucch na kr bhai tu bhi, option nhi ho skta tha kya!
            }

        })
        tabs.setupWithViewPager(container)
        tabs.getTabAt(0)?.text = "Make Request"
        tabs.getTabAt(1)?.text = "Results"

        floatingActionButton = fab
    }

    inner class SectionsPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

        override fun getItem(position: Int): Fragment? =
                when (position) {
                    0 -> {
                        RequestFragment.getInstance()
                    }
                    1 -> {
                        ResultsFragment.getInstance()
                    }
                    else -> null
                }

        override fun getCount(): Int {
            // 2 Total Pages
            return 2
        }

    }
}
