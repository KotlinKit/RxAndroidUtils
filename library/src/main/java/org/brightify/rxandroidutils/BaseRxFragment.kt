package org.brightify.rxandroidutils

import android.content.Intent
import android.view.MenuItem
import com.trello.rxlifecycle.components.support.RxFragment
import rx.Observable
import rx.lang.kotlin.PublishSubject

/**
 * @author <a href="mailto:hyblmatous@gmail.com">Matous Hybl</a>
 */
abstract class BaseRxFragment : RxFragment() {

    private val rxActivityResultSubject = PublishSubject<ActivityResult>()

    protected val rxActivityResult: Observable<ActivityResult>
        get() = rxActivityResultSubject.asObservable()

    private val rxMenuSubject = PublishSubject<MenuItem>()

    val rxMenuItemSelected: Observable<MenuItem>
        get() = rxMenuSubject

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            rxMenuSubject.onNext(it)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        rxActivityResultSubject.onNext(ActivityResult(requestCode, resultCode, data))
    }

}