package com.eriko.audithelper

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length < 23) tag else tag.substring(0, 23)
    }

fun Disposable.addTo(c: CompositeDisposable) {
    c.add(this)
}
