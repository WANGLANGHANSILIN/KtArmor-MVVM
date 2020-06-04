package com.zhan.ktarmor.account.vm

import android.text.TextUtils
import com.zhan.ktarmor.R
import com.zhan.ktarmor.account.data.AccountRepository
import com.zhan.ktarmor.account.data.response.LoginRsp
import com.zhan.ktarmor.common.data.BaseResponse
import com.zhan.ktwing.ext.logd
import com.zhan.mvvm.mvvm.livedata.CommonLiveData
import com.zhan.mvvm.mvvm.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

/**
 * @author  hyzhan
 * @date    2019/5/23
 * @desc    TODO
 */
class LoginViewModel : BaseViewModel<AccountRepository>() {

    val loginData = CommonLiveData<LoginRsp>()

    val concurrentData = CommonLiveData<LoginRsp>()

    fun login(account: String, password: String) {

        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
            showToast(R.string.account_or_password_empty)
            return
        }

        /**
         *  DSL方式发起网络请求
         *
         *  若需要 code, message, 则调用 onSuccessRsp 方法
         *  若需要 loginRsp, 则调用 onSuccess 方法
         */
//        quickLaunch<LoginRsp> {
//
//            request { repository.login(account, password) }
//
//            onSuccess { loginData.value = it }
//
//            onFailure { loginData.failureMessage = it }
//
//            onException { loginData.exception = it }
//        }

//        /**
//         * 等同上面 quickLaunch
//         */
//        superLaunch(loginData) {
//            request { repository.login(account, password) }
//        }

        /**
         * 等同上面 superLaunch
         */
        superLaunchRequest(loginData) { repository.login(account, password) }
    }

    fun loginByConcurrent() {
        launchUI({

            val now = System.currentTimeMillis()
            val result = async { repository.login("", "") }
            val result2 = async { repository.login("", "") }

            result.await().execute({

            }, {
                concurrentData.postFailureMessage("result $it, nowTime = ${System.currentTimeMillis() - now}")
            })

            result2.await().execute({

            }, {
                concurrentData.postFailureMessage("result222 $it, nowTime = ${System.currentTimeMillis() - now}")
            })
        })

    }

    private fun asyncLaunchUI(function: () -> Unit) {

    }

    private suspend fun oneTask(): Int {
        delay(2000)
        return 1
    }

    private suspend fun twoTask(): Int {
        delay(3000)
        return 2
    }

    fun loginByTest(account: String, password: String) {
        superLaunch(loginData) {
            request { repository.testLogin(account, password) }
        }
    }

    fun loginByTestService(account: String, password: String) {
        superLaunch(loginData) {
            request { repository.serviceTestLogin(account, password) }
        }
    }


}