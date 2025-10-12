package app.wojablo.alk

import app.cash.sqldelight.Query
import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement

class InMemorySqlDriver : SqlDriver {
    private val tables = mutableMapOf<String, MutableList<Map<String, Any?>>>()
    private val listeners = mutableMapOf<String, MutableSet<Query.Listener>>()

    override fun close() {
        tables.clear()
        listeners.clear()
    }

    override fun addListener(vararg queryKeys: String, listener: Query.Listener) {
        queryKeys.forEach { key ->
            listeners.getOrPut(key) { mutableSetOf() }.add(listener)
        }
    }

    override fun removeListener(vararg queryKeys: String, listener: Query.Listener) {
        queryKeys.forEach { key ->
            listeners[key]?.remove(listener)
        }
    }

    override fun notifyListeners(vararg queryKeys: String) {
        queryKeys.forEach { key ->
            listeners[key]?.forEach { it.queryResultsChanged() }
        }
    }

    override fun currentTransaction(): Transacter.Transaction? {
        return null
    }

    override fun execute(
        identifier: Int?,
        sql: String,
        parameters: Int,
        binders: (SqlPreparedStatement.() -> Unit)?
    ): QueryResult.Value<Long> {
        val stmt = InMemoryPreparedStatement()
        binders?.invoke(stmt)

        when {
            sql.trim().startsWith("CREATE", ignoreCase = true) -> {
                val tableName = extractTableName(sql, "CREATE TABLE")
                if (tableName != null) {
                    tables[tableName] = mutableListOf()
                }
            }
            sql.trim().startsWith("INSERT", ignoreCase = true) -> {
                val tableName = extractTableName(sql, "INSERT INTO")
                if (tableName != null) {
                    val row = stmt.boundValues.mapKeys { it.key.toString() }
                    tables[tableName]?.add(row)
                }
            }
            sql.trim().startsWith("DELETE", ignoreCase = true) -> {
                val tableName = extractTableName(sql, "DELETE FROM")
                tables[tableName]?.clear()
            }
        }

        return QueryResult.Value(1L)
    }

    override fun <R> executeQuery(
        identifier: Int?,
        sql: String,
        mapper: (SqlCursor) -> QueryResult<R>,
        parameters: Int,
        binders: (SqlPreparedStatement.() -> Unit)?
    ): QueryResult<R> {
        val stmt = InMemoryPreparedStatement()
        binders?.invoke(stmt)

        val tableName = extractTableName(sql, "SELECT") ?: ""
        val rows = tables[tableName] ?: emptyList()

        val cursor = InMemoryCursor(rows)
        return mapper(cursor)
    }

    override fun newTransaction(): QueryResult<Transacter.Transaction> {
        return QueryResult.Value(object : Transacter.Transaction() {
            override val enclosingTransaction: Transacter.Transaction?
                get() = null

            override fun endTransaction(successful: Boolean): QueryResult<Unit> {
                return QueryResult.Unit
            }
        })
    }

    private fun extractTableName(sql: String, keyword: String): String? {
        val regex = when (keyword) {
            "CREATE TABLE" -> """CREATE\s+TABLE\s+(?:IF\s+NOT\s+EXISTS\s+)?(\w+)""".toRegex(RegexOption.IGNORE_CASE)
            "INSERT INTO" -> """INSERT\s+INTO\s+(\w+)""".toRegex(RegexOption.IGNORE_CASE)
            "DELETE FROM" -> """DELETE\s+FROM\s+(\w+)""".toRegex(RegexOption.IGNORE_CASE)
            "SELECT" -> """FROM\s+(\w+)""".toRegex(RegexOption.IGNORE_CASE)
            else -> return null
        }
        return regex.find(sql)?.groupValues?.get(1)
    }
}

private class InMemoryPreparedStatement : SqlPreparedStatement {
    val boundValues = mutableMapOf<Int, Any?>()

    override fun bindBytes(index: Int, bytes: ByteArray?) {
        boundValues[index] = bytes
    }

    override fun bindDouble(index: Int, double: Double?) {
        boundValues[index] = double
    }

    override fun bindLong(index: Int, long: Long?) {
        boundValues[index] = long
    }

    override fun bindString(index: Int, string: String?) {
        boundValues[index] = string
    }

    override fun bindBoolean(index: Int, boolean: Boolean?) {
        boundValues[index] = boolean
    }
}

private class InMemoryCursor(private val rows: List<Map<String, Any?>>) : SqlCursor {
    private var currentIndex = -1

    override fun next(): QueryResult.Value<Boolean> {
        currentIndex++
        return QueryResult.Value(currentIndex < rows.size)
    }

    override fun getString(index: Int): String? {
        return rows.getOrNull(currentIndex)?.values?.elementAtOrNull(index) as? String
    }

    override fun getLong(index: Int): Long? {
        val value = rows.getOrNull(currentIndex)?.values?.elementAtOrNull(index)
        return when (value) {
            is Long -> value
            is Int -> value.toLong()
            is String -> value.toLongOrNull()
            else -> null
        }
    }

    override fun getBytes(index: Int): ByteArray? {
        return rows.getOrNull(currentIndex)?.values?.elementAtOrNull(index) as? ByteArray
    }

    override fun getDouble(index: Int): Double? {
        val value = rows.getOrNull(currentIndex)?.values?.elementAtOrNull(index)
        return when (value) {
            is Double -> value
            is Float -> value.toDouble()
            is String -> value.toDoubleOrNull()
            else -> null
        }
    }

    override fun getBoolean(index: Int): Boolean? {
        return rows.getOrNull(currentIndex)?.values?.elementAtOrNull(index) as? Boolean
    }
}
