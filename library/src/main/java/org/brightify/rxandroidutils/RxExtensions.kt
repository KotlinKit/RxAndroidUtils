package org.brightify.rxandroidutils

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * @author <a href="mailto:hyblmatous@gmail.com">Matous Hybl</a>
 */

fun <T, U> Observable<T>.rewrite(value: U): Observable<U> = map { value }

fun <T, U> Observable<T>.rewrite(func: () -> U): Observable<U> = map { func() }

fun <T> doOnIOPresentOnUI(): (Observable<T>) -> Observable<T> = {
    it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.doOnIOPresentOnUI(): Observable<T> =
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.filterNulls(): Observable<T> = this.filter { it == null }
