package com.kseniabl.userstasks.utils

sealed class Routes(val route: String) {
    object Tabs : Routes("tabs")
    object Login : Routes("login")
    object Registration : Routes("registration")
}

sealed class TabsRoutes(val route: String) {
    object Tasks : Routes("tasks")
    object Clients : Routes("clients")
    object Report : Routes("report")
}