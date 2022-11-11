package com.kseniabl.userstasks.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kseniabl.userstasks.models.InsertTaskModel
import com.kseniabl.userstasks.models.TaskModel
import com.kseniabl.userstasks.utils.Resource
import com.kseniabl.userstasks.viewmodel.MainViewModel

@Composable
fun TaskScreen(viewModel: MainViewModel, snackbarHostState: SnackbarHostState) {

    var tasks by rememberSaveable { mutableStateOf(arrayListOf<TaskModel>()) }

    LaunchedEffect(Unit) {
        viewModel.getTasks()
        viewModel.tasksList.collect { value ->
            if (value is Resource.Loading<*>)
                Log.e("qqq", "Loading tasks")
            if (value is Resource.Success<*>) {
                tasks = value.data?.body() ?: arrayListOf()
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.updated.collect { value ->
            if (value is Resource.Loading<*>)
                Log.e("qqq", "Loading")
            if (value is Resource.Success<*>) {
                if (value.data?.isSuccessful == false) {
                    Log.e("qqq", "${value.data}")
                    //snackbarHostState.showSnackbar("Error: ${value.data.message()}")
                }
                else {
                    viewModel.taskUpdatedSuccessful()
                    //snackbarHostState.showSnackbar("Task was finished")
                }
            }
            if (value is Resource.Error<*>) {
                snackbarHostState.showSnackbar("Error: ${value.data?.message()}")
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.insert.collect { value ->
            if (value is Resource.Loading<*>)
                Log.e("qqq", "Loading insert")
            if (value is Resource.Success<*>) {
                if (value.data?.isSuccessful == false) {
                    Log.e("qqq", "${value.data}")
                    //snackbarHostState.showSnackbar("Error: ${value.data.message()}")
                }
                else {
                    Log.e("qqq", "Success ${value.data}")
                    viewModel.getTasks()
                    //snackbarHostState.showSnackbar("Task was finished")
                }
            }
            if (value is Resource.Error<*>) {
                Log.e("qqq", "${value.data}")
                snackbarHostState.showSnackbar("Error: ${value.data?.message()}")
            }
        }
    }

    var id by remember { mutableStateOf("") }

    var showTaskCreateDialog by remember { mutableStateOf(false) }

    if (showTaskCreateDialog) {
        TaskCreateDialog(
            onDismiss = { showTaskCreateDialog = false },
            onTaskCreated = { viewModel.insertTask(it) }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn() {
            items(tasks) { item ->
                val cardPadding = Modifier.padding(start = 16.dp, end = 8.dp, top = 2.dp)
                val (checkedState, onStateChange) = remember { mutableStateOf(item.isTaskFinish) }

                var isSuccess = viewModel.isSuccessUpdated.collectAsState(initial = false).value

                LaunchedEffect(isSuccess) {
                    if (isSuccess && id == item.taskId) {
                        onStateChange(!checkedState)
                        tasks.find { it.taskId == item.taskId }?.isTaskFinish = !checkedState
                    }
                    isSuccess = false
                }

                Card(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        modifier = cardPadding.padding(top = 8.dp),
                        text = item.taskName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(modifier = cardPadding, text = item.taskDescription)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(cardPadding.fillMaxWidth()) {
                        Text(text = "Author name: ",
                            fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = item.authorName)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        cardPadding
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)) {
                        Text(text = "Executor name: ",
                            fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = item.executorName)
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .toggleable(
                                value = checkedState,
                                onValueChange = {
                                    viewModel.updateTask(item.taskId)
                                    id = item.taskId
                                },
                                role = Role.Checkbox,
                                enabled = !checkedState
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Finish",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Checkbox(
                            modifier = Modifier.padding(start = 8.dp),
                            checked =  checkedState,
                            onCheckedChange = null
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showTaskCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreateDialog(onDismiss: () -> Unit, onTaskCreated: (InsertTaskModel) -> Unit) {
    var taskNameValue by remember { mutableStateOf(TextFieldValue("")) }
    var taskDescrValue by remember { mutableStateOf(TextFieldValue("")) }
    var taskPriority by remember { mutableStateOf(TextFieldValue("")) }
    var executorName by remember { mutableStateOf(TextFieldValue("")) }

    val (checkedState, onStateChange) = remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Box(modifier = Modifier.wrapContentSize()) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(size = 16.dp)
                    )
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp),
                    text = "Create Task".uppercase(),
                    style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    value = taskNameValue,
                    onValueChange = { taskNameValue = it },
                    label = { Text("Task name") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    value = taskDescrValue,
                    onValueChange = { taskDescrValue = it },
                    label = { Text("Task description") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    value = taskPriority,
                    onValueChange = { taskPriority = it },
                    label = { Text("Priority") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    value = executorName,
                    onValueChange = { executorName = it },
                    label = { Text("Executor name") },
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .toggleable(
                            value = checkedState,
                            onValueChange = {
                                onStateChange(it)
                            },
                            role = Role.Checkbox
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Is Termless",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Checkbox(
                        modifier = Modifier.padding(start = 8.dp),
                        checked =  checkedState,
                        onCheckedChange = null
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 16.dp, end = 16.dp)
                ) {
                    TextButton(
                        onClick = { onDismiss() }
                    ) {
                        Text(text = "Cancel")
                    }
                    TextButton(
                        onClick = {
                            onTaskCreated(InsertTaskModel(executorName.text, checkedState,
                                taskPriority.text.toInt(), taskDescrValue.text, taskNameValue.text))
                            onDismiss()
                        }
                    ) {
                        Text(text = "OK")
                    }

                }

            }
        }

    }
}