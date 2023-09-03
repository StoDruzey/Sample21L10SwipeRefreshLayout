package com.example.sample21l10swiperefreshlayout

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.sample21l10swiperefreshlayout.databinding.FragmentFirstBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = requireNotNull(_binding)
    private var currentRequest: Call<List<User>>? = null
    private val currentUsers = mutableListOf<User>()
    private val adapter by lazy { UserAdapter(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentFirstBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            swipeRefresh.setOnRefreshListener {
                executeRequest {
                    swipeRefresh.isRefreshing = false
                }
            }
            recyclerView.adapter = adapter
            recyclerView.addItemDecoration(
                object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        outRect.bottom = 50
                    }
                }
            )



//            toolbar
//                .menu
//                .findItem(R.id.menu_search)
//                .actionView
//                .let { it as SearchView }
//                .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//                    override fun onQueryTextSubmit(query: String): Boolean {
//                        return false
//                    }
//
//                    override fun onQueryTextChange(query: String): Boolean {
//                        adapter.submitList(currentUsers.filter { it.login.contains(query) })
//                        return true
//                    }
//                })
        }
        executeRequest()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        currentRequest?.cancel()
        _binding = null
    }

    private fun handleException(e: Throwable) {
        Toast.makeText(requireContext(), e.message ?: "Something went wrong", Toast.LENGTH_SHORT).show()
    }

    private fun executeRequest(
        onRequestFinished: () -> Unit = {}
    ) {
        val finishRequest = {
            onRequestFinished()
            currentRequest = null
        }
        currentRequest?.cancel()
        currentRequest = GithubService.api
            .getUsers(1, 100)
            .apply {
                enqueue(object : Callback<List<User>> {
                    override fun onResponse(
                        call: Call<List<User>>,
                        response: Response<List<User>>
                    ) {
                        if (response.isSuccessful) {
                            val users = response.body() ?: return
                            currentUsers.addAll(users)
                            val items =
                                users.map { PagingData.Item(it) } + PagingData.Loading
                            adapter.submitList(items)
                        } else {
                            handleException(HttpException(response))
                        }
                        finishRequest()
                    }

                    override fun onFailure(call: Call<List<User>>, t: Throwable) {
                        if (!call.isCanceled) {
                            handleException(t)
                        }
                        finishRequest()
                    }
                })
            }
    }
}