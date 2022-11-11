package com.kseniabl.userstasks.ui

import android.util.Log
import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.kseniabl.userstasks.models.ReportBody
import com.kseniabl.userstasks.utils.Resource
import com.kseniabl.userstasks.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(viewModel: MainViewModel) {
    var dateStart by remember { mutableStateOf("") }
    var dateEnd by remember { mutableStateOf("") }
    var clickedStart by remember { mutableStateOf(false) }
    var clickedEnd by remember { mutableStateOf(false) }

    var outlineTextVal by remember { mutableStateOf(TextFieldValue("")) }

    val startDateText = dateStart.ifEmpty { "Start Date" }
    val endDateText = dateEnd.ifEmpty { "End Date" }

    if (clickedStart) {
        CustomCalendarView(
            onDateSelected = { dateStart = formatDate(it) },
            onDismiss = { clickedStart = false })
    }
    if (clickedEnd) {
        CustomCalendarView(
            onDateSelected = { dateEnd = formatDate(it) },
            onDismiss = { clickedEnd = false })
    }

    LaunchedEffect(Unit) {
        viewModel.reportArr.collect { value ->
            if (value is Resource.Loading<*>)
                Log.e("qqq", "Loading report")
            if (value is Resource.Success<*>) {
                if (value.data?.isSuccessful == false) {
                    //snackbarHostState.showSnackbar("Error: ${value.data?.message()}")
                    Log.e("qqq", "${value.data}")
                }
                else {
                    Log.e("qqq", "report ${value.data?.body()}")
                    viewModel.setReportRes(value.data?.body())
                }
            }
            if (value is Resource.Error<*>) {
                Log.e("qqq", "error ${value.data?.code()}")
                //snackbarHostState.showSnackbar("Error: ${value.data?.message()}")
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(8.dp)) {
        OutlinedTextField(
            value = outlineTextVal,
            onValueChange = { outlineTextVal = it },
            label = { Text("Worker name") },
            singleLine = true
        )
        Box(modifier = Modifier
            .padding(4.dp)
            .clickable { clickedStart = true }) {
            Row(Modifier.padding(12.dp)) {
                Text(text = startDateText)
                Spacer(modifier = Modifier.width(32.dp))
                Icon(imageVector = Icons.Filled.CalendarMonth, contentDescription = null)
            }
        }
        Box(modifier = Modifier
            .padding(4.dp)
            .clickable { clickedEnd = true }) {
            Row(Modifier.padding(12.dp)) {
                Text(text = endDateText)
                Spacer(modifier = Modifier.width(32.dp))
                Icon(imageVector = Icons.Filled.CalendarMonth, contentDescription = null)
            }
        }
        Button(modifier = Modifier.padding(bottom = 4.dp),
            onClick = { viewModel.getReport(ReportBody(endDateText, startDateText, outlineTextVal.text)) }) {
            Text(text = "Form report")
        }
        val report = viewModel.reportRes.collectAsState().value
        LazyColumn() {
            items(1) {
                ReportView(report.firstSelect, "Tasks not finished with time")
                ReportView(report.secondSelect, "Tasks not finished and not in time")
                ReportView(report.thirdSelect, "Tasks finished not in time")
                ReportView(report.fourthSelect, "Tasks finished in time")
                ReportView(report.mainSelect, "Total Tasks")
            }
        }
    }

}

@Composable
fun ReportView(report: Int, name: String) {
    Spacer(modifier = Modifier.height(6.dp))
    Row(Modifier.fillMaxWidth()) {
        Text(text = "$name:")
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = report.toString())
    }
}

@Composable
fun CustomCalendarView(onDateSelected: (LocalDate) -> Unit, onDismiss: () -> Unit) {
    var localDate by remember { mutableStateOf(LocalDate.now()) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(size = 16.dp)
                )
        ) {
            Column(
                Modifier
                    .defaultMinSize(minHeight = 72.dp)
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select date".uppercase(Locale.ENGLISH),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.size(24.dp))

                Text(
                    text = formatDate(localDate),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            AndroidView(
                modifier = Modifier.wrapContentSize(),
                factory = { context ->
                    CalendarView(context)
                },
                update = { view ->
                    view.setOnDateChangeListener { _, year, month, dayOfMonth ->
                        localDate = LocalDate
                                .now()
                                .withMonth(month + 1)
                                .withYear(year)
                                .withDayOfMonth(dayOfMonth)
                    }
                }
            )
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
                        onDateSelected(localDate)
                        onDismiss()
                    }
                ) {
                    Text(text = "OK")
                }

            }
        }
    }
}

private fun formatDate(localDate: LocalDate): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY/MM/dd")
    return localDate.format(formatter)
}