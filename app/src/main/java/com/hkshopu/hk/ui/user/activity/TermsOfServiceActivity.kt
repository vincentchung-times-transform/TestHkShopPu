package com.HKSHOPU.hk.ui.user.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.databinding.ActivityTermsOfServiceBinding
import com.HKSHOPU.hk.ui.user.fragment.*
import com.HKSHOPU.hk.ui.user.fragmentdialog.BottomSheeFragment

class TermsOfServiceActivity : AppCompatActivity(),  BottomSheeFragment.OnDialogButtonFragmentListener {

    private lateinit var binding: ActivityTermsOfServiceBinding

    private val termsOfServiceFragment = TermsOfServiceFragment()
    private val privacyPolicyFragment = PrivacyPolicyFragment()
    private val salesPolicyFragment = SalesPolicyFragment()
    private val intellectualPropertyFragment = IntellectualPropertyFragment()
    private val disclaimerFragment = DisclaimerFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsOfServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initIntent()
        initView()
        initVM()

    }

    private fun initIntent() {


    }

    private fun  initVM() {

    }
    private fun initView() {

        //fragment
        fun addFragment(f: Fragment) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, f)
            transaction.commit()
        }

        addFragment(termsOfServiceFragment)
        addFragment(privacyPolicyFragment)
        addFragment(salesPolicyFragment)
        addFragment(intellectualPropertyFragment)
        addFragment(disclaimerFragment)

        initClick()
    }


    private fun initClick() {

        binding.xCancel.setOnClickListener {
            finish()
        }

        fun replaceFragment(f : Fragment){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, f)
            transaction.commit()
        }

        //fragmentlist
        var list = listOf<Fragment>(termsOfServiceFragment, privacyPolicyFragment, salesPolicyFragment, intellectualPropertyFragment, disclaimerFragment)


        //spinner
        val spinner = binding.spinner
        val adapter = ArrayAdapter.createFromResource(this, R.array.array_terms, android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                replaceFragment(list.get(position))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

//                val bottomSheetFragment = BottomSheeFragment()
//                bottomSheetFragment.listener = this
//                bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)

        }

    }

    override fun onSelectDialog(select: String) {
        TODO("Not yet implemented")
    }


}