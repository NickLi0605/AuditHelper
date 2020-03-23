package com.eriko.audithelper

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

val Any.TAG: String
    get() {
        return if (!javaClass.isAnonymousClass) {
            val name = javaClass.simpleName
            if (name.length <= 23) name else name.substring(0, 23) // first 23 chars
        } else {
            val name = javaClass.name
            if (name.length <= 23) name else name.substring(name.length - 23, name.length) // last 23 chars
        }
    }

fun Disposable.addTo(c: CompositeDisposable) {
    c.add(this)
}
