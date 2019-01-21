package com.artear.rigel.extensions

inline fun <T> T.ifNull(block: () -> T?): T? {
    if (this == null) return block()
    return this
}