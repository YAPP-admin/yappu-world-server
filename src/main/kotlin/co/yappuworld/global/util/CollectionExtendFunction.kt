package co.yappuworld.global.util

fun <T> List<T>.ifNotEmpty(action: (List<T>) -> Unit) {
    if (this.isNotEmpty()) action(this)
}
