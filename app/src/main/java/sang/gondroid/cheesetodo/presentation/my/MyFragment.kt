package sang.gondroid.cheesetodo.presentation.my

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.databinding.FragmentMyBinding
import sang.gondroid.cheesetodo.domain.model.FireStoreMemberModel
import sang.gondroid.cheesetodo.presentation.base.BaseFragment
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.NetworkConnection
import sang.gondroid.cheesetodo.widget.custom.CustomDialog
import sang.gondroid.cheesetodo.widget.custom.CustomDialogClickListener
import java.lang.Exception


class MyFragment : BaseFragment<MyViewModel, FragmentMyBinding>() {
    private val THIS_NAME = this::class.simpleName

    /**
     * Gon : 네트워크 연결 상태를 확인하기 위한 NetworkConnection class
     *       [21.11.28]
     */
    private val connection : NetworkConnection by inject()

    override val viewModel: MyViewModel by viewModel()

    override fun getDataBinding(): FragmentMyBinding
            = FragmentMyBinding.inflate(layoutInflater)

    /**
     * Gon : GoogleSignInOptions : 사용자의 아이디, 이메일 주소, 기본 정보를 요청하기 위한 설정
     *                             default_web_clien_id엔 자신의 client id / DEFAULT_SIGN_IN에 기본 프로필이 포함
     *       [update - 21.12.3]
     */
    private val gso : GoogleSignInOptions by lazy {
        LogUtil.v(Constants.TAG, "$THIS_NAME initializing GoogleSignInOptions")

        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    /**
     * Gon : GoogleSignInClient : Google Sign In API와 상호작용하기 위한 클라이언트(Google 로그인 관리 클라이언트)
     *       [update - 21.12.3]
     */
    private val gsc by lazy {
        LogUtil.v(Constants.TAG, "$THIS_NAME initializing GoogleSignInClient")

        GoogleSignIn.getClient(requireActivity(), gso)
    }

    /**
     * Gon : ActivityResultLauncher<Intent> : ActivityResultContract 실행 프로세스를 시작하기 위해 미리 준비된 호출의 시작점
     *
     *       Google login 기능을 구현하기위한 인스턴스
     *       registerForActivityResult : ActivityResultContract 및 ActivityResultCallback을 가져와 다른 활동을 실행하는데 사용할 ActivityResultLauncher를 반환합니다.
     *       ActivityResultContracts : android에서 제공하는 일부 표준 활동 호출 계약의 모음
     *       StartActivityForResult() : 활동? 작업?을 실행하고 결과값을 반환받아 옵니다.
     *       GoogleSignIn.getSignedInAccountFromIntent() : Task<GoogleSignInAccount> 객체로 변환할 수 있다
     *       Task<GoogleSignInAccount>.getResult() : authentication에 요청하고 GoogleSignInAccount 객체를 반환받으며, 사용자의 Google 계정 정보를 얻습니다.
     *       [update - 21.12.3]
     */
    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                task.getResult(ApiException::class.java)?.let { accont ->
                    Log.i(Constants.TAG, "$THIS_NAME GoogleSignInAccount getResult() : ${accont.idToken}, ${accont.displayName}")

                    viewModel.saveToken(accont.idToken ?: throw Exception(), accont.displayName ?: throw Exception())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Gon : GoogleSignInClient.getSignInIntent() : registerForActivityResult를 호출하여 Google 로그인 흐름을 시작하기 위한 Intent를 가져옵니다.
     *       ActivityResultLauncher.launch() : ActivityResultContract를 실행합니다.
     *       [update - 21.12.3]
     */
    private fun signInGoogle() {
        LogUtil.v(Constants.TAG, "$THIS_NAME signInGoogle() called")
        val signInIntent = gsc.signInIntent
        signInLauncher.launch(signInIntent)
    }

    /**
     * Gon : FirebaseAuth.getInstance().signOut() : 현재 사용자를 로그아웃하고 디스크 캐시에서 삭제
     *       GoogleSignInClient.revokeAccess() : 현재 애플리케이션에 부여된 액세스 권한을 취소
     *       GoogleSignInClient.signOut() : 앱에 연결된 계정을 지우는 작업(매번 계정 선택가능)
     *       [update - 21.11.17]
     */
    private fun signOut() {
        LogUtil.v(Constants.TAG, "$THIS_NAME signOut() called")
        gsc.signOut()
        gsc.revokeAccess()
        viewModel.removeToken()
    }

    override fun initViews() = with(binding) {

        /**
         * Gon : 로그인 버튼을 클릭하고 발생한 SignIn
         *       [update - 21.11.17]
         */
        signInButton.setOnClickListener {
            LogUtil.v(Constants.TAG, "$THIS_NAME signInButton onClick called")
            signInGoogle()
        }

        /**
         * Gon : 로그아웃 버튼을 클릭하고 발생한 Dialog의 선택 값에 따라 작업을 처리합니다.
         *       [update - 21.11.17]
         */
        signOutButton.setOnClickListener {
            CustomDialog(requireContext(),
                getString(R.string.sign_out_dialog_title),
                getString(R.string.sign_out_dialog_description),
                object : CustomDialogClickListener {
                    override fun onPositiveClick() {
                        LogUtil.v(Constants.TAG, "$THIS_NAME 로그아웃 CustomDialog Positive")
                        signOut()
                    }
                    override fun onNegativeClick() {
                        LogUtil.v(Constants.TAG, "$THIS_NAME 로그아웃 CustomDialog Negative")
                    }
                }).show()
        }

        /**
         * Gon : 회원탈퇴 버튼을 클릭하고 발생한 Dialog의 선택 값에 따라 작업을 처리합니다.
         *       [update - 21.11.17]
         */
        deleteAccountButton.setOnClickListener {
            CustomDialog(requireContext(), getString(R.string.delete_account_dialog_title), getString(R.string.delete_account_dialog_description), object : CustomDialogClickListener {
                override fun onPositiveClick() {
                    LogUtil.v(Constants.TAG, "$THIS_NAME 회원탈퇴 CustomDialog Positive")
                    viewModel.deleteAccount()
                }
                override fun onNegativeClick() {
                    LogUtil.v(Constants.TAG, "$THIS_NAME 회원탈퇴 CustomDialog Negative")
                }
            }).show()
        }
    }

    override fun observeData() = with(binding) {

        /**
         * Gon : memberVerification() 또는 signInWithCredential()의 반환값을 토대로 메서드를 실행
         *       [update - 21.12.3]
         */
        viewModel.signInStateLiveData.observe(viewLifecycleOwner, Observer {
            LogUtil.i(Constants.TAG, "$THIS_NAME observeData signInStateLiveData : ${it}")
            when (it) {
                is JobState.Loading -> handleLoadingState()
                is JobState.Signin -> handleSignInState()
                is JobState.Success -> handleSuccessState(it)
                is JobState.False -> handleFalseState()
                is JobState.Error -> handleErrorState(it)
                is JobState.Uninitialized -> handleUninitialized()
            }
        })

        /**
         * Gon : deleteAccount() 반환값을 토대로 메서드를 실행
         *       [update - 21.12.3]
         */
        viewModel.deleteAccountLiveData.observe(viewLifecycleOwner, Observer {
            LogUtil.d(Constants.TAG, "$THIS_NAME observeData deleteAccountLiveData : ${it}")
            when (it) {
                is JobState.Loading -> handleLoadingState()
                is JobState.True -> signOut()
                is JobState.False -> handleFalseState()
                is JobState.Error -> handleErrorState(it)
            }
        })

        /**
         * Gon : connection(네트워크 변경 상태에 따라 true, false) 값에 따라 서비스 이용이 유무에 대응하는 layout을 표시
         *       [update - 21.11.28]
         */
        connection.observe(this@MyFragment, Observer { isConnected ->
            if (isConnected) {
                LogUtil.d(Constants.TAG, "$THIS_NAME, 네트워크 연결 상태 : $isConnected ")

                myLayoutDisconnected.visibility = View.GONE
                myLayoutConnected.visibility = View.VISIBLE
            } else {
                LogUtil.d(Constants.TAG, "$THIS_NAME, 네트워크 연결 상태 : $isConnected ")

                myLayoutDisconnected.visibility = View.VISIBLE
                myLayoutConnected.visibility = View.GONE
            }
        })
    }

    /**
     * Gon : FirebaseAuth CurrentUser 또는 FirebaseAuth CurrentUser 하위 정보가 null인 경우 호출
     *       [update - 21.12.3]
     */
    private fun handleUninitialized() {
        LogUtil.v(Constants.TAG, "$THIS_NAME handleUninitialized() called")
        Toast.makeText(requireContext(), R.string.an_error_occurred, Toast.LENGTH_LONG).show()
    }

    /**
     * Gon : 작업 중 [ProgressBar 표시]
     *       [update - 21.12.3]
     */
    private fun handleLoadingState() = with(binding) {
        LogUtil.v(Constants.TAG, "$THIS_NAME handleLoadingState() called")
        signInRequireGroup.isGone = true
        profileGroup.isGone = true
        signStatePrograssBar.isVisible = true
    }

    /**
     * Gon : signInWithCredential()로부터 JobState.SignIn을 반환받은 경우 Firestore에서 저장된 회원을 검증하는 memberVerification() 호출
     *       [update - 21.12.3]
     */
    private fun handleSignInState() {
        LogUtil.v(Constants.TAG, "$THIS_NAME handleSignInState() called")

        viewModel.memberVerification()
    }

    /**
     * Gon : Registered - getCurrentMember() 반환값을 전달하며 hanlderRegisteredState() 호출
     *       NotRegistered - signIn Require 표시
     *       [update - 21.12.3]
     */
    @Suppress("UNCHECKED_CAST")
    private fun handleSuccessState(state: JobState.Success) = with(binding) {
        LogUtil.v(Constants.TAG, "$THIS_NAME handleSuccessState() called")
        LogUtil.d(Constants.TAG, "$THIS_NAME handleSuccessState() : $state")

        when(state) {
            is JobState.Success.Registered<*> -> {
                handleRegisteredState(state as JobState.Success.Registered<FireStoreMemberModel>)
            }

            is JobState.Success.NotRegistered -> {
                profileGroup.isGone = true
                signInRequireGroup.isVisible = true
            }
        }

        signStatePrograssBar.isGone = true
    }

    /**
     * Gon : SignIn 상태로 profileGroup을 표시
     *       fireStoreMemberModel : userInfoLayout(ViewGroup) 내의 View들이 표시할 데이터
     *       [update - 21.12.3]
     */
    private fun handleRegisteredState(state: JobState.Success.Registered<FireStoreMemberModel>) = with(binding) {
        LogUtil.v(Constants.TAG, "$THIS_NAME handleRegisteredState() called")

        fireStoreMemberModel = state.data

        profileGroup.isVisible = true
        signInRequireGroup.isGone = true
    }

    /**
     * Gon : 공통적으로 JobState.Error 반환되면, Toast Message를 표시하고, SignIn Require 표시
     *       [update - 21.12.3]
     */
    private fun handleErrorState(state: JobState.Error) = with(binding) {
        LogUtil.e(Constants.TAG, "$THIS_NAME handleErrorState() : ${getString(state.messageId, state.e)}")
        Toast.makeText(requireContext(), R.string.an_error_occurred, Toast.LENGTH_LONG).show()

        signStatePrograssBar.isGone = true
        signInRequireGroup.isVisible = true
    }

    /**
     * Gon : 공통적으로 JobState.False 반환되면, Toast Message를 표시
     *       [update - 21.12.3]
     */
    private fun handleFalseState() {
        LogUtil.w(Constants.TAG, "$THIS_NAME handleFalseState() called")
        Toast.makeText(requireContext(), R.string.request_false, Toast.LENGTH_LONG).show()
    }

    companion object {
        fun newInstance() = MyFragment()

        const val TAG = "MyFragment"
    }
}