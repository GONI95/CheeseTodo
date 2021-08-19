package sang.gondroid.cheesetodo.presentation.todocategory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.domain.usecase.GetTodoListUseCase
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.TodoCategory
import sang.gondroid.cheesetodo.util.TodoListSortFilter

class TodoCategoryViewModel(
    private val todoCategory: TodoCategory,
    private val getTodoListUseCase : GetTodoListUseCase,
    private var todoListSortFilter : TodoListSortFilter = TodoListSortFilter.DEFAULT
) : BaseViewModel() {
    private val THIS_NAME = this::class.simpleName

    init {
        Log.d(Constants.TAG, "$THIS_NAME todoCategory : $todoCategory, ${hashCode()}")
    }

    private val _todoListLiveData = MutableLiveData<List<TodoModel>>()
    val todoListLiveData : LiveData<List<TodoModel>>
        get() = _todoListLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        Log.d(Constants.TAG, "$THIS_NAME fetchData() called")

        // todoCategory로 부터 받아온 상수 값에 따라 DB에게 요청하는 함수를 선택
        val todoList =
            if (todoCategory.ordinal != 0){
                Log.d(Constants.TAG, "$THIS_NAME invoke(category) called")
                getTodoListUseCase.invoke(todoCategory.name)
            }
            else{
                Log.d(Constants.TAG, "$THIS_NAME invoke() called")
                getTodoListUseCase.invoke()
            }

        /**
         * todoListSortFilter.DEFAULT를 생성자로 받아 정렬없이 List가 출력되지만,
         * HomeFragment로 부터 setTodoListFilter()가 호출되며, todoListSortFilter 값이
         * 동적으로 변함
         */

        val sortedTodoList = when(todoListSortFilter) {
            TodoListSortFilter.DEFAULT-> {
                todoList
            }
            TodoListSortFilter.HIGH_IMPORTANCE -> {
                todoList.sortedBy { it.importanceId }
            }
            TodoListSortFilter.FAST_DATE -> {
                todoList.sortedByDescending { it.date }
            }
        }

        Log.d(Constants.TAG, "$THIS_NAME todoListSortFilter : $todoListSortFilter $sortedTodoList")

        // 정렬된 List를 이용해 TodoMoel() 생성
        _todoListLiveData.value = sortedTodoList
    }

    /**
     * HomFragment에서 Chip을 클릭하여 filter 값을 변경하고 Select 작업을 요청하는 메서드
     */
    fun setTodoListFilter(filter: TodoListSortFilter) {
        Log.d(Constants.TAG, "$THIS_NAME filter : $filter")
        todoListSortFilter = filter
        fetchData()
    }
}