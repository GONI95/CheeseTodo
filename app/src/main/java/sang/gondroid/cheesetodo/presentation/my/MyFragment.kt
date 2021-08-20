package sang.gondroid.cheesetodo.presentation.my

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.databinding.FragmentMyBinding
import sang.gondroid.cheesetodo.presentation.base.BaseFragment
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.MyState
import java.lang.Exception


@Suppress("UNCHECKED_CAST")
class MyFragment : BaseFragment<MyViewModel, FragmentMyBinding>() {
    private val THIS_NAME = this::class.simpleName

    override val viewModel: MyViewModel by viewModel()

    private val firebaseAuth : FirebaseAuth by inject()

    override fun getDataBinding(): FragmentMyBinding
            = FragmentMyBinding.inflate(layoutInflater)

    /**
     * Google login 기능을 구현하기위한 인스턴스
     * 1. Google 로그인 옵션 구성 : requestIdToken 요청, default_web_clien_id엔 자신의 client id가 있다.
     * 2. Google 로그인 관리 클라이언트
     */
    private val gso : GoogleSignInOptions by lazy {
        Log.d(Constants.TAG, "$THIS_NAME initializing gso : GoogleSignInOptions")

        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    private val gsc by lazy {
        Log.d(Constants.TAG, "$THIS_NAME initializing gsc : GoogleSignInClient")

        GoogleSignIn.getClient(requireActivity(), gso)
    }

    /**
     * Google login 기능을 구현하기위한 인스턴스
     * 1. registerForActivityResult : ActivityResultContract 및 ActivityResultCallback을 가져와 다른 활동을 실행하는데 사용할 ActivityResultLauncher를 반환합니다.
     * 2. ActivityResultContracts : android에서 제공하는 일부 표준 활동 호출 계약의 모음
     * 3. StartActivityForResult() : 활동? 작업?을 실행하고 결과값을 반환받아 옵니다.
     * 4. GoogleSignIn.getSignedInAccountFromIntent() : Task<GoogleSignInAccount> 객체로 변환할 수 있다
     * 5. Task<GoogleSignInAccount>.getResult() : authentication에 요청하고 GoogleSignInAccount 객체를 반환받으며, 사용자의 Google 계정 정보를 얻습니다.
     */
    private val loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                task.getResult(ApiException::class.java)?.let { accont ->
                    Log.d(Constants.TAG, "$THIS_NAME loginLauncher getResult() : ${accont.idToken}, ${accont.displayName}")

                    viewModel.saveToken(accont.idToken ?: throw Exception(), accont.displayName ?: throw Exception())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 1. getSignInIntent() : registerForActivityResult를 호출하여 Google 로그인 흐름을 시작하기 위한 Intent를 가져옵니다.
     * 2. launch() : ActivityResultContract를 실행합니다.
     */
    private fun signInGoogle() {
        val signInIntent = gsc.signInIntent
        loginLauncher.launch(signInIntent)
    }

    override fun initViews() = with(binding) {
        loginButton.setOnClickListener {
            signInGoogle()
        }
    }

    /**
     * Preference 또는 Firebase의 현재 유저 정보를 기초로 MyState를 관리하고 그에 맞는 메서드를 실행
     */
    override fun observeData() {
        viewModel.myStateLiveData.observe(viewLifecycleOwner, Observer {
            Log.d(Constants.TAG, "$THIS_NAME observeData MyState : ${it}")
            when (it) {
                is MyState.Loading -> handleLoadingState()
                is MyState.Success -> handleSuccessState(it)
                is MyState.Login -> handleLoginState(it)
                is MyState.Error -> handleErrorState(it)
                is MyState.Uninitialized -> handleUninitialized()
            }
        })
    }

    private fun handleUninitialized() {
        Log.d(Constants.TAG, "$THIS_NAME handleUninitialized() called")
    }

    private fun handleLoadingState() {
        Log.d(Constants.TAG, "$THIS_NAME handleLoadingState() : Loading...")
        binding.loginRequireGroup.isGone = true
        binding.loginPrograssBar.isVisible = true
    }

    private fun handleSuccessState(state: MyState.Success) = with(binding) {
        Log.d(Constants.TAG, "$THIS_NAME handleSuccessState() called")

        loginPrograssBar.isGone = true

        when(state) {
            is MyState.Success.Registered<*, *> -> {
                Log.d(Constants.TAG, "$THIS_NAME handleSuccessState() : Registered")
                handleRegisteredState(state as MyState.Success.Registered<String, Uri>)
            }

            is MyState.Success.NotRegistered -> {
                Log.d(Constants.TAG, "$THIS_NAME handleSuccessState() : NotRegistered")
                profileGroup.isGone = true
                loginRequireGroup.isVisible = true
            }
        }
    }

    /**
     * 로그인 완료 상태의 UI를 표시
     */
    private fun handleRegisteredState(state: MyState.Success.Registered<String, Uri>) = with(binding) {
        Log.d(Constants.TAG, "$THIS_NAME handleRegisteredState() called")


    }

    /**
     * Firebase Authentication에 google email 등록 후 실질적인 로그인 작업 요청
     * 1. getCredential() : Goodle 로그인 ID 또는 AccessToken을 래핑하는 인스턴스를 반환
     * 2. signInWithCredential() : 지정된 User Token으로 Firebase 인증 시스템에 로그인하며, 성공 시 getCurrentUser로 사용자 정보를 가져올 수 있습니다.
     */
    private fun handleLoginState(state: MyState.Login) {
        Log.d(Constants.TAG, "$THIS_NAME handleLoginState() called")

        binding.loginPrograssBar.isGone = true

        val credential = GoogleAuthProvider.getCredential(state.idData, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                Log.d(Constants.TAG, "$THIS_NAME handleLoginState() isSuccessful")

                val user = firebaseAuth.currentUser
                viewModel.validateCurrentUser(user)
            }else {
                Log.d(Constants.TAG, "$THIS_NAME handleLoginState() !isSuccessful")

                firebaseAuth.signOut()
                viewModel.validateCurrentUser(null)
            }
        }
    }

    private fun handleErrorState(state: MyState.Error) {
        Log.d(Constants.TAG, "$THIS_NAME handleErrorState() called")
    }

    companion object {
        fun newInstance() = MyFragment()

        const val TAG = "MyFragment"
    }
}
