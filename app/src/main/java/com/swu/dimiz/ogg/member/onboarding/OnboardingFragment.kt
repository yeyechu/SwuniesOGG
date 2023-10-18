package com.swu.dimiz.ogg.member.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentOnboardingSlidBinding
import com.swu.dimiz.ogg.oggdata.OggDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingSlidBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: OnboardingAdapter
    private lateinit var pager: ViewPager

    private var isCheck = true

    init {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                Timber.i("로딩 중")
                binding.imageLoading.visibility = View.VISIBLE
                isCheck = withContext(Dispatchers.Default) {
                    OggDatabase.getInstance(requireContext()).uiDatabaseDao.onBoarding() == 1
                }
                Timber.i("온보딩 여부 확인: $isCheck")
                binding.imageLoading.visibility = View.GONE
                if(isCheck) {
                    findNavController().popBackStack()
                    Timber.i("온보딩 여부 건너뜀")
                }
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingSlidBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = OnboardingAdapter(requireContext())
        pager = binding.viewpager
        binding.viewpager.adapter = adapter

        binding.includedLayoutPermission.permissionLayout.visibility = View.VISIBLE

        binding.includedLayoutPermission.button.setOnClickListener {
            binding.includedLayoutPermission.permissionLayout.visibility = View.GONE
            binding.btnGetStarted.visibility = View.VISIBLE
        }

        binding.btnGetStarted.setOnClickListener {
            navigateToSignin()
            viewLifecycleOwner.lifecycleScope.launch {
                //OggDatabase.getInstance(requireContext()).uiDatabaseDao.upBoarding()
                Toast.makeText(context, "온보딩 봤다고 테스트 중이라서 안 저장", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToSignin() {
        Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
            .navigate(OnboardingFragmentDirections
                .actionOnboardingFragmentToSigninFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
