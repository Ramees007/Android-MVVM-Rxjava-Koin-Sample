package com.ramees.currency_convertor.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramees.currency_convertor.data.ConvertedItem
import com.ramees.currency_convertor.databinding.ActivityHomeBinding
import com.ramees.currency_convertor.util.Resource
import com.ramees.currency_convertor.util.errorForUser
import com.ramees.currency_convertor.util.gone
import com.ramees.currency_convertor.util.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity() {

    private val homeVM: HomeVM by viewModel()
    private val convertedCurrencies = mutableListOf<ConvertedItem>()

    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.amtET.addTextChangedListener {
            homeVM.enterAmount(it?.toString() ?: "")
        }

        binding.currencyDropDwn.setOnClickListener {
            CurrencyItemListDialogFragment.show(supportFragmentManager)
        }

        setRV()

        homeVM.convertedLiveDate.observe(this) {
            when (it) {
                is Resource.Success -> {
                    binding.pb.hide()
                    binding.errorTV.gone()
                    binding.convertedList.visible()
                    convertedCurrencies.clear()
                    convertedCurrencies.addAll(it.data)
                    binding.convertedList.adapter?.notifyDataSetChanged()
                }

                is Resource.Error -> {
                    binding.pb.hide()
                    binding.errorTV.visible()
                    binding.errorTV.text = it.error.errorForUser(this)
                    convertedCurrencies.clear()
                    binding.convertedList.adapter?.notifyDataSetChanged()
                    binding.convertedList.gone()
                }

                is Resource.Loading -> {
                    binding.errorTV.gone()
                    binding.convertedList.gone()
                    binding.pb.show()
                }
            }
        }

        homeVM.currencySelectionLD.observe(this) {
            binding.currencyDropDwn.text = "${it.displayName} (${it.currencyCode})"
        }

        binding.amtET.requestFocus()


    }

    private fun setRV() {
        binding.convertedList.layoutManager = LinearLayoutManager(this)
        binding.convertedList.adapter = ConversionAdapter(convertedCurrencies)

    }



}