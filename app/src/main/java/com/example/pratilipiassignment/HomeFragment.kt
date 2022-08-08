package com.example.pratilipiassignment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pratilipiassignment.adapter.NotesAdapter
import com.example.pratilipiassignment.databinding.FragmentHomeBinding
import com.example.pratilipiassignment.viewmodel.NoteViewModel
import kotlin.collections.ArrayList

class HomeFragment : BaseFragment() {

    var arrNotes = ArrayList<Notes>()
    var notesAdapter: NotesAdapter = NotesAdapter()
    lateinit var binding : FragmentHomeBinding
    lateinit var viewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NoteViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.setHasFixedSize(true)

        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)

        viewModel.getAllNotes()

        attachObserver()

        attachListeners()

    }


    private fun attachListeners()
    {
        notesAdapter.setOnClickListener(onClicked)

        binding.fabBtnCreateNote.setOnClickListener {
            replaceFragment(CreateNoteFragment.newInstance(),false)
        }
    }

    private fun attachObserver()
    {
        viewModel.notes.observe(viewLifecycleOwner)
        {
            notesAdapter.setData(it)
            arrNotes = it as ArrayList<Notes>
            binding.recyclerView.adapter = notesAdapter
        }

        binding.searchView.setOnQueryTextListener( object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                var tempArr = ArrayList<Notes>()

                for (arr in arrNotes){
                    if (arr.title!!.lowercase().contains(p0.toString())){
                        tempArr.add(arr)
                    }
                }

                notesAdapter.setData(tempArr)
                notesAdapter.notifyDataSetChanged()
                return true
            }

        })
    }

    private val onClicked = object :NotesAdapter.OnItemClickListener{
        override fun onClicked(noteId: Int) {


            var fragment :Fragment
            var bundle = Bundle()
            bundle.putInt("noteId",noteId)
            fragment = CreateNoteFragment.newInstance()
            fragment.arguments = bundle

            replaceFragment(fragment,false)
        }

    }


    fun replaceFragment(fragment:Fragment, istransition:Boolean){
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()

        if (istransition){
            fragmentTransition.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_in_left)
        }
        fragmentTransition.replace(R.id.frame_layout,fragment).addToBackStack(fragment.javaClass.simpleName).commit()
    }


}