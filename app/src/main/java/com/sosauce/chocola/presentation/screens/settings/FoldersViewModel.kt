package com.sosauce.chocola.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.data.models.Folder
import com.sosauce.chocola.domain.repository.FoldersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FoldersViewModel(
    private val foldersRepository: FoldersRepository
) : ViewModel() {

    private val _folders = MutableStateFlow(emptyList<Folder>())
    val folders = _folders.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _folders.update { foldersRepository.fetchFoldersWithMusics() }
        }
    }

}