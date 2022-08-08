package com.example.pratilipiassignment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.pratilipiassignment.databinding.FragmentCreateNoteBinding
import com.example.pratilipiassignment.viewmodel.NoteViewModel
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*


class CreateNoteFragment : BaseFragment(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    lateinit var binding: FragmentCreateNoteBinding
    lateinit var viewModel: NoteViewModel
//
//    val styleBold = StyleSpan(Typeface.BOLD)
//    val styleNormal = StyleSpan(Typeface.NORMAL)
//    val styleItalc = StyleSpan(Typeface.ITALIC)
//    val styleStrike = StrikethroughSpan()
//    val isBold = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(NoteViewModel::class.java)

    }

    private fun attachObservers() {
        viewModel.callSuccess.observe(viewLifecycleOwner)
        {
            if (it) {
                requireActivity().supportFragmentManager.popBackStack()
                viewModel.callSuccess.value = false
            }
        }

        viewModel.selectedColor.observe(viewLifecycleOwner)
        {
            binding.etNoteDesc.setEditorFontColor(Color.parseColor(it))
        }


    }

    private fun attachListeners() {
        binding.blue.setOnClickListener {

            binding.imgNote1.setImageResource(R.drawable.ic_tick)
            binding.imgNote2.setImageResource(0)
            binding.imgNote4.setImageResource(0)
            binding.imgNote5.setImageResource(0)
            binding.imgNote6.setImageResource(0)
            binding.imgNote7.setImageResource(0)
            viewModel.selectedColor.value = "#4e33ff"

        }

        binding.yellow.setOnClickListener {
            binding.imgNote1.setImageResource(0)
            binding.imgNote2.setImageResource(R.drawable.ic_tick)
            binding.imgNote4.setImageResource(0)
            binding.imgNote5.setImageResource(0)
            binding.imgNote6.setImageResource(0)
            binding.imgNote7.setImageResource(0)
            viewModel.selectedColor.value = "#ffd633"

        }

        binding.pink.setOnClickListener {
            binding.imgNote1.setImageResource(0)
            binding.imgNote2.setImageResource(0)
            binding.imgNote4.setImageResource(R.drawable.ic_tick)
            binding.imgNote5.setImageResource(0)
            binding.imgNote6.setImageResource(0)
            binding.imgNote7.setImageResource(0)
            viewModel.selectedColor.value = "#ae3b76"

        }

        binding.teal.setOnClickListener {
            binding.imgNote1.setImageResource(0)
            binding.imgNote2.setImageResource(0)
            binding.imgNote4.setImageResource(0)
            binding.imgNote5.setImageResource(R.drawable.ic_tick)
            binding.imgNote6.setImageResource(0)
            binding.imgNote7.setImageResource(0)
            viewModel.selectedColor.value = "#0aebaf"
        }

        binding.orange.setOnClickListener {

            binding.imgNote1.setImageResource(0)
            binding.imgNote2.setImageResource(0)
            binding.imgNote4.setImageResource(0)
            binding.imgNote5.setImageResource(0)
            binding.imgNote6.setImageResource(R.drawable.ic_tick)
            binding.imgNote7.setImageResource(0)
            viewModel.selectedColor.value = "#ff7746"
        }

        binding.black.setOnClickListener {
            binding.imgNote1.setImageResource(0)
            binding.imgNote2.setImageResource(0)
            binding.imgNote4.setImageResource(0)
            binding.imgNote5.setImageResource(0)
            binding.imgNote6.setImageResource(0)
            binding.imgNote7.setImageResource(R.drawable.ic_tick)
            viewModel.selectedColor.value = "#202734"
        }

        binding.actionBold.setOnClickListener {
            binding.etNoteDesc.setBold()
        }

        binding.actionItalic.setOnClickListener {
            binding.etNoteDesc.setItalic()
        }

        binding.actionStrikethrough.setOnClickListener {
            binding.etNoteDesc.setStrikeThrough()
        }

        binding.layoutImage.setOnClickListener {
            readStorageTask()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCreateNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateNoteFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.data = viewModel
        viewModel.noteId = requireArguments().getInt("noteId", -1)
        //resetting views
        viewModel.noteDescription=""
        viewModel.noteTitle=""
        binding.etNoteDesc.setPlaceholder("Insert text here...")
        binding.etNoteDesc.setPadding(10, 10, 10, 10)

        attachListeners()
        attachObservers()

        if (viewModel.noteId != -1) {

            launch {
                context?.let {
                    val notes =
                        NotesDatabase.getDatabase(it).noteDao().getSpecificNote(viewModel.noteId)
                    binding.etNoteTitle.setText(notes.title)
                    binding.etNoteDesc.html = notes.noteText
                    binding.imgeDelete.visibility = View.VISIBLE
                    viewModel.selectedImagePath = notes.imgPath!!
                    binding.layoutImage.visibility = View.VISIBLE
                }
            }
        }

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")

        viewModel.currentDate = sdf.format(Date())

        binding.imgDone.setOnClickListener {
            viewModel.noteDescription = binding.etNoteDesc.html
            if (viewModel.noteId != -1) {
                viewModel.updateNote()
            } else {
                viewModel.saveNote()
            }
        }

        binding.imgeDelete.setOnClickListener {
            viewModel.deleteNote()
        }

        binding.imgBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

    }

    private fun hasReadStoragePerm(): Boolean {
        return EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }


    private fun readStorageTask() {
        if (hasReadStoragePerm()) {


            pickImageFromGallery()
        } else {
            EasyPermissions.requestPermissions(
                requireActivity(),
                getString(R.string.storage_permission_text),
                viewModel.READ_STORAGE_PERM,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun pickImageFromGallery() {
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, viewModel.REQUEST_CODE_IMAGE)
        }
    }

    private fun getPathFromUri(contentUri: Uri): String? {
        var filePath: String?
        var cursor = requireActivity().contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            var index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == viewModel.REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                var selectedImageUrl = data.data
                if (selectedImageUrl != null) {
                    try {
//                        var inputStream =
//                            requireActivity().contentResolver.openInputStream(selectedImageUrl)
//                        var bitmap = BitmapFactory.decodeStream(inputStream)
                        binding.etNoteDesc.insertImage(selectedImageUrl.toString(),"",30,30)
                        binding.layoutImage.visibility = View.VISIBLE

                        viewModel.selectedImagePath = getPathFromUri(selectedImageUrl)!!
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            requireActivity()
        )
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onRationaleDenied(requestCode: Int) {

    }

    override fun onRationaleAccepted(requestCode: Int) {

    }

}

