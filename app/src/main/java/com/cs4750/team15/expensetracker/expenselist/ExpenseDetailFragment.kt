package com.cs4750.team15.expensetracker.expenselist

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cs4750.team15.expensetracker.databinding.FragmentExpenseDetailBinding
import kotlinx.coroutines.launch
import android.text.format.DateFormat
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.content.SharedPreferences
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import com.cs4750.team15.expensetracker.R
import java.io.File
import java.util.Date

private const val BUTTON_DATE_FORMAT = "MMMM dd, yyyy"

class ExpenseDetailFragment: Fragment() {

    private var _binding: FragmentExpenseDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val args: com.cs4750.team15.expensetracker.expenselist.ExpenseDetailFragmentArgs by navArgs()

    private val expenseDetailViewModel: ExpenseDetailViewModel by viewModels {
        ExpenseDetailViewModelFactory(args.expenseId)
    }

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto && photoName != null) {
            expenseDetailViewModel.updateExpense { oldExpense ->
                oldExpense.copy(photoFileName = photoName)
            }
        }
    }

    private var photoName: String? = null

    private lateinit var sharedPreferences: SharedPreferences
    private val SPINNER_PREF_KEY = "spinner_selected_item"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExpenseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            expenseTitle.doOnTextChanged { text, _, _, _ ->
                expenseDetailViewModel.updateExpense { oldExpense ->
                    oldExpense.copy(title = text.toString())
                }
            }

            expenseAmount.doOnTextChanged { text, _, _, _ ->
                expenseDetailViewModel.updateExpense { oldExpense ->
                    oldExpense.copy(amount = text.toString().toDouble())
                }
            }

            val spinner = expenseCategory
            val spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.expense_categories_array,
                android.R.layout.simple_spinner_item
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            spinner.adapter = spinnerAdapter

            val savedPosition = sharedPreferences.getInt(SPINNER_PREF_KEY, 0)
            spinner.setSelection(savedPosition)

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent?.getItemAtPosition(position).toString()
                    with(sharedPreferences.edit()) {
                        putInt(SPINNER_PREF_KEY, position)
                        apply()
                    }
                    expenseDetailViewModel.updateExpense { oldExpense ->
                        oldExpense.copy(category = selectedItem)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

            expenseReceiptPhoto.setOnClickListener {
                photoName = "IMG_${Date()}.JPG"
                val photoFile = File(requireContext().applicationContext.filesDir, photoName)
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.cs4750.team15.expensetracker.fileprovider",
                    photoFile
                )

                takePhoto.launch(photoUri)
            }

            val captureImageIntent = takePhoto.contract.createIntent(
                requireContext(),
                Uri.parse("")
            )
            expenseReceiptPhoto.isEnabled = canResolveIntent(captureImageIntent)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                expenseDetailViewModel.expense.collect {expense ->
                    expense?.let { updateUi(it) }
                }
            }
        }

        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ) { _, bundle ->
            val newDate =
                bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            expenseDetailViewModel.updateExpense { it.copy(date = newDate) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUi(expense: Expense){
        binding.apply {
            if (expenseTitle.text.toString() != expense.title)
                expenseTitle.setText(expense.title)
            expenseDate.text = DateFormat.format(BUTTON_DATE_FORMAT, expense.date)
            expenseDate.setOnClickListener {
                findNavController().navigate(
                    com.cs4750.team15.expensetracker.expenselist.ExpenseDetailFragmentDirections.selectDate(
                        expense.date
                    )
                )
            }
            if (expenseAmount.text.toString().toDouble() != expense.amount)
                expenseAmount.setText(expense.amount.toString())
            updatePhoto(expense.photoFileName)
        }
    }

    private fun canResolveIntent(intent: Intent): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        val resolvedActivity: ResolveInfo? =
            packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return resolvedActivity != null
    }

    private fun updatePhoto(photoFileName: String?){
        if (binding.expenseReceiptPhoto.tag != photoFileName){
            val photoFile = photoFileName?.let {
                File(requireContext().applicationContext.filesDir, it)
            }

            if (photoFile?.exists() == true){
                binding.expenseReceiptPhoto.doOnLayout { measuredView ->
                    val scaledBitmap = getScaledBitmap(
                        photoFile.path,
                        measuredView.width,
                        measuredView.height
                    )
                    binding.expenseReceiptPhoto.setImageBitmap(scaledBitmap)
                    binding.expenseReceiptPhoto.tag = photoFileName
                }
            } else {
                binding.expenseReceiptPhoto.setImageBitmap(null)
                binding.expenseReceiptPhoto.tag = null
            }
        }
    }
}