/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data

import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.events.CommandListener
import org.flywaydb.core.Flyway
import org.flywaydb.core.internal.info.MigrationInfoDumper
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.reflections.Reflections
import java.net.URLEncoder
import java.util.Properties
import java.util.function.Consumer
import javax.persistence.Entity


class PostgresManager() {

    val sessionFactory: SessionFactory

    private fun urlEncode(input: String): String {
        return URLEncoder.encode(input, "utf-8")
    }

    init {
        val connectionString = Config.INS.databaseConnectionString
        val username = Config.INS.databaseUsername
        val password = Config.INS.databasePassword


        require(connectionString.isNotBlank()) { "connection string cannot be empty or blank!" }

        if (Config.INS.isDatabaseFlywayEnabled) {
            val flyway = Flyway.configure()
                .baselineVersion("0")
                .validateMigrationNaming(true)
                .failOnMissingLocations(true)
                .dataSource(connectionString, username, password)
                .locations("classpath:db_migrations")
                .load()

            val beforeMigrationInfo = flyway.info()
            val migrationResult = flyway.migrate()
            val afterMigrationInfo = flyway.info()

            if (migrationResult.migrationsExecuted > 0 && !beforeMigrationInfo.all().contentEquals(afterMigrationInfo.all())) {
                CascadeBot.LOGGER.info("Migration info before migration:")
                MigrationInfoDumper.dumpToAsciiTable(beforeMigrationInfo.all()).trim().split("\n").forEach {
                    CascadeBot.LOGGER.info(it)
                }
                CascadeBot.LOGGER.info("Migration info after migration:")
                MigrationInfoDumper.dumpToAsciiTable(afterMigrationInfo.all()).trim().split("\n").forEach {
                    CascadeBot.LOGGER.info(it)
                }
            }
        }

        val dbConfig = createDBConfig(connectionString, username, password)

        sessionFactory = dbConfig.buildSessionFactory()
    }

    private fun createDBConfig(connectionString: String, dbUsername: String, dbPassword: String): Configuration {
        val dbConfig = Configuration()

        val entityReflections = Reflections("org.cascadebot.cascadebot.data.entities")
        val classes: Set<Class<*>> = entityReflections.getTypesAnnotatedWith(Entity::class.java)

        classes.forEach { dbConfig.addAnnotatedClass(it) }

        dbConfig.addPackage("org.cascadebot.cascadebot.data.entities")

        val hibernateProperties = Properties()

        hibernateProperties["hibernate.dialect"] = "org.hibernate.dialect.PostgreSQL10Dialect"
        hibernateProperties["hibernate.connection.provider_class"] =
            "org.hibernate.hikaricp.internal.HikariCPConnectionProvider"
        hibernateProperties["hibernate.connection.driver_class"] = "org.postgresql.Driver"
        hibernateProperties["hibernate.connection.url"] = connectionString
        hibernateProperties["hibernate.connection.username"] = dbUsername
        hibernateProperties["hibernate.connection.password"] = dbPassword
        hibernateProperties["hibernate.hikari.maximumPoolSize"] = "20"
        hibernateProperties["hibernate.types.print.banner"] = "false"

        dbConfig.addProperties(hibernateProperties)

        return dbConfig
    }

    fun <T : Any> transaction(work: Session.() -> T?): T? {
        if (CommandListener.getSqlSession().get() != null) {
            return createTransaction(CommandListener.getSqlSession().get(), work)
        } else {
            val session = this.sessionFactory.openSession()
            session.use {
                return createTransaction(it, work)
            }
        }
    }

    fun transactionNoReturn(work: Session.() -> Unit) {
        if (CommandListener.getSqlSession().get() != null) {
            createTransaction(CommandListener.getSqlSession().get(), work)
        } else {
            val session = this.sessionFactory.openSession()
            session.use {
                createTransaction(it, work)
            }
        }
    }

    fun transactionNoReturn(work: Consumer<Session>) {
        transactionNoReturn() kotlinTransaction@{
            work.accept(this)
        }
    }

}

private fun <T : Any> createTransaction(session: Session, work: Session.() -> T?): T? {
    try {
        session.transaction.timeout = 3
        session.transaction.begin()

        val value = work(session);

        session.transaction.commit()
        return value;
    } catch (e: RuntimeException) {
        session.transaction.rollback()
        throw e; // TODO: Or display error?
    }
}
