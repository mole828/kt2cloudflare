import kotlinx.coroutines.coroutineScope
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import org.w3c.fetch.ResponseInit

typealias HandlerType = suspend()->Response
class Router(
    builder: Router.() -> Unit = {}
) {
    private val routeMap = mutableMapOf<String, HandlerType>()
    fun all(path: String, f: HandlerType) {
        routeMap[path] = f
    }
    suspend fun handle(request: Request): Response = coroutineScope {
        val (proto, fullPath) = request.url.split("://")
        val (host, path) = fullPath.split("/", limit = 2)

        println("path: $path")
        routeMap[path]?.let {
            println("has handler: $path")
            return@coroutineScope it.invoke()
        }

        Response(
            body = JSON.stringify(mapOf(
                "proto" to proto,
                "fullPath" to fullPath,
                "host" to host,
                "path" to path,
            )),
            ResponseInit()
        )
    }
}