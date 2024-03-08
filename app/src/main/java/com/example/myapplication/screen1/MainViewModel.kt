package com.example.myapplication.screen1

import android.widget.Toast
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.lang.Exception
import javax.inject.Inject
import kotlin.math.exp

@HiltViewModel
class MainViewModel
@Inject
constructor() : ContainerHost<State, SideEffect>, ViewModel() {
    override val container =
        container<State, SideEffect>(initialState = State(1, Scree1Data("", "", "", "", "")))

    private var expenseObject: ExpenseObject = ExpenseObject.default()
    private val listOfUsers: List<String> = listOf("Raj", "Varun", "Keshav")
    private val owings: MutableList<Double> = mutableListOf(0.0, 0.0, 0.0)

    fun onAddButtonClicked() = intent {
        reduce {
            this.state.copy(screenState = 1)
        }
    }

    fun onViewBalancesClicked() = intent {
//        reduce {
////            expenseObject.clear()
////            val scree2Data: Screen2Data()
////            for(x in 0..listOfUsers.size-1){
////                if(x==0)
////                    scree2Data.user1 =
////                else if
////            }
////            this.state.copy(screenState = 2, screenData =
////            Screen2Data(
////
////            ))
//        }
    }

    fun onExpenseAdded(it: String) {
        expenseObject.expense = it
    }

    fun onTotalAdded(it: String) {
        try {
            val value = it.toInt()
            if (value >= 0)
                expenseObject.total = value
            else
                intent {
                    postSideEffect(SideEffect.ShowToast("Put total > 0"))
                }

        } catch (e: Exception) {
            expenseObject.total = -1
        }
    }

    fun onPaidByAdded(it: String) {
        expenseObject.paidBy = it
    }

    fun onFirstParticipantAdded(it: String) {
        expenseObject.user1 = it
    }

    fun onSecondParticipantAdded(it: String) {
        expenseObject.user2 = it
    }

    private fun addRecord() {
        val findIndex = listOfUsers.indexOf(expenseObject.paidBy)
        owings[findIndex] = owings[findIndex] + expenseObject.total.toInt()
        val divideExpense: Double = expenseObject.total.toDouble() / 3;
        owings.forEachIndexed { index, d ->
            owings[index] = d - divideExpense
        }
    }

    fun onFinalAddClicked() {
        if (validateData()) {
            addRecord()
            onViewBalancesClicked()
            intent {
                postSideEffect(SideEffect.ShowToast("Added successfully"))
            }
            expenseObject.clear()
        }
    }

    private fun validateData(): Boolean {
        if (listOfUsers.contains(expenseObject.paidBy)
            && listOfUsers.contains(expenseObject.user1)
            && listOfUsers.contains(expenseObject.user2)
            && expenseObject.expense.length > 0
            && expenseObject.total >= 0
        ) {
            return true
        } else {
            intent {
                postSideEffect(SideEffect.ShowToast("Please check inputs"))
            }
            return false
        }
    }
}

data class ExpenseObject(
    var expense: String,
    var total: Int,
    var paidBy: String,
    var user1: String,
    var user2: String
) {
    companion object {
        fun default(): ExpenseObject {
            return ExpenseObject("", -1, "", "", "")
        }
    }

    fun clear() {
        this.expense = ""
        this.total = 0
        this.paidBy = ""
        this.user1 = ""
        this.user2 = ""
    }


}

data class State(
    val screenState: Int,
    val screenData: ScreenData
)

interface ScreenData

data class Scree1Data(
    val expenseDefault: String,
    val paidBy: String,
    val user1: String,
    val user2: String,
    val total: String
) : ScreenData

data class Screen2Data(
    var user1: String,
    var user2: String,
    var user3: String,
    var user1owing: String,
    var user2owing: String,
    var user3owing: String
) : ScreenData

sealed class SideEffect {
    data class ShowToast(val message: String) : SideEffect()

}