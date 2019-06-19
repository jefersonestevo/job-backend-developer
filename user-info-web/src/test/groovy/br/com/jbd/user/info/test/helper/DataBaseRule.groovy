package br.com.jbd.user.info.test.helper

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import groovy.sql.Sql
import org.dbunit.database.DatabaseDataSourceConnection
import org.dbunit.dataset.DefaultDataSet
import org.dbunit.dataset.DefaultTable
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.operation.DatabaseOperation
import org.junit.rules.ExternalResource
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.support.PropertiesLoaderUtils

class DataBaseRule extends ExternalResource {

    HikariDataSource dataSource
    DatabaseDataSourceConnection dbConnection
    Sql sql

    @Override
    protected void before() throws Throwable {
        Properties props = PropertiesLoaderUtils.loadProperties(new ClassPathResource("test.properties"));

        dataSource = new HikariDataSource(new HikariConfig(
                driverClassName: "org.postgresql.Driver",
                jdbcUrl: props["jdbc.url"],
                username: props["jdbc.username"],
                password: props["jdbc.password"]
        ))

        this.dbConnection = new DatabaseDataSourceConnection(dataSource)
        this.sql = Sql.newInstance(dataSource)
    }

    @Override
    protected void after() {
        dataSource.close()
    }

    void deleteTablesContent(String... tables) throws Exception {
        for (String table : tables) {
            IDataSet dataSet = new DefaultDataSet(new DefaultTable(table))
            DatabaseOperation.DELETE_ALL.execute(dbConnection, dataSet)
        }
    }

    void insert(String path) throws Exception {
        InputStream resourceStream = getInputStream(path)

        ReplacementDataSet replacementDataSet = new ReplacementDataSet(new FlatXmlDataSet(resourceStream))
        replacementDataSet.addReplacementObject("null", null)

        DatabaseOperation.INSERT.execute(dbConnection, replacementDataSet)
    }

    InputStream getInputStream(String path) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader()
        return contextClassLoader.getResourceAsStream(path)
    }

}
