import kotlin.js.Promise

external interface ExecutionContext {
    fun waitUntil(promise: Promise<*>)
}