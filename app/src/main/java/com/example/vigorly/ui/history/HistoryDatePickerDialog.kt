package com.example.vigorly.ui.history

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.vigorly.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDatePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    initialDate: LocalDate = LocalDate.now()
) {
    val zone = ZoneId.systemDefault()
    val initialMillis = initialDate.atStartOfDay(zone).toInstant().toEpochMilli()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis ?: initialMillis
                    val date = Instant.ofEpochMilli(selectedMillis).atZone(zone).toLocalDate()
                    onDateSelected(date)
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.history_date_accept))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.history_date_cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
