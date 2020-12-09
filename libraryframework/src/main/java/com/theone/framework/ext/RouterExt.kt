package cn.magicwindow.core.ext

import android.app.Activity
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.theone.framework.R

/**
 * @author Zhiqiang
 * @date 2018/4/7 17:44
 * @description Ui切换
 */


/**
 * 进入Fragment
 * @param fragment BaseFragment
 * @param containerViewId Int
 * @param addToBackStack Boolean 默认为false
 * @param backStackName String?
 */
fun AppCompatActivity.toFragment(
    @IdRes containerViewId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = false,
    backStackName: String? = null
) {
    val fm = supportFragmentManager
    val transaction = fm.beginTransaction()
    transaction.replace(containerViewId, fragment)
    if (addToBackStack) transaction.addToBackStack(backStackName)
    transaction.commit()
}

/**
 * 进入Fragment
 * @param targetFragment BaseFragment
 * @param containerViewId Int
 * @param addToBackStack Boolean 默认为true
 * @param backStackName String?
 */
fun Fragment.toFragment(
    @IdRes containerViewId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = true,
    backStackName: String? = null
) {
    val fm = parentFragmentManager
    val transaction = fm.fragmentTransaction()
    transaction.replace(containerViewId, fragment)
    if (addToBackStack) transaction.addToBackStack(backStackName)
    transaction.commit()
}

/**
 * 带Anim的FragmentTransaction
 * @receiver FragmentManager
 * @return FragmentTransaction
 */
fun FragmentManager.fragmentTransaction(): FragmentTransaction {
    return beginTransaction().setCustomAnimations(
        R.anim.in_from_right,
        R.anim.stay,
        R.anim.nothing,
        R.anim.out_to_right
    )
}

/**
 * 带Anim的结束finish
 * @receiver BaseActivity
 */
fun Activity.finishWithAnim() {
    finish()
    overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right)
}
