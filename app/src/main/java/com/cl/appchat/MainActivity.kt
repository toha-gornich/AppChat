package com.cl.appchat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.cl.appchat.databinding.ActivityMainBinding
import com.cl.appchat.databinding.FragmentMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private var firebaseAuth = FirebaseAuth.getInstance()

    private var mSectionPagerAdapter: SectionPagerAdapter? = null
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        setSupportActionBar(binding!!.toolbar)
        mSectionPagerAdapter = SectionPagerAdapter(supportFragmentManager)

        binding!!.container.adapter = mSectionPagerAdapter

        binding!!.fab.setOnClickListener{view:View ->Snackbar.make(view, "Replace with action", Snackbar.LENGTH_SHORT).setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_logout ->onLogout()
        }
//        val id:Int? = item.itemId
//        if (id==R.id.action_settings){
//            return  true
//        }

        return super.onOptionsItemSelected(item)
    }

    fun onLogout(){firebaseAuth.signOut()
    startActivity(LoginActivity.newIntent(this))
    finish()}


    inner class SectionPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return 3
        }

        override fun getItem(position: Int): Fragment {
            return PlaceholderFragment.newIntent(position + 1)

        }

    }


    class PlaceholderFragment : Fragment() {
        var binding: FragmentMainBinding? = null
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = FragmentMainBinding.inflate(inflater, container, false)
            val rootView: View = binding!!.root
            binding!!.sectionLabel.text =
                "Hello World from section${arguments?.getInt(ARG_SECTION_NUMBER)}"
            return rootView
        }

        companion object {
            private const val ARG_SECTION_NUMBER = "Section number"
            fun newIntent(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }


    }
    companion object{
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}