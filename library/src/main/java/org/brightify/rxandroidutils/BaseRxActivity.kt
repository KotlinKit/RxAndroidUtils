package org.brightify.rxandroidutils

import android.content.Intent
import android.support.v4.app.Fragment
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import rx.Observable
import rx.lang.kotlin.PublishSubject
import java.io.Serializable

abstract class BaseRxActivity : RxAppCompatActivity() {

    val rxTitle: (titleString: String) -> Unit = { titleString ->
        title = titleString
    }

    val replaceFragment: (fragment: Fragment) -> Unit = { fragment ->
        supportFragmentManager.beginTransaction().replace(android.R.id.content, fragment).commit()
    }

    private val rxActivityResultSubject = PublishSubject<ActivityResult>()

    protected val rxActivityResult: Observable<ActivityResult>
        get() = rxActivityResultSubject.asObservable()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        rxActivityResultSubject.onNext(ActivityResult(requestCode, resultCode, data))
    }

    // FIXME when functions are first class citizens, make it a function
    protected val setResultAndLeave: (model: Serializable) -> Unit = {
        val resultIntent = Intent()
        resultIntent.putExtra("model", it)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

}
