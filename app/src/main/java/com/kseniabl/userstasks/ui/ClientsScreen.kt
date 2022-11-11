package com.kseniabl.userstasks.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.kseniabl.userstasks.models.OrganizationResponse
import com.kseniabl.userstasks.utils.Resource
import com.kseniabl.userstasks.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen(
    viewModel: MainViewModel
) {
    var showClearButton by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    var valueSearch by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        viewModel.getOrganizations()
        viewModel.organizations.collect { value ->
            if (value is Resource.Loading<*>)
                Log.e("qqq", "Loading organizations")
            if (value is Resource.Success<*>) {
                if (value.data?.isSuccessful == false) {
                    //snackbarHostState.showSnackbar("Error: ${value.data?.message()}")
                    Log.e("qqq", "${value.data}")
                }
                else {
                    viewModel.setOrgsList(value.data?.body())
                    Log.e("qqq", "orgs ${value.data?.body()}")

                }
            }
            if (value is Resource.Error<*>) {
                //snackbarHostState.showSnackbar("Error: ${value.data?.message()}")
            }
        }
    }

    Column(Modifier.padding(8.dp).fillMaxSize()) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .onFocusChanged { focusState ->
                    showClearButton = (focusState.isFocused)
                }
                .focusRequester(focusRequester),
            value = valueSearch,
            onValueChange = {
                valueSearch = it
                viewModel.onSearchQueryChanged(it.text)
            },
            placeholder = {
                Text(text = "Search")
            },
            trailingIcon = {
                IconButton(onClick = {
                    valueSearch = TextFieldValue("")
                    viewModel.onSearchQueryChanged("") }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null
                    )
                }
            },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            }),
        )

        val orgs = viewModel.matchedOrgs.collectAsState().value ?: arrayListOf()
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(orgs) { item ->
                ClientItem(item)
            }
        }
    }
}

@Composable
fun ClientItem(item: OrganizationResponse) {
    val cardPadding = Modifier.padding(start = 16.dp, end = 8.dp, top = 2.dp)

    Card(
        Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(
            modifier = cardPadding.padding(top = 8.dp),
            text = item.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(modifier = cardPadding, text = item.email)
        Spacer(modifier = Modifier.height(8.dp))
        Text(modifier = cardPadding.padding(bottom = 8.dp), text = item.phoneNum)
    }
}