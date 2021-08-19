package sang.gondroid.cheesetodo.presentation.my

import android.app.Activity
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
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
import java.lang.Exception


class MyFragment : BaseFragment<MyViewModel, FragmentMyBinding>() {
    private val THIS_NAME = this::class.simpleName

    override val viewModel: MyViewModel by viewModel()

    override fun getDataBinding(): FragmentMyBinding
            = FragmentMyBinding.inflate(layoutInflater)

    private val firebaseAuth : FirebaseAuth by inject()

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
     * 1. Task<GoogleSignInAccount> 객체로 변환할 수 있다
     * 2. GoogleSignInAccount 객체를 반환받으며, 사용자의 Google 계정 정보를 얻을 수 있다.
     */
    private val loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                // 구글 authentication에 요청한 데이터를 가져옴
                task.getResult(ApiException::class.java)?.let { accont ->
                    Log.d(Constants.TAG, "$THIS_NAME loginLauncher getResult() : ${accont.idToken}, ${accont.displayName}")

                    viewModel.saveToken(accont.idToken ?: throw Exception(), accont.displayName ?: throw Exception())
                }
            }catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun signInGoogle() {
        val signInIntent = gsc.signInIntent
        loginLauncher.launch(signInIntent)
    }

    override fun initViews() = with(binding) {
        loginButton.setOnClickListener {
            signInGoogle()
        }
    }

    override fun observeData() {}

    companion object {
        fun newInstance() = MyFragment()

        const val TAG = "MyFragment"
    }
}