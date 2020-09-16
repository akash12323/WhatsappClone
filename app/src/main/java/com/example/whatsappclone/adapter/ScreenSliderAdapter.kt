package com.example.whatsappclone.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.whatsappclone.fragments.InboxFragment
import com.example.whatsappclone.fragments.PeopleFragment

class ScreenSliderAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {

        return when(position) {
            0 -> {
                InboxFragment()
            }
            else -> {
                return PeopleFragment()
            }
            // this method set our tabs position
        }
    }

    override fun getCount(): Int {      // this method return 3 tabs
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {            // this method set the titles of our tabs
        return when(position) {
            0 -> "CHATS"
            else -> {
                return "PEOPLE"
            }
        }
    }
}
