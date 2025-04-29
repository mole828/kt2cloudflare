import kotlinx.coroutines.Deferred
import kotlin.js.Promise

external interface KvBinding {
    suspend fun put(key: String, value: String): Promise<Unit>
    suspend fun get(key: String): Promise<String?>
}
