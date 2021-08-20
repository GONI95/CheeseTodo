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

    override fun observeData() {

    }

    companion object {
        fun newInstance() = MyFragment()

        const val TAG = "MyFragment"
    }
}
