package sang.gondroid.cheesetodo.presentation.my

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.databinding.FragmentMyBinding
import sang.gondroid.cheesetodo.presentation.base.BaseFragment
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.MyState
import sang.gondroid.cheesetodo.widget.custom.CustomDialog
import sang.gondroid.cheesetodo.widget.custom.CustomDialogClickListener
import java.lang.Exception


@Suppress("UNCHECKED_CAST")
class MyFragment : BaseFragment<MyViewModel, FragmentMyBinding>() {
    private val THIS_NAME = this::class.simpleName

    override val viewModel: MyViewModel by viewModel()

    override fun getDataBinding(): FragmentMyBinding
            = FragmentMyBinding.inflate(layoutInflater)

    /**
     * Google login 기능을 구현하기위한 인스턴스
     * - GoogleSignInOptions : 사용자의 아이디, 이메일 주소, 기본 정보를 요청하기 위한 로그인 설정
     *                          (default_web_clien_id엔 자신의 client id 있음 / DEFAULT_SIGN_IN에 기본 프로필이 포함되어 있음)
     */
    private val gso : GoogleSignInOptions by lazy {
        Log.d(Constants.TAG, "$THIS_NAME initializing gso : GoogleSignInOptions")

        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    /**
     * Google login 기능을 구현하기위한 인스턴스
     * - GoogleSignInClient : Google Sign In API와 상호작용하기 위한 클라이언트(Google 로그인 관리 클라이언트)
     */
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
     * - getSignInIntent() : registerForActivityResult를 호출하여 Google 로그인 흐름을 시작하기 위한 Intent를 가져옵니다.
     * - launch() : ActivityResultContract를 실행합니다.
     */
    private fun signInGoogle() {
        val signInIntent = gsc.signInIntent
        loginLauncher.launch(signInIntent)
    }

    /**
     * - FirebaseAuth.getInstance().signOut() : 현재 사용자를 로그아웃하고 디스크 캐시에서 삭제
     * - GoogleSignInClient.signOut() : 앱에 연결된 계정을 지우는 작업(매번 계정 선택가능)
     */
    private fun signOut() {
        viewModel.removeToken()
        gsc.signOut()
        gsc.revokeAccess()
    }

    /**
     * - FirebaseAuth.getInstance().currentUser.delete() : Firebase 프로젝트의 데이터베이스에서 사용자 레코드를 삭제
     * - GoogleSignInClient.revokeAccess() : 현재 애플리케이션에 부여된 액세스 권한을 취소
     */
    private fun disjoin() {
        viewModel.disjoinMembership()
        gsc.signOut()
        gsc.revokeAccess()
    }

    override fun initViews() = with(binding) {
        super.initViews()

        loginButton.setOnClickListener {
            signInGoogle()
        }

        logoutButton.setOnClickListener {
            CustomDialog(requireContext(),
                getString(R.string.logout_dialog_title),
                getString(R.string.logout_dialog_description),
                object : CustomDialogClickListener {
                    override fun onPositiveClick() {
                        Log.d(Constants.TAG, "$THIS_NAME onMenuItemClick CustomDialog Positive")
                        signOut()
                    }
                    override fun onNegativeClick() {
                        Log.d(Constants.TAG, "$THIS_NAME onMenuItemClick CustomDialog Negative")
                    }
                }).show()
        }

        deleteButton.setOnClickListener {
            CustomDialog(requireContext(),
                getString(R.string.delete_user_dialog_title),
                getString(R.string.delete_user_dialog_description),
                object : CustomDialogClickListener {
                    override fun onPositiveClick() {
                        Log.d(Constants.TAG, "$THIS_NAME onMenuItemClick CustomDialog Positive")
                        disjoin()
                    }
                    override fun onNegativeClick() {
                        Log.d(Constants.TAG, "$THIS_NAME onMenuItemClick CustomDialog Negative")
                    }
                }).show()
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
                is MyState.False -> handleFalseState()
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

    /**
     * handleSuccessState() : Registered - "validateCurrentUser()로부터 받은 User 정보를 전달하며 hanlderRegisteredState() 호출"
     */
    private fun handleSuccessState(state: MyState.Success) = with(binding) {
        Log.d(Constants.TAG, "$THIS_NAME handleSuccessState() called")

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

        loginPrograssBar.isGone = true
    }

    /**
     * handleRegisteredState() : validateCurrentUser() -> handleSuccessState() -> 로그인 완료 상태의 UI를 표시
     */
    private fun handleRegisteredState(state: MyState.Success.Registered<String, Uri>) = with(binding) {
        Log.d(Constants.TAG, "$THIS_NAME handleRegisteredState() called")

        state.userImageUri.let {
            Glide.with(requireContext())
                .load(it)
                .circleCrop()
                .into(userImageView)
        }

        userNameTextView.text = state.userName

        profileGroup.isVisible = true
        loginRequireGroup.isGone = true
    }

    private fun handleLoginState(state: MyState.Login) {
        Log.d(Constants.TAG, "$THIS_NAME handleLoginState() called")

        viewModel.validateMembership()
    }

    private fun handleErrorState(state: MyState.Error) {
        Log.e(Constants.TAG, "$THIS_NAME handleErrorState() : ${getString(state.messageId, state.e)}")
        Toast.makeText(requireContext(), R.string.an_error_occurred, Toast.LENGTH_LONG).show()
    }

    private fun handleFalseState() {
        Log.d(Constants.TAG, "$THIS_NAME handleFalseState() called")
        Toast.makeText(requireContext(), R.string.request_false, Toast.LENGTH_LONG).show()
    }

    companion object {
        fun newInstance() = MyFragment()

        const val TAG = "MyFragment"
    }
}
