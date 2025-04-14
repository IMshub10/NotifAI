package com.summer.notifai.ui.search.globalsearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.summer.notifai.ui.datamodel.GlobalSearchListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalSearchViewModel @Inject constructor(
    private val globalSearchCoordinator: GlobalSearchCoordinator
) : ViewModel() {

    val searchFilter = MutableLiveData("")

    @OptIn(FlowPreview::class)
    private val selectedQuery: StateFlow<String> = searchFilter.asFlow()
        .debounce(300)
        .map { it.trim().lowercase() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private val _searchResults = MutableLiveData<List<GlobalSearchListItem>>(emptyList())
    val searchResults: LiveData<List<GlobalSearchListItem>> = _searchResults

    init {
        observeQueryAndSearch()
    }

    private fun observeQueryAndSearch() {
        viewModelScope.launch {
            selectedQuery.collectLatest { query ->
                if (query.isBlank()) {
                    _searchResults.value = emptyList()
                } else {
                    val results = globalSearchCoordinator.performGlobalSearch(query, this)
                    _searchResults.value = results
                }
            }
        }
    }
}