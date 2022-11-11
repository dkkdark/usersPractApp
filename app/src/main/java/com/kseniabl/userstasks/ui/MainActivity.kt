package com.kseniabl.userstasks.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kseniabl.userstasks.utils.ButtonsTitles
import com.kseniabl.userstasks.utils.Routes
import com.kseniabl.userstasks.ui.theme.UsersTasksTheme
import com.kseniabl.userstasks.utils.TabsRoutes
import com.kseniabl.userstasks.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UsersTasksTheme {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        navController = navController,
                        viewModel = viewModel,
                        finish = { finish() }
                    )
                }
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    finish: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val topBarState = rememberSaveable { (mutableStateOf(false)) }
    val snackbarHostState = remember { SnackbarHostState() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    when (navBackStackEntry?.destination?.route) {
        Routes.Login.route -> topBarState.value = false
        Routes.Registration.route -> topBarState.value = false
        TabsRoutes.Tasks.route -> topBarState.value = true
        TabsRoutes.Clients.route -> topBarState.value = true
        TabsRoutes.Report.route -> topBarState.value = true
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { TopBar(topBarState, scrollBehavior, navController, navBackStackEntry, viewModel) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavGraph(
                navController = navController,
                finish = { finish() },
                viewModel = viewModel,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    topBarState: MutableState<Boolean>,
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavHostController,
    navBackStackEntry: NavBackStackEntry?,
    viewModel: MainViewModel
) {
    AnimatedVisibility(
        visible = topBarState.value,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
    ) {
        TopBarContent(scrollBehavior = scrollBehavior, navController, navBackStackEntry, viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarContent(scrollBehavior: TopAppBarScrollBehavior, navController: NavHostController,
                  navBackStackEntry: NavBackStackEntry?, viewModel: MainViewModel) {
    val buttonsName = ButtonsTitles.values().map { it.name }

    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(bottom = 12.dp, start = 8.dp, end = 8.dp)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    "TopAppBar",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            actions = {
                IconButton(onClick = {
                    viewModel.saveToken("")
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Tabs.route) { inclusive = true }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Localized description",
                        tint = Color.White
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            itemsIndexed(buttonsName) { _, item ->
                Button(
                    onClick = {
                          when(item) {
                              "Tasks" -> {
                                  navController.navigate(TabsRoutes.Tasks.route) {
                                      // delete from back stack current destination, so back -> close
                                      navBackStackEntry?.destination?.route?.let {
                                          popUpTo(it) { inclusive = true }
                                      }
                                  }
                              }
                              "Clients" -> {
                                  navController.navigate(TabsRoutes.Clients.route) {
                                      navBackStackEntry?.destination?.route?.let {
                                          popUpTo(it) { inclusive = true }
                                      }
                                  }
                              }
                              "Report" -> {
                                  navController.navigate(TabsRoutes.Report.route) {
                                      navBackStackEntry?.destination?.route?.let {
                                          popUpTo(it) { inclusive = true }
                                      }
                                  }
                              }
                          }
                    },
                    border = BorderStroke(2.dp, Color.White)
                ) {
                    Text(
                        text = item, fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
    
}


