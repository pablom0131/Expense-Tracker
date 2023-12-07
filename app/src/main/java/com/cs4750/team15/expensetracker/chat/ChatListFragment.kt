package com.cs4750.team15.expensetracker.chat

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs4750.team15.expensetracker.databinding.FragmentChatBinding
import kotlinx.coroutines.launch

class ChatFragment: Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        binding.recyclerGchat.layoutManager = LinearLayoutManager(context)

        val messages = chatViewModel.messages
        val adapter = ChatListAdapter(messages)
        binding.recyclerGchat.adapter = adapter


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            buttonGchatSend.setOnClickListener {
                hideKeyboard()
                showNewMessage()
                getResponse()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.fragment_expense_list, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.new_expense -> {
//                showNewExpense()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    private fun showNewMessage() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.editGchatMessage.setBackgroundColor(0)
            val newMessage = ChatMessage(
                user = "You",
                msgContent = "You said: " + binding.editGchatMessage.text.toString()
                )
            binding.editGchatMessage.text.clear()
            chatViewModel.addMessage(newMessage)
        }
    }

    private fun getResponse() {
        viewLifecycleOwner.lifecycleScope.launch {
            val newMessage = ChatMessage(
                user = "PennyWise",
                msgContent = "PennyWise says: ur a dumbass"
            )

            chatViewModel.addMessage(newMessage)
        }
    }
    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}