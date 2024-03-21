package com.example.photoeditor.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants
import com.example.photoeditor.databinding.FragmentMainBinding
import com.example.photoeditor.utils.getTempFileUri
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

class MainFragment : Fragment() {

    lateinit var mBinding: FragmentMainBinding

    private var mUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize MobileAds SDK.
        MobileAds.initialize(requireContext()) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = FragmentMainBinding.inflate(inflater, container, false)

        mBinding.ivEdit.setOnClickListener {
            launchGallery()
        }

        mBinding.ivCamera.setOnClickListener {
            launchCamera()
        }

        loadAds()

        return mBinding.root
    }


    /**
     * Method will launch Content Launcher to choose images.
     */
    private fun launchGallery() {
        selectImageLauncher.launch("image/*") // Launch the selectImageLauncher with "image/*" type(only images).
    }

    /**
     * Method will launch Camera to take picture.
     */
    private fun launchCamera() {
        mUri = getTempFileUri(requireContext())
        cameraLauncher.launch(mUri) // Launch camera with the temp uri.
    }

    /**
     * Load the test ads.
     */
    private fun loadAds() {
        val adRequest = AdRequest.Builder().build()
        mBinding.adView.loadAd(adRequest)
    }

    // Content Launcher.
    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                launchPhotoEditor(it)
            }
        }

    // Camera Launcher.
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                mUri?.let {
                    launchPhotoEditor(it)
                }
            }
        }

    // DS Editor Launcher
    private val photoEditorLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.data?.let {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToResultFragment(
                        it.data.toString()
                    )
                )
            }
        }

    /**
     * Method will launch the DS Editor Activity with given uri as data.
     */
    private fun launchPhotoEditor(uri: Uri) {
        val photoEditorIntent = Intent(requireContext(), DsPhotoEditorActivity::class.java)
        photoEditorIntent.data = uri
        photoEditorIntent.putExtra(
            DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY,
            "PHOTO EDITOR"
        );
        val toolsToHide =
            intArrayOf(DsPhotoEditorActivity.TOOL_ORIENTATION)
        photoEditorIntent.putExtra(
            DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE,
            toolsToHide
        )
        photoEditorLauncher.launch(photoEditorIntent)
    }

}