package com.cs4750.team15.expensetracker.spendinganalysis

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cs4750.team15.expensetracker.R
import com.cs4750.team15.expensetracker.databinding.FragmentSpendingAnalysisBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import java.text.DecimalFormat

class SpendingAnalysisFragment : Fragment() {
    private var _binding: FragmentSpendingAnalysisBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding"
        }

    private val spendingAnalysisViewModel: SpendingAnalysisViewModel by viewModels()

    lateinit var pieChart: PieChart
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSpendingAnalysisBinding.inflate(inflater, container, false)
        pieChart = binding.pieChart
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val expenses = spendingAnalysisViewModel.getExpenses()

        val amountPerCategory = hashMapOf(
            "Groceries" to 0.0f,
            "Transportation" to 0.0f,
            "Entertainment" to 0.0f,
            "Shopping" to 0.0f,
            "Travel" to 0.0f,
            "Misc/Other" to 0.0f
        )

        for (expense in expenses){
            val amount = expense.amount.toFloat()
            when (val category = expense.category) {
                "Groceries" -> amountPerCategory[category] = amountPerCategory[category]!! + amount
                "Transportation" -> amountPerCategory[category] = amountPerCategory[category]!! + amount
                "Entertainment" -> amountPerCategory[category] = amountPerCategory[category]!! + amount
                "Shopping" -> amountPerCategory[category] = amountPerCategory[category]!! + amount
                "Travel" -> amountPerCategory[category] = amountPerCategory[category]!! + amount
                "Misc/Other" -> amountPerCategory[category] = amountPerCategory[category]!! + amount
            }
        }

        // on below line we are initializing our
        // variable with their ids.

        // on below line we are setting user percent value,
        // setting description as enabled and offset for pie chart
        pieChart.setUsePercentValues(true)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        // on below line we are setting drag for our pie chart
        pieChart.setDragDecelerationFrictionCoef(0.95f)

        // on below line we are setting hole
        // and hole color for pie chart
        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.WHITE)

        // on below line we are setting circle color and alpha
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        // on  below line we are setting hole radius
        pieChart.setHoleRadius(35f)
        pieChart.setTransparentCircleRadius(45f)

        // on below line we are setting center text
        pieChart.setDrawCenterText(true)

        // on below line we are setting
        // rotation for our pie chart
        pieChart.setRotationAngle(0f)

        // enable rotation of the pieChart by touch
        pieChart.setRotationEnabled(true)
        pieChart.setHighlightPerTapEnabled(true)

        // on below line we are setting animation for our pie chart
        pieChart.animateY(1600, Easing.EaseInOutQuad)

        // on below line we are disabling our legend for pie chart
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        // on below line we are creating array list and
        // adding data to it to display in pie chart
        binding.apply {
            catId1.visibility = View.INVISIBLE
            catId2.visibility = View.INVISIBLE
            catId3.visibility = View.INVISIBLE
            catId4.visibility = View.INVISIBLE
            catId5.visibility = View.INVISIBLE
            catId6.visibility = View.INVISIBLE
        }

        val entries: ArrayList<PieEntry> = ArrayList()
        var catID = 1
        amountPerCategory.forEach { (key, value) ->
            if (value > 0.0f) {
                entries.add(PieEntry(value))
                binding.apply {
                    when (catID) {
                        1 -> {
                            catId1.text = key
                            catId1.visibility = View.VISIBLE
                        }
                        2 -> {
                            catId2.text = key
                            catId2.visibility = View.VISIBLE
                        }
                        3 -> {
                            catId3.text = key
                            catId3.visibility = View.VISIBLE
                        }
                        4 -> {
                            catId4.text = key
                            catId4.visibility = View.VISIBLE
                        }
                        5 -> {
                            catId5.text = key
                            catId5.visibility = View.VISIBLE
                        }
                        6 -> {
                            catId6.text = key
                            catId6.visibility = View.VISIBLE
                        }
                    }
                }
                catID++
            }
        }

        // on below line we are setting pie data set
        val dataSet = PieDataSet(entries, "Mobile OS")

        // on below line we are setting icons.
        dataSet.setDrawIcons(false)

        // on below line we are setting slice for pie
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors to list
        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.saffron))
        colors.add(resources.getColor(R.color.teal_200))
        colors.add(resources.getColor(R.color.red))
        colors.add(resources.getColor(R.color.purple_200))
        colors.add(resources.getColor(R.color.yellow))
        colors.add(resources.getColor(R.color.green))

        // on below line we are setting colors.
        dataSet.colors = colors

        // on below line we are setting pie data set
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.setData(data)

        // undo all highlights
        pieChart.highlightValues(null)

        // loading chart
        pieChart.invalidate()

        binding.apply {
            amountSpentEntertainment.text = amountPerCategory["Entertainment"]?.let { formatFloat(it) }
            amountSpentGroceries.text = amountPerCategory["Groceries"]?.let { formatFloat(it) }
            amountSpentMisc.text = amountPerCategory["Misc/Other"]?.let { formatFloat(it) }
            amountSpentShopping.text = amountPerCategory["Shopping"]?.let { formatFloat(it) }
            amountSpentTravel.text = amountPerCategory["Travel"]?.let { formatFloat(it) }
            amountSpentTransportation.text = amountPerCategory["Transportation"]?.let { formatFloat(it) }

            var total = 0.0f
            amountPerCategory.forEach { (key, value) ->
                total += value
            }
            spendingPercentageEntertainment.text =
                amountPerCategory["Entertainment"]?.let { calculatePercentage(it, total) }
                    ?.let { formatFloat(it) }
            spendingPercentageGroceries.text =
                amountPerCategory["Groceries"]?.let { calculatePercentage(it, total) }
                    ?.let { formatFloat(it) }
            spendingPercentageMisc.text =
                amountPerCategory["Misc/Other"]?.let { calculatePercentage(it, total) }
                    ?.let { formatFloat(it) }
            spendingPercentageShopping.text =
                amountPerCategory["Shopping"]?.let { calculatePercentage(it, total) }
                    ?.let { formatFloat(it) }
            spendingPercentageTravel.text =
                amountPerCategory["Travel"]?.let { calculatePercentage(it, total) }
                    ?.let { formatFloat(it) }
            spendingPercentageTransportation.text =
                amountPerCategory["Transportation"]?.let { calculatePercentage(it, total) }
                    ?.let { formatFloat(it) }

        }
    }
    private fun formatFloat(amount: Float): String{
        return String.format("%.2f", amount)
    }
    private fun calculatePercentage(amount: Float, total: Float): Float {
        return String.format("%.2f", (amount/total) * 100).toFloat()
    }
}