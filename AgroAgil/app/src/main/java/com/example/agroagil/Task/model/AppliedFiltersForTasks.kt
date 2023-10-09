package com.example.agroagil.Task.model

data class AppliedFiltersForTasks(
    var filterByOverdue: Boolean,
    var filterByToday: Boolean,
    var filterByNext: Boolean,
    var filterByLow: Boolean,
    var filterByHigh: Boolean,
    var filterByDone: Boolean,
) {

    fun getFilterValue(taskFilter: TaskFilter): Boolean {
        when(taskFilter) {
            is TaskFilter.ByOverdue -> return filterByOverdue
            is TaskFilter.ByToday -> return filterByToday
            is TaskFilter.ByNext -> return filterByNext
            is TaskFilter.ByLow -> return filterByLow
            is TaskFilter.ByHigh -> return filterByHigh
            is TaskFilter.ByDone -> return filterByDone
        }
    }

    fun noFilterApplied(): Boolean {
        return noDateFilterApplied() && noPriorityFilterApplied()
    }

    fun noDateFilterApplied(): Boolean {
        return !(filterByOverdue || filterByToday || filterByNext)
    }

    fun noPriorityFilterApplied(): Boolean {
        return !(filterByLow || filterByHigh || filterByDone)
    }

    fun copyWithUpdatedFilter(taskFilter: TaskFilter): AppliedFiltersForTasks {
        var updatedTaskFilters: AppliedFiltersForTasks = this.copy()
        when(taskFilter) {
            is TaskFilter.ByOverdue -> updatedTaskFilters.filterByOverdue = !this.filterByOverdue
            is TaskFilter.ByToday -> updatedTaskFilters.filterByToday = !this.filterByToday
            is TaskFilter.ByNext -> updatedTaskFilters.filterByNext = !this.filterByNext
            is TaskFilter.ByLow -> updatedTaskFilters.filterByLow = !this.filterByLow
            is TaskFilter.ByHigh -> updatedTaskFilters.filterByHigh = !this.filterByHigh
            is TaskFilter.ByDone -> updatedTaskFilters.filterByDone = !this.filterByDone
        }

        return updatedTaskFilters
    }

}