@file:OptIn(DelicateCoroutinesApi::class, ExperimentalJsExport::class)

import kotlinx.coroutines.*
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import org.w3c.fetch.ResponseInit
import kotlin.js.Promise

external interface Env {
    val testKv: KvBinding
}

val router = Router<Env> {
    val countKey = "count"
    all("/count") { req, env ->
        val count = env.testKv.get(countKey).await()?.toInt()?:0
        val newCount = count + 1
        env.testKv.put(countKey, newCount.toString()).await()
        Response(
            "counter: $newCount"
        )
    }
}


@JsExport
fun fetch(request: Request, env: Env) = GlobalScope.promise {
    router.handle(request, env)
}