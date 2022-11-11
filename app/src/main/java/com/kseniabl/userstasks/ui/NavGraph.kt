package com.kseniabl.userstasks.ui

import LoginScreen
import RegistrationScreen
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kseniabl.userstasks.models.ErrorResponse
import com.kseniabl.userstasks.models.TaskModel
import com.kseniabl.userstasks.ui.theme.Grey40
import com.kseniabl.userstasks.utils.Resource
import com.kseniabl.userstasks.utils.Routes
import com.kseniabl.userstasks.utils.TabsRoutes
import com.kseniabl.userstasks.viewmodel.MainViewModel
import org.json.JSONObject

@Composable
fun NavGraph(
    navController: NavHostController,
    finish: () -> Unit,
    viewModel: MainViewModel,
    snackbarHostState: SnackbarHostState,
) {

    NavHost(navController = navController, startDestination = Routes.Tabs.route) {
        composable(Routes.Login.route) {
            BackHandler {
                finish()
            }
            LoginScreen(viewModel, navController, snackbarHostState)
        }
        composable(Routes.Registration.route) {
            RegistrationScreen(navController, viewModel)
        }
        navigation(
            route = Routes.Tabs.route,
            startDestination = TabsRoutes.Tasks.route
        ) {
            composableTabs(
                viewModel = viewModel,
                navController = navController,
                snackbarHostState = snackbarHostState
            )
        }
    }
}


fun NavGraphBuilder.composableTabs(
    viewModel: MainViewModel,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState
) {
    composable(TabsRoutes.Tasks.route) {
        var setTasksScreen by rememberSaveable { mutableStateOf(false) }

        val isUserLogin = viewModel.tokenFlowData.collectAsState().value
        LaunchedEffect(isUserLogin) {
            if (isUserLogin is Resource.Loading<*>) {
                Log.e("qqq", "Loading...")
            }
            if (isUserLogin is Resource.Success<*>) {
                if (isUserLogin.data.isNullOrEmpty())
                    navController.navigate(Routes.Login.route)
                if (isUserLogin.data?.isNotEmpty() == true) {
                    setTasksScreen = true
                }
            }
        }
        
        if (setTasksScreen) {
            TaskScreen(viewModel, snackbarHostState)
        }
    }
    composable(TabsRoutes.Clients.route) {
        ClientsScreen(viewModel)
    }
    composable(TabsRoutes.Report.route) {
        ReportScreen(viewModel)
    }
}


