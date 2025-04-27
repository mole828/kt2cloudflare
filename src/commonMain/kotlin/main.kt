@file:OptIn(DelicateCoroutinesApi::class, ExperimentalJsExport::class)

import kotlinx.coroutines.*
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import org.w3c.fetch.ResponseInit
import kotlin.js.Promise

var counter = 0
val router = Router {
    all("/count") {
        Response(
            "counter: ${counter++}"
        )
    }
}

@JsExport
fun fetch(request: Request) = GlobalScope.promise {
    router.handle(request)
}