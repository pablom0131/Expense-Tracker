package com.cs4750.team15.expensetracker.expenselist

import android.app.AlertDialog
import android.content.Context
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import android.text.format.DateFormat
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.content.SharedPreferences
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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

    private val args: ExpenseDetailFragmentArgs by navArgs()

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

    private val handler = Handler()
    private val updateDelayMillis = 1000

    private lateinit var currTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        binding.apply {
            expenseTitle.doOnTextChanged { text, _, _, _ ->
                currTitle = text.toString()
                expenseDetailViewModel.updateExpense { oldExpense ->
                    oldExpense.copy(title = text.toString())
                }
            }

            expenseAmount.doOnTextChanged { text, _, _, _ ->
                handler.removeCallbacksAndMessages(null)

                handler.postDelayed({
                    expenseDetailViewModel.updateExpense { oldExpense ->
                        oldExpense.copy(amount = if (text.toString().isBlank()) 0.0 else text.toString().toDouble())
                    }
                }, updateDelayMillis.toLong())
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

            receiptCamera.setOnClickListener {
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
            receiptCamera.isEnabled = canResolveIntent(captureImageIntent)
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

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currTitle.isNotEmpty())
                    findNavController().popBackStack()
                else{
                    val alertDialog = AlertDialog.Builder(context)
                        .setMessage("Expense title cannot be empty")
                        .setPositiveButton("OK") {dialog, _, ->
                            dialog.dismiss()
                        }
                        .create()
                    alertDialog.show()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
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
                    ExpenseDetailFragmentDirections.selectDate(
                        expense.date
                    )
                )
            }
            if (expense.amount != 0.0) expenseAmount.setText(expense.amount.toString())
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_expense_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_expense -> {
                deleteExpense()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun deleteExpense() {
        viewLifecycleOwner.lifecycleScope.launch {
            expenseDetailViewModel.deleteExpense()
            findNavController().popBackStack()
        }
    }
}