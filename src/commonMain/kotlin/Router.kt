import kotlinx.coroutines.coroutineScope
import org.w3c.dom.url.URL
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import org.w3c.fetch.ResponseInit

typealias HandlerType = suspend()->Response
class Router(
    builder: Router.() -> Unit = {}
) {
    private val routeMap = mutableMapOf<String, HandlerType>()
    
    init {
        builder()
    }
    fun all(path: String, f: HandlerType) {
        routeMap[path] = f
    }
    suspend fun handle(request: Request): Response = coroutineScope {
        val (proto, fullPath) = request.url.split("://")
        val (host, _path) = fullPath.split("/", limit = 2)
        val url = URL(request.url)
        val path = url.pathname

        println("path: $path")
        routeMap[path]?.let {
            println("has handler: $path")
            return@coroutineScope it.invoke()
        }

        Response(
            body = mapOf(
                "url" to JSON.stringify(url),
                "pathname" to url.pathname,
                "pathHas" to routeMap.keys,
            ),
            ResponseInit()
        )
    }
}