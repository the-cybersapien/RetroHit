package xyz.cybersapien.retrohit.activities.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.cybersapien.retrohit.R

/**
 * Created by ogcybersapien on 6/8/17.
 */

class ResultsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_result, container, false)
        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private val ARG_SECTION_NUMBER = "section_number"
        private var resultFragment: ResultsFragment? = null

        /**
         * Gets the instance of this Fragment
         */
        fun getInstance(): ResultsFragment {
            resultFragment = resultFragment ?: ResultsFragment()
            return resultFragment as ResultsFragment
        }
    }
}