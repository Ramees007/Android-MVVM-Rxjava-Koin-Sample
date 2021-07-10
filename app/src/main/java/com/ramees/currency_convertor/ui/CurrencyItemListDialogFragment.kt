package com.ramees.currency_convertor.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ramees.currency_convertor.data.CurrencyItem
import com.ramees.currency_convertor.databinding.CurrencyDialogItemBinding
import com.ramees.currency_convertor.databinding.FragmentCurrencyDialogBinding
import com.ramees.currency_convertor.util.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class CurrencyItemListDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCurrencyDialogBinding? = null

    private val homeVM: HomeVM by sharedViewModel()


    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.peekHeight = Utils.screenHeight() / 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCurrencyDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        homeVM.currencyLD.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.pb.gone()
                    binding.list.run {
                        visible()
                        layoutManager = LinearLayoutManager(context)
                        adapter = CurrencyItemAdapter(it.data)
                    }
                }

                is Resource.Loading -> {
                    binding.pb.visible()
                }

                is Resource.Error -> {
                    binding.pb.gone()
                    dismiss()
                    it.error.errorForUser(requireContext()).toast(requireContext())
                }

            }
        }

        if (homeVM.currencyLD.value == null || !homeVM.currencyLD.value!!.succeeded) homeVM.getCurrencies()


    }


    private inner class CurrencyItemAdapter(private val items: List<CurrencyItem>) :
        RecyclerView.Adapter<CurrencyItemAdapter.CurrencyVH>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyVH {

            return CurrencyVH(
                CurrencyDialogItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        }

        override fun onBindViewHolder(holder: CurrencyVH, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int {
            return items.size
        }

        private inner class CurrencyVH constructor(binding: CurrencyDialogItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
            private val countryTV: TextView = binding.text

            init {
                binding.root.setOnClickListener {
                    homeVM.selectCurrency(items[bindingAdapterPosition].currencyCode)
                    dismiss()
                }
            }

            fun bind(currencyItem: CurrencyItem) {
                countryTV.text = currencyItem.country
            }


        }
    }

    companion object {
        const val TAG = "currency_list"
        fun show(fm: FragmentManager) = CurrencyItemListDialogFragment().show(fm, TAG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}