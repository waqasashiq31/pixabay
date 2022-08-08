package com.mobsol.pixabay.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.FlexboxLayoutManager
import com.mobsol.pixabay.databinding.FragmentImageDetailBinding
import com.mobsol.pixabay.ui.listing.ImageTagListAdapter
import com.mobsol.pixabay.util.FlexListItemDecoration

class ImageDetailFragment : Fragment() {

    private var _binding: FragmentImageDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val args by navArgs<ImageDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentImageDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            bindUI()
            imageItem = args.imageItem
        }
    }

    private fun FragmentImageDetailBinding.bindUI() {
        bindTagList()
        ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun FragmentImageDetailBinding.bindTagList() {
        rvImageTags.apply {
            layoutManager = FlexboxLayoutManager(requireContext())
            adapter = ImageTagListAdapter(args.imageItem.tags.split(",").map { it.trim() })
            addItemDecoration(FlexListItemDecoration(10))
        }
    }

    companion object {
        const val TAG = "ImageDetailFragment"
    }
}