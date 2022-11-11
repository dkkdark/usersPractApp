package com.kseniabl.userstasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kseniabl.userstasks.models.*
import com.kseniabl.userstasks.network.Repository
import com.kseniabl.userstasks.utils.Resource
import com.kseniabl.userstasks.utils.UserTokenDataStoreInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userTokenDataStore: UserTokenDataStoreInterface,
    val repository: Repository
): ViewModel() {

    private val _tokenFlowData = MutableStateFlow<Resource<String>>(Resource.Loading())
    val tokenFlowData = _tokenFlowData.asStateFlow()

    private val _loginStatus = MutableSharedFlow<Resource<Response<TokenModel>>>()
    val loginStatus = _loginStatus.asSharedFlow()

    private val _registrationStatus = MutableSharedFlow<Resource<Response<Void>>>()
    val registrationStatus = _registrationStatus.asSharedFlow()

    private val _tasksList = MutableSharedFlow<Resource<Response<ArrayList<TaskModel>>>>()
    val tasksList = _tasksList.asSharedFlow()

    private val _insert = MutableSharedFlow<Resource<Response<Void>>>()
    val insert = _insert.asSharedFlow()

    private val _updated = MutableSharedFlow<Resource<Response<Void>>>()
    val updated = _updated.asSharedFlow()

    private val _isSuccessUpdated = MutableSharedFlow<Boolean>()
    val isSuccessUpdated = _isSuccessUpdated.asSharedFlow()

    private val _organizations = MutableSharedFlow<Resource<Response<ArrayList<OrganizationResponse>>>>()
    val organizations = _organizations.asSharedFlow()

    private val _matchedOrgs = MutableStateFlow<ArrayList<OrganizationResponse>?>(null)
    var matchedOrgs = _matchedOrgs.asStateFlow()

    private var _allOrgs: ArrayList<OrganizationResponse>? = arrayListOf()

    private val _reportArr = MutableSharedFlow<Resource<Response<ArrayList<ReportResponse>>>>()
    val reportArr = _reportArr.asSharedFlow()

    private val _reportRes = MutableStateFlow(ReportResponse(0, 0, 0, 0, 0))
    val reportRes = _reportRes.asStateFlow()

    init {
        viewModelScope.launch {
            _tokenFlowData.emit(Resource.Loading())
            userTokenDataStore.readToken.collect { token ->
                _tokenFlowData.emit(Resource.Success(token))
            }
        }
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            userTokenDataStore.saveToken(token)
        }
    }

    fun loginUser(userLoginModel: UserLoginModel) {
        viewModelScope.launch {
            _loginStatus.emit(Resource.Loading())
            try {
                _loginStatus.emit(Resource.Success(data = repository.loginUser(userLoginModel)))
            } catch (exception: Exception) {
                _loginStatus.emit(Resource.Error(errorMessage = exception.message ?: "Some error occurred"))
            }
        }
    }

    fun addUser(addUserModel: AddUserModel) {
        viewModelScope.launch {
            _registrationStatus.emit(Resource.Loading())
            try {
                _registrationStatus.emit(Resource.Success(data = repository.addUser(addUserModel)))
            } catch (exception: Exception) {
                _registrationStatus.emit(Resource.Error(errorMessage = exception.message ?: "Some error occurred"))
            }
        }
    }

    fun getTasks() {
        viewModelScope.launch {
            userTokenDataStore.readToken.collect { token ->
                _tasksList.emit(Resource.Loading())
                try {
                    _tasksList.emit(Resource.Success(data = repository.getTasks(token)))
                } catch (exception: Exception) {
                    _tasksList.emit(Resource.Error(errorMessage = exception.message ?: "Some error occurred"))
                }
            }
        }
    }

    fun insertTask(insertTaskModel: InsertTaskModel) {
        viewModelScope.launch {
            userTokenDataStore.readToken.collect { token ->
                _insert.emit(Resource.Loading())
                try {
                    _insert.emit(Resource.Success(data = repository.insertTask(token, insertTaskModel)))
                } catch (exception: Exception) {
                    _insert.emit(Resource.Error(errorMessage = exception.message ?: "Some error occurred"))
                }
            }
        }
    }

    fun updateTask(id: String) {
        viewModelScope.launch {
            userTokenDataStore.readToken.collect { token ->
                _updated.emit(Resource.Loading())
                try {
                    _updated.emit(Resource.Success(data = repository.updateTask(token, IdContainBody(id))))
                } catch (exception: Exception) {
                    _updated.emit(Resource.Error(errorMessage = exception.message ?: "Some error occurred"))
                }
            }
        }
    }

    fun taskUpdatedSuccessful() {
        viewModelScope.launch {
            _isSuccessUpdated.emit(true)
        }
    }

    fun getOrganizations() {
        viewModelScope.launch {
            userTokenDataStore.readToken.collect { token ->
                _organizations.emit(Resource.Loading())
                try {
                    _organizations.emit(Resource.Success(data = repository.getOrganizations(token)))
                } catch (exception: Exception) {
                    _organizations.emit(Resource.Error(errorMessage = exception.message ?: "Some error occurred"))
                }
            }
        }
    }

    fun setOrgsList(list: ArrayList<OrganizationResponse>?) {
        _allOrgs = list
        viewModelScope.launch {
            _matchedOrgs.emit(_allOrgs)
        }
    }

    fun onSearchQueryChanged(search: String) {
        viewModelScope.launch {
            if (search.isEmpty()) {
                _matchedOrgs.emit(_allOrgs)
                return@launch
            }
           val filterList = _allOrgs?.filter { el ->
               el.name.contains(search) || el.phoneNum.contains(search)
           } as ArrayList<OrganizationResponse>
           _matchedOrgs.emit(filterList)
       }
    }

    fun getReport(params: ReportBody) {
        viewModelScope.launch {
            userTokenDataStore.readToken.collect { token ->
                _reportArr.emit(Resource.Loading())
                _reportArr.emit(Resource.Success(data = repository.getReport(token, params)))

            }
        }
    }

    fun setReportRes(list: ArrayList<ReportResponse>?) {
        viewModelScope.launch {
            _reportRes.emit(list?.get(0) ?: ReportResponse(0, 0, 0, 0, 0))
        }
    }
}