package app.wojablo.alk

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.db.QueryResult

import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver

actual class DatabaseDriverFactory {
    actual suspend fun createDriver(
        schema: SqlSchema<QueryResult.AsyncValue<Unit>>,
        name: String
    ): SqlDriver? {
        return createDefaultWebWorkerDriver()
    }
}

