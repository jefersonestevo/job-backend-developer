package br.com.jbd.user.info.repository

import br.com.jbd.user.info.model.User
import br.com.jbd.user.info.test.helper.DataBaseRule
import org.junit.Rule
import spock.lang.Specification

class UserDataBaseRepositoryIntegrationTest extends Specification {

    UserDataBaseRepository userDataBaseRepository

    @Rule
    DataBaseRule dataBaseRule = new DataBaseRule()

    def setup() {
        dataBaseRule.deleteTablesContent("JBD_USER_IMPORT", "JBD_USER_ADDRESS", "JBD_USER_INFO", "JBD_USER")

        this.userDataBaseRepository = new UserDataBaseRepository(dataBaseRule.dataSource)
    }

    def "findById with valid user id"() {
        given: "A database populate with some user information"
        dataBaseRule.insert("db/user-info.xml")

        when: "We call findById with a valid user id"
        Optional<User> optional = userDataBaseRepository.findById(1L)

        then: "The user must be returned"
        optional.isPresent()

        User user = optional.get()
        user.id == 1L
        user.login == "user1"
        user.userInfo.name == "User 01"
        user.addresses.size() == 2
    }

    def "findById with invalid user id"() {
        given: "A database populate with some user information"
        dataBaseRule.insert("db/user-info.xml")

        when: "We call findById with a invalid user id"
        Optional<User> user = userDataBaseRepository.findById(123456L)

        then: "The optional must return empty"
        !user.isPresent()
    }

    def "registerImport"() {
        given: "A database populate with some user information"
        dataBaseRule.insert("db/user-info.xml")

        when: "We register the import of a user id"
        userDataBaseRepository.registerImport(1L)

        then: "It must be registered on the correct table"
        def ids = dataBaseRule.sql.rows("SELECT ID FROM JBD_USER_IMPORT").collect {it.ID}
        [1L] == ids
    }

    def "findIdByLogin with valid user login"() {
        given: "A database populate with some user information"
        dataBaseRule.insert("db/user-info.xml")

        when: "We call findIdByLogin with a valid login"
        Long id = userDataBaseRepository.findIdByLogin("user1")

        then: "It must return the correct user id"
        id == 1L
    }

    def "findIdByLogin with invalid user login"() {
        given: "A database populate with some user information"
        dataBaseRule.insert("db/user-info.xml")

        when: "We call findIdByLogin with a invalid login"
        Long id = userDataBaseRepository.findIdByLogin("invalid_user")

        then: "It must return null"
        id == null
    }

}
