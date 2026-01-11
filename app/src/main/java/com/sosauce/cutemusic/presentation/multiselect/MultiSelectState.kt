package com.sosauce.cutemusic.presentation.multiselect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember


/**
 * A [MultiSelectState] remembered across recompositions. Used to keep track of selected items and perform actions with them. It requires to be given a type parameter for [T]
 */
@Composable
fun <T> rememberMultiSelectState(): MultiSelectState<T> {
    return remember { MultiSelectState() }
}

/**
 * The state that contains selected items and actions related to said items.
 */
class MultiSelectState<T> {

    /**
     * Mutable list of all selected items.
     */
    private val _selectedItems = mutableStateListOf<T>()

    /**
     * Immutable list of all selected items.
     */
    val selectedItems: List<T> = _selectedItems

    /**
     * Whether the state is in selection mode or not. Can be useful, for example, to display radio buttons on items
     */
    val isInSelectionMode: Boolean
        get() = selectedItems.isNotEmpty()

    /**
     * Selects or deselects an item based on it's state.
     * @param item Item to select or deselect.
     */
    fun toggle(item: T) {
        if (selectedItems.contains(item)) {
            _selectedItems.remove(item)
        } else {
            _selectedItems.add(item)
        }
    }

    /**
     * Selects all items at once.
     * @param items Items to select.
     */
    fun toggleAll(items: List<T>) {
        _selectedItems.clear()
        _selectedItems.addAll(items)
    }

    /**
     * Checks whether a given item is selected or not.
     * @param item Item to check for.
     */
    fun isSelected(item: T): Boolean = selectedItems.contains(item)

    /**
     * Clears all selected items from the list. This action is irreversible.
     */
    fun clearSelected() = _selectedItems.clear()

}