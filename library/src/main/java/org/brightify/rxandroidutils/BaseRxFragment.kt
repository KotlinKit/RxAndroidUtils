package org.brightify.rxandroidutils

import android.content.Intent
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        rxActivityResultSubject.onNext(ActivityResult(requestCode, resultCode, data))
    }

}