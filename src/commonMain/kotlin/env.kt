external interface KvBinding {
    suspend fun put(key: String, value: String)
    suspend fun get(key: String): String
}
