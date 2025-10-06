package app.wojablo.alk

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class DatabaseDriverFactory(private val context: Context) {
  actual suspend fun createDriver(
    schema: SqlSchema<QueryResult.AsyncValue<Unit>>,
    name: String
  ): SqlDriver? = withContext(Dispatchers.IO) {
    AndroidSqliteDriver(schema.synchronous(), context, name)
  }
}
