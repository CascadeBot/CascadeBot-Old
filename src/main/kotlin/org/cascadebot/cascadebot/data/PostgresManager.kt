/*
 * Copyright (c) 2021 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.data

import org.cascadebot.cascadebot.events.CommandListener
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.reflections.Reflections
import java.net.URLEncoder
import java.util.Properties
import java.util.function.Consumer
import javax.persistence.Entity

class PostgresManager(connectionString: String) {

    val sessionFactory: SessionFactory

    private fun urlEncode(input: String): String {
        return URLEncoder.encode(input, "utf-8")
    }

    init {
        require(connectionString.isNotBlank()) { "connection string cannot be empty or blank!" }

        val dbConfig = Configuration()

        val entityReflections = Reflections("org.cascadebot.cascadebot.data.entities")
        val classes: Set<Class<*>> = entityReflections.getTypesAnnotatedWith(Entity::class.java)

        classes.forEach { dbConfig.addAnnotatedClass(it) }

        dbConfig.addPackage("org.cascadebot.cascadebot.data.entities")

        val hibernateProperties = Properties()

        hibernateProperties["hibernate.dialect"] = "org.hibernate.dialect.PostgreSQL10Dialect"
        hibernateProperties["hibernate.connection.provider_class"] = "org.hibernate.hikaricp.internal.HikariCPConnectionProvider"
        hibernateProperties["hibernate.connection.driver_class"] = "org.postgresql.Driver"
        hibernateProperties["hibernate.connection.url"] = connectionString
        hibernateProperties["hibernate.hikari.maximumPoolSize"] = "20"
        hibernateProperties["hibernate.types.print.banner"] = "false"

        dbConfig.addProperties(hibernateProperties)
        sessionFactory = dbConfig.buildSessionFactory()
    }

    fun <T : Any> transaction(work: Session.()->T?) : T? {
        if (CommandListener.getSqlSession().get() != null) {
            return createTransaction(CommandListener.getSqlSession().get(), work)
        } else {
            val session = this.sessionFactory.openSession()
            session.use {
                return createTransaction(it, work)
            }
        }
    }

    fun transactionNoReturn(work: Session.()->Unit) {
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

private fun <T : Any> createTransaction(session: Session, work: Session.()->T?) : T? {
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
