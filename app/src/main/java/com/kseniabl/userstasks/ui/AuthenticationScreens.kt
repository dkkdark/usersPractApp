import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import com.kseniabl.userstasks.models.AddUserModel
import com.kseniabl.userstasks.models.UserLoginModel
import com.kseniabl.userstasks.utils.Resource
import com.kseniabl.userstasks.utils.Routes
import com.kseniabl.userstasks.viewmodel.MainViewModel
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: MainViewModel, navController: NavHostController,
                snackbarHostState: SnackbarHostState) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var valueName by remember { mutableStateOf(TextFieldValue("")) }
        var valuePass by remember { mutableStateOf(TextFieldValue("")) }
        var passwordVisible by rememberSaveable { mutableStateOf(false) }

        var isLoading by rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            viewModel.loginStatus.collect { state ->
                if (state is Resource.Loading<*>)
                    isLoading = true
                if (state is Resource.Success<*>) {
                    isLoading = false
                    val token = state.data?.body()?.token
                    if (token != null) {
                        viewModel.saveToken(token)
                        snackbarHostState.showSnackbar("Welcome!")
                        navController.navigate(Routes.Tabs.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    }
                    else {
                        snackbarHostState.showSnackbar("Something went wrong :(")
                    }
                }
                if (state is Resource.Error<*>) {
                    isLoading = false
                    snackbarHostState.showSnackbar("Error: ${state.message}")
                }
            }
        }

        Text(
            text = "Sign in",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = valueName,
            onValueChange = { valueName = it },
            label = { Text("Username") },
            singleLine = true,
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = valuePass,
            onValueChange = { valuePass = it },
            label = { Text("Password") },
            singleLine = true,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector  = image, contentDescription = null)
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            enabled = !isLoading,
            onClick = {
                viewModel.loginUser(UserLoginModel(valueName.text, valuePass.text))
            }
        ) {
            Text(text = "Sign in".uppercase(Locale.getDefault()))
        }

        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(modifier = Modifier
                .alpha(0.6F)
                .alignByBaseline(),
                style = MaterialTheme.typography.bodySmall,
                text = "Haven't an account yet?")
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(
                modifier = Modifier.alignByBaseline(),
                enabled = !isLoading,
                onClick = {
                    navController.navigate(Routes.Registration.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            ) {
                Text(text = "Sign up",
                    color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(navController: NavHostController, viewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var valueName by remember { mutableStateOf(TextFieldValue("")) }
        var valuePass by remember { mutableStateOf(TextFieldValue("")) }
        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        var valuePassRep by remember { mutableStateOf(TextFieldValue("")) }
        var passwordRepVisible by rememberSaveable { mutableStateOf(false) }
        var valueKeyWord by remember { mutableStateOf(TextFieldValue("")) }

        val radioOptions = listOf("Worker", "Manager")
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

        LaunchedEffect(Unit) {
            viewModel.registrationStatus.collect { value ->
                if (value is Resource.Loading<*>)
                    Log.e("qqq", "Loading")
                if (value is Resource.Success<*>) {
                    if (value.data?.isSuccessful == false) {
                        Log.e("qqq", "Not success ${value.data}")
                        //snackbarHostState.showSnackbar("Error: ${value.data.message()}")
                    }
                    else {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Registration.route) { inclusive = true }
                        }
                        Log.e("qqq", "${value.data}")
                        //snackbarHostState.showSnackbar("Task was finished")
                    }
                }
                if (value is Resource.Error<*>) {
                    Log.e("qqq", "Error ${value.data}")
                    //snackbarHostState.showSnackbar("Error: ${value.data?.message()}")
                }
            }
        }

        Text(
            text = "Sign up",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = valueName,
            onValueChange = { valueName = it },
            label = { Text("Username") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = valuePass,
            onValueChange = { valuePass = it },
            label = { Text("Password") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                IconButton(onClick = { passwordVisible = !passwordVisible }){
                    Icon(imageVector  = image, contentDescription = null)
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = valuePassRep,
            onValueChange = { valuePassRep = it },
            label = { Text("Repeat password") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordRepVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordRepVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                IconButton(onClick = { passwordRepVisible = !passwordRepVisible }){
                    Icon(imageVector  = image, contentDescription = null)
                }
            }
        )
        Spacer(modifier = Modifier.height(6.dp))

        Column(Modifier.selectableGroup()) {
            radioOptions.forEach { text ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = { onOptionSelected(text) },
                            role = Role.RadioButton
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = valueKeyWord,
            onValueChange = { valueKeyWord = it },
            label = { Text("Secret admin key") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                if (valuePass == valuePassRep) {
                    viewModel.addUser(AddUserModel(valueKeyWord.text, valuePass.text,
                        selectedOption.lowercase(Locale.getDefault()), valueName.text))
                }
                else {
                    //TODO
                }
            }
        ) {
            Text(text = "Sign up".uppercase(Locale.getDefault()))
        }

        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(modifier = Modifier
                .alpha(0.6F)
                .alignByBaseline(),
                style = MaterialTheme.typography.bodySmall,
                text = "Have an account?")
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(
                modifier = Modifier.alignByBaseline(),
                onClick = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Registration.route) { inclusive = true }
                    }
                }
            ) {
                Text(text = "Sign in",
                    color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
