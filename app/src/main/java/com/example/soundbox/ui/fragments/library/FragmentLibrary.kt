package com.example.soundbox.ui.fragments.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.LibraryManager
import com.example.mediaplayer.model.Song
import com.example.soundbox.R
import com.example.soundbox.databinding.FragmentLibraryBinding
import com.example.soundbox.ui.adapters.LibraryRecycleViewAdapter
import com.example.soundbox.ui.adapters.models.BaseRecycleViewItem
import com.example.soundbox.ui.adapters.models.SongRecyclerViewItem

class FragmentLibrary : Fragment() {
   private lateinit var binding: FragmentLibraryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLibraryBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerAdapter(view)
    }
    private   fun  initRecyclerAdapter(view: View){
        val rv :RecyclerView =view.findViewById(R.id.rv_LibraryFragment)
        val songs : List<Song> = LibraryManager.getSongs(this.requireContext())
        val items: MutableList<BaseRecycleViewItem> = mutableListOf()
        songs.forEach { song->
            items.add(SongRecyclerViewItem(song))
        }
        val adapter = LibraryRecycleViewAdapter(items)
        rv.layoutManager = LinearLayoutManager(this.requireContext())
        rv.adapter =adapter

    }
}