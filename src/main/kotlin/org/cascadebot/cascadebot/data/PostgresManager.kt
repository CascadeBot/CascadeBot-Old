/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data

import org.cascadebot.cascadebot.data.Postgres.transaction
import org.cascadebot.cascadebot.events.CommandListener
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.reflections.Reflections
import org.yaml.snakeyaml.util.UriEncoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Properties
import java.util.function.Consumer
import javax.persistence.Entity
import org.cascadebot.cascadebot.data.transaction as kotlinTransaction

class PostgresManager(hosts: String, database: String, username: String, password: String, val options: Map<String, String>) {

    val database: String = urlEncode(database)
    val username: String = urlEncode(username)
    val password: String = urlEncode(password)

    private val connectionString: String by lazy {
        "jdbc:postgresql://$hosts/${this.database}?user=${this.username}&password=${this.password}${if (options.isNotEmpty()) { 
            var builder: StringBuilder = StringBuilder()
            for (entry in options.entries) {
                builder.append('&').append(urlEncode(entry.key)).append('=').append(urlEncode(entry.value))
            }
            builder.toString();
        } else ""}"
    }

    val sessionFactory: SessionFactory

    private fun urlEncode(input: String): String {
        return URLEncoder.encode(input, "utf-8")
    }

    init {
        require(hosts.isNotBlank()) { "hosts cannot be empty or blank!" }
        require(database.isNotBlank()) { "database cannot be empty or blank!" }
        require(username.isNotBlank()) { "username cannot be empty or blank!" }
        require(password.isNotBlank()) { "password cannot be empty or blank!" }

        val dbConfig = Configuration()

        val reflections = Reflections("org.cascadebot.cascadebot.data.entities")
        val classes: Set<Class<*>> = reflections.getTypesAnnotatedWith(Entity::class.java)

        classes.forEach { dbConfig.addAnnotatedClass(it) }

        val hibernateProperties = Properties()

        hibernateProperties["hibernate.dialect"] = "org.hibernate.dialect.PostgreSQL10Dialect"
        hibernateProperties["hibernate.connection.provider_class"] = "org.hibernate.hikaricp.internal.HikariCPConnectionProvider"
        hibernateProperties["hibernate.connection.driver_class"] = "org.postgresql.Driver"
        hibernateProperties["hibernate.connection.url"] = connectionString
        hibernateProperties["hibernate.hikari.maximumPoolSize"] = "20"

        dbConfig.addProperties(hibernateProperties)
        sessionFactory = dbConfig.buildSessionFactory()
    }

}

fun transaction(postgresManager: PostgresManager, work: Session.()->Unit) {
    if (CommandListener.getSqlSession().get() != null) {
        createTransaction(CommandListener.getSqlSession().get(), work)
    } else {
        val session = postgresManager.sessionFactory.openSession()
        session.use {
            createTransaction(it, work)
        }
    }
}

private fun createTransaction(session: Session, work: Session.()->Unit) {
    try {
        session.transaction.timeout = 3
        session.transaction.begin()

        work(session);

        session.transaction.commit()
    } catch (e: RuntimeException) {
        session.transaction.rollback()
        throw e; // TODO: Or display error?
    }
}

object Postgres {
    @JvmStatic
    fun transaction(postgresManager: PostgresManager, work: Consumer<Session>) {
        kotlinTransaction(postgresManager) {
            work.accept(this)
        }
    }
}

