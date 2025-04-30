@file:OptIn(DelicateCoroutinesApi::class, ExperimentalJsExport::class)

import kotlinx.coroutines.*
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import org.w3c.fetch.ResponseInit
import kotlin.js.Promise
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

external interface Env {
    val testKv: KvBinding
}

val router = Router<Env> {
    val countKey = "count"
    val countLockKey = "count_lock"
    suspend fun Env.count() = Promise<Int> { resolve, reject ->
        GlobalScope.promise {
            resolve(this@count.testKv.get(countKey).await()?.toInt()?:0)
        }
    }
    suspend fun Env.setCount(v: Int) = this.testKv.put(countKey, v.toString())
    @Deprecated("不是正确的锁使用")
    suspend fun Env.sync(key: String, func: suspend ()->Unit) {
        while(this.testKv.get(key).await()!=null){delay(1.microseconds)}
        this.testKv.put(key, "1").await()
        func()
        this.testKv.delete(key)
    }
    all("/count") { req, env, ctx ->
        val timedValue = measureTimedValue {
            val count = env.count().await()
            ctx.waitUntil(env.setCount(count+1))
            count
        }
        Response(
            "counter: ${timedValue.value}, cost: ${timedValue.duration}"
        )
    }
}


@JsExport
fun fetch(request: Request, env: Env, ctx: ExecutionContext) = GlobalScope.promise {
    router.handle(request, env, ctx)
}