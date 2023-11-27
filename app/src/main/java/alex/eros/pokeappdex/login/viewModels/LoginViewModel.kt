package alex.eros.pokeappdex.login.viewModels

import alex.eros.pokeappdex.utils.Cons
import alex.eros.pokeappdex.utils.SharedPrefs
import android.app.Application
import android.os.CountDownTimer
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val application:Application
) : ViewModel() {

    private val TAG = LoginViewModel::class.simpleName

    private lateinit var timerError: CountDownTimer
    private val errorTimerCountDownValue:Long = 3000

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _buttonState = MutableLiveData<Boolean>()
    val buttonState: LiveData<Boolean> = _buttonState

    private val _showAnimation = MutableLiveData<Boolean>()
    val showAnimation:LiveData<Boolean> = _showAnimation

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog:LiveData<Boolean> = _showDialog

    private val _isErrorMessage = MutableLiveData <Boolean>()
    val isErrorMessage:LiveData<Boolean> = _isErrorMessage

    private val _dialogMessage = MutableLiveData<String>()
    val dialogMessage:LiveData<String> = _dialogMessage

    private val _doLogin = MutableLiveData(false)
    val  doLogin: LiveData<Boolean> = _doLogin

    private val _isInvalidDataEmail = MutableLiveData<Boolean>()
    val isInvalidDataEmail:LiveData<Boolean> = _isInvalidDataEmail

    private val _isInvalidDataPassWord = MutableLiveData<Boolean>()
    val isInvalidDataPassWord:LiveData<Boolean> = _isInvalidDataPassWord

    private val _showErrorInvalidEmail = MutableLiveData<String>()
    val showErrorInvalidEMail:LiveData<String> = _showErrorInvalidEmail

    private val _showErrorInvalidPassWord = MutableLiveData<String>()
    val showErrorInvalidPassWord:LiveData<String> = _showErrorInvalidPassWord

    private val _keepSesionActive = MutableLiveData(false)
    val keepSessionActive:LiveData<Boolean> = _keepSesionActive


    fun onLoginChanged(email: String, password: String) {
        _email.value = email
       if (password.length <= 12) _password.value = password
    }

    fun rememberSession(isActive:Boolean){
        _keepSesionActive.value = isActive
    }


    fun loginTrainer() {
        firebaseAuth.signInWithEmailAndPassword(_email.value!!, _password.value!!)
            .addOnCompleteListener { loginResult ->
                if (loginResult.isSuccessful){
                    SharedPrefs.saveData(application,Cons.REMEMBER_SESSION,_keepSesionActive.value!!)
                    setAnimationState(false)
                    enableButton(true)
                    doLogin()
                }else{
                    //TODO: Agregar mensajes cortos y descriptivos, para mostrar al usuario
                    Log.e(TAG,"[loginTrainer] Ex:${loginResult.exception?.message}")
                    setAnimationState(false)
                    _dialogMessage.postValue(loginResult.exception?.message)
                    _isErrorMessage.value = true
                    _showDialog.value = true
                    startTimerCountDown()
                    enableButton(true)
                }
            }
    }

    fun doLogin(){
        _doLogin.postValue(true)
    }

    private fun setAnimationState(state:Boolean){
        _showAnimation.postValue(state)
    }

    fun startTimerCountDown(){

        viewModelScope.launch(Dispatchers.Main){
            timerError = object : CountDownTimer(errorTimerCountDownValue,1000){
                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
                    _showDialog.postValue(false)
                }
            }
            timerError.start()
        }

    }

    private fun isValidEmailFormat(email: String?): Boolean =
        email?.let { Patterns.EMAIL_ADDRESS.matcher(email).matches() } ?: false



    private fun enableButton(state:Boolean) {
        _buttonState.value = state
    }

    fun verifyInfo(){
        enableButton(false)
        setAnimationState(true)
        cleanValues()
        viewModelScope.launch {
            delay(5000)
            if (_email.value.isNullOrBlank()){
                setErrorEmail("Can't be empty")
            }else if (!isValidEmailFormat(_email.value)){
                setErrorEmail("Invalid format")
            }

            if (_password.value.isNullOrBlank()){
                setErrorPassWord("Can't be empty")
            }else if (_password.value!!.length < 12){
                setErrorPassWord("Short password")
            }

            if (_isInvalidDataEmail.value!! || _isInvalidDataPassWord.value!!) {
                setAnimationState(false)
                enableButton(true)
            }
            else loginTrainer()
        }
    }

    private fun cleanValues() {
        _isInvalidDataEmail.value = false
        _isInvalidDataPassWord.value = false
        _showErrorInvalidEmail.value = ""
        _showErrorInvalidPassWord.value = ""
    }

    private fun setErrorEmail(message:String){
        _isInvalidDataEmail.value = true
        _showErrorInvalidEmail.value = message
    }

    private fun setErrorPassWord(message: String){
        _isInvalidDataPassWord.value = true
        _showErrorInvalidPassWord.value = message
    }

    fun setOriginalValues() {
        if (::timerError.isInitialized) timerError.cancel()
        _email.value = ""
        _password.value = ""
        _showAnimation.value = false
        _isInvalidDataPassWord.value = false
        _isInvalidDataEmail.value = false
        _showErrorInvalidPassWord.value = "Enter your registered password"
        _showErrorInvalidEmail.value = "Enter your regitered email"
        _showDialog.value = false
    }


}