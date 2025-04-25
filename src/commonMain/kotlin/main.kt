import kotlinx.coroutines.*
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import org.w3c.fetch.ResponseInit
import kotlin.time.Duration.Companion.seconds

suspend fun doWorld() : Int = coroutineScope {
    listOf(
        async {
            delay(1.seconds)
            "1 seconds"
        },
        async {
            delay(2.seconds)
            "2 seconds"
        }
    ).awaitAll().map {
        if(it.contains("2"))return@coroutineScope 2
    }
    1
}

@OptIn(ExperimentalJsExport::class, DelicateCoroutinesApi::class)
@JsExport
fun fetch(request: Request) = GlobalScope.promise {
    val d = doWorld()
    Response(
        body = "wait for $d",
        ResponseInit()
    )
}