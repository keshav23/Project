package com.example.myapplication.screen1

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {

                val state = viewModel.container.stateFlow.collectAsState().value
                val sideFlow = viewModel.container.sideEffectFlow
                LaunchedEffect(sideFlow) {
                    handleSideEffects(sideFlow)
                }
                Column {
                    TopBar()
                    ActionRow(
                        onAddClicked = viewModel::onAddButtonClicked,
                        onViewBalanceClicked = viewModel::onViewBalancesClicked
                    )
                    if (state.screenState == 1)
                        Screen1(
                            viewModel::onExpenseAdded,
                            viewModel::onTotalAdded,
                            viewModel::onPaidByAdded,
                            viewModel::onFirstParticipantAdded,
                            viewModel::onSecondParticipantAdded,
                            viewModel::onFinalAddClicked,
                            state.screenData
                        )
                    else
                        Screen2(state.screenData)
                }
            }
        }

    }

    private suspend fun handleSideEffects(sideEffect: Flow<SideEffect>) {
        sideEffect.collect {
            when (it) {
                is SideEffect.ShowToast -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }

                else -> {}
            }
        }
    }
}

@Composable
fun Screen2(
    screenData: ScreenData
) {
    val data = screenData as? Screen2Data
    data?.let {
        ShowOutput(it.user1, it.user1owing)
        Spacer(modifier = Modifier.height(2.dp))
        ShowOutput(it.user2, it.user2owing)
        Spacer(modifier = Modifier.height(2.dp))
        ShowOutput(it.user3, it.user3owing)
    }

}

@Composable
fun Screen1(
    onExpenseAdded: (String) -> Unit,
    onTotalAdded: (String) -> Unit,
    onPaidByAdded: (String) -> Unit,
    onFirstParticipantAdded: (String) -> Unit,
    onSecondParticipantAdded: (String) -> Unit,
    onFinalAddClicked: () -> Unit,
    screenData: ScreenData
) {
    (screenData as? Scree1Data)?.let {
        Spacer(modifier = Modifier.height(2.dp))
        TakeInput("Expense", onExpenseAdded, it.expenseDefault)
        Spacer(modifier = Modifier.height(2.dp))
        TakeInput("Total", onTotalAdded, it.total)
        Spacer(modifier = Modifier.height(2.dp))
        TakeInput("Paid By", onPaidByAdded, it.paidBy)
        TakeInput("Participates", onFirstParticipantAdded, it.user1)
        TakeInput("Participates", onSecondParticipantAdded, it.user2)

        Spacer(modifier = Modifier.height(8.dp))

        Button(modifier = Modifier.padding(horizontal = 128.dp),
            onClick = { onFinalAddClicked() }) {
            Text(text = "Add")
        }
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()
            .background(Color.LightGray),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "SPLITWISE",
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
    )
    Divider(thickness = 2.dp, color = Color.Black)
}

@Composable
fun ActionRow(
    onAddClicked: () -> Unit,
    onViewBalanceClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = { onAddClicked() }) {
            Text(text = "Add")
        }
        Divider(
            thickness = 2.dp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
        )
        Button(onClick = { onViewBalanceClicked() }) {
            Text(text = "View Balances")
        }
    }
    Divider(thickness = 2.dp, color = Color.Black)
}

@Composable
fun ShowOutput(
    person: String,
    owings: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            textAlign = TextAlign.Justify,
            text = person,
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 8.dp, top = 16.dp)
        )
        Text(
            textAlign = TextAlign.Justify,
            text = owings,
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 8.dp, top = 16.dp)
        )
    }
}

@Composable
fun TakeInput(
    inputField: String,
    onValueChange: (String) -> Unit,
    defaultValue: String
) {
    var text by rememberSaveable { mutableStateOf(defaultValue) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            textAlign = TextAlign.Justify,
            text = inputField,
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 8.dp, top = 16.dp)
        )
        TextField(
            modifier = Modifier.padding(horizontal = 8.dp),
            value = text, onValueChange = {
                text = it
                onValueChange(it)
            })

    }
}