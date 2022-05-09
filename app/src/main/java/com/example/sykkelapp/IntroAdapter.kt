package com.example.sykkelapp

import android.content.Context
import androidx.viewpager.widget.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class IntroAdapter(var context: Context) : PagerAdapter() {
    var layoutInflater: LayoutInflater? = null

    var layouts = intArrayOf(
        R.layout.screen_one,
        R.layout.screen_two,
        R.layout.screen_three
    )

    //returns the number of layouts
    override fun getCount(): Int {
        return layouts.size
    }

    //assign the view to the object
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    //returns the layout at the given position of the view pager
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(layouts[position], container, false)
        container.addView(view)
        return view
    }

    //stop slide on last screen
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }
}