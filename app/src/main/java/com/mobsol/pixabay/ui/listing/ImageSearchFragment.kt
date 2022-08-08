package com.mobsol.pixabay.ui.listing

import android.app.AlertDialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobsol.pixabay.R
import com.mobsol.pixabay.databinding.FragmentImageSearchBinding
import com.mobsol.pixabay.model.ImageItem
import com.mobsol.pixabay.util.Utils
import com.mobsol.pixabay.util.VerticalGridItemDecoration
import com.mobsol.pixabay.viewmodel.ImageSearchViewModel
import com.mobsol.pixabay.viewmodel.UiAction
import com.mobsol.pixabay.viewmodel.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class ImageSearchFragment : Fragment() {

    private var _binding: FragmentImageSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: ImageSearchViewModel by viewModels()

    private var columnCount = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // bind the state
        binding.bindState(
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            uiActions = viewModel.accept
        )

    }

    fun onImageItemClicked(imageItem: ImageItem) {
        showImageDetailConfirmationDialog(imageItem)
    }

    fun showImageDetailConfirmationDialog(imageItem: ImageItem) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.lbl_image_details)
            .setMessage(R.string.lbl_click_to_see_detail)
            .setPositiveButton(
                R.string.lbl_ok
            ) { dialog, whichButton ->
                findNavController().navigate(
                    ImageSearchFragmentDirections.actionImageSearchFragmentToImageDetailFragment(
                        imageItem
                    )
                )
            }
            .setNegativeButton(R.string.lbl_cancel, null).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Binds the [UiState] provided  by the [SearchRepositoriesViewModel] to the UI,
     * and allows the UI to feed back user actions to it.
     */
    private fun FragmentImageSearchBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<ImageItem>>,
        uiActions: (UiAction) -> Unit
    ) {
        val imageListAdapter = ImageListAdapter { imageItem ->
            onImageItemClicked(imageItem)
        }

        rvImageList.apply {
            val footerAdapter = ImageLoadStateAdapter { imageListAdapter.retry() }
            val gridLayoutManager = GridLayoutManager(context, columnCount).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position == imageListAdapter.itemCount && footerAdapter.itemCount > 0) {
                            2
                        } else {
                            1
                        }
                    }
                }
            }
            addItemDecoration(VerticalGridItemDecoration(columnCount, 20))
            layoutManager = gridLayoutManager
            adapter = imageListAdapter.withLoadStateFooter(
                footer = footerAdapter
            )
        }

        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindList(
            imageListAdapter = imageListAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }


    private fun FragmentImageSearchBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        etSearchImage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateImageListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        etSearchImage.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateImageListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect(etSearchImage::setText)
        }
    }

    private fun FragmentImageSearchBinding.bindList(
        imageListAdapter: ImageListAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<ImageItem>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        rvImageList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })

        layoutError.btnRetry.setOnClickListener { imageListAdapter.retry() }

        val notLoading = imageListAdapter.loadStateFlow
            // Only emit when REFRESH LoadState for the paging source changes.
            .distinctUntilChangedBy { it.source.refresh }
            // Only react to cases where REFRESH completes i.e., NotLoading.
            .map { it.source.refresh is LoadState.NotLoading }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        lifecycleScope.launch {
            pagingData.collectLatest(imageListAdapter::submitData)
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) rvImageList.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            imageListAdapter.loadStateFlow.collect { loadState ->
                rvImageList.isVisible =
                    !(loadState.refresh is LoadState.Error || loadState.refresh is LoadState.Loading)
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && imageListAdapter.itemCount == 0
                tvNoImageFound.isVisible = isListEmpty
                // Show loading spinner during initial load or refresh.
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                // Show the retry state if initial load or refresh fails.
                layoutError.root.isVisible = loadState.source.refresh is LoadState.Error
            }
        }
    }

    private fun FragmentImageSearchBinding.updateImageListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        etSearchImage.text?.trim().let {
            if (it?.isNotEmpty() == true) {
                rvImageList.scrollToPosition(0)
                Utils.hideSoftKeyboard(requireActivity())
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }

    companion object {
        const val TAG = "ImageSearchFragment"
    }
}
