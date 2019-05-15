package com.ymlee.bottomview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_test.*

/**
 * <p> Copyright  2019 深圳市三三得玖教育科技有限公司所有权 </p>
 * Author: liyimin
 * Time: 2019/5/15 0015 15:39
 */
class FragmentTest: Fragment() {
    companion object{
        fun newInstance(text: String): FragmentTest{
            val fragment = FragmentTest()
            val bundle = Bundle()
            bundle.putString("text", text)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv.text = arguments?.getString("text")
    }
}