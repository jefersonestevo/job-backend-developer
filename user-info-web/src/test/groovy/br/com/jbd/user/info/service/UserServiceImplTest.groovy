package br.com.jbd.user.info.service

import br.com.jbd.user.info.dto.Address
import br.com.jbd.user.info.dto.UserData
import br.com.jbd.user.info.model.User
import br.com.jbd.user.info.model.UserAddress
import br.com.jbd.user.info.model.UserInfo
import br.com.jbd.user.info.repository.UserDataBaseRepository
import br.com.jbd.user.info.repository.UserDataMongoRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Specification

class UserServiceImplTest extends Specification {

    UserServiceImpl userService

    UserDataBaseRepository userDataBaseRepository
    UserDataMongoRepository userDataMongoRepository
    TransactionTemplate transactionTemplate

    def setup() {
        userDataBaseRepository = Mock(UserDataBaseRepository)
        userDataMongoRepository = Mock(UserDataMongoRepository)
        transactionTemplate = Mock(TransactionTemplate)

        userService = new UserServiceImpl()
        userService.userDataBaseRepository = userDataBaseRepository
        userService.userDataMongoRepository = userDataMongoRepository
        userService.transactionTemplate = transactionTemplate
    }

    def "findUser must not call jdbc when UserData is found on mongo"() {
        given: "Any UserData"
        UserData userData = new UserData()

        and: "This data is returned from the mongo interface"
        1 * userDataMongoRepository.findById(1L) >> Optional.of(userData)

        when: "We try to find the user"
        Optional<UserData> result = userService.findUser(1L)

        then: "It must return the user provided by the mongo interface"
        result.isPresent()
        result.get() == userData

        and: "No more methods should be called on mocks"
        0 * userDataBaseRepository._
        0 * transactionTemplate._
    }

    def "when the user is not found on mongoDB, findUser must find the user data on the database and save it to mongo"() {
        given: "A DB User"
        User user = createUser()
        1 * userDataBaseRepository.findById(1L) >> Optional.of(user)

        and: "The user is not found on mongoDB"
        1 * userDataMongoRepository.findById(1L) >> Optional.empty()

        when: "We try to find the user"
        Optional<UserData> result = userService.findUser(1L)

        then: "It must return the user provided by the mongo interface"
        result.isPresent()

        and: "The user data must have all the data from the original User"
        UserData userData = result.get()
        userData.id == user.id
        userData.login == user.login
        userData.name == user.userInfo.name
        userData.lastName == user.userInfo.lastName
        userData.email == user.userInfo.email
        userData.phoneNumber == user.userInfo.phoneNumber

        for (int i = 0; i < user.addresses.size(); i++) {
            UserAddress userAddress = user.addresses[i]
            Address address = userData.addresses[i]

            assert address.street == userAddress.street
            assert address.number == userAddress.number
            assert address.city == userAddress.city
            assert address.state == userAddress.state
            assert address.country == userAddress.country
        }

        and: "It must persist the user on mongoDB"
        1 * userDataMongoRepository.save(_)

        and: "Use a transaction"
        1 * transactionTemplate.execute(_ as TransactionCallback) >> {args ->
            (args[0] as TransactionCallback).doInTransaction(null)
        }

        and: "Register the import on the JDBC DataBase"
        1 * userDataBaseRepository.registerImport(1L)
    }

    def "when the user is not found on mongoDB, findUser must find the user data on the database and save it to mongo even when there is a duplicate key on the register import"() {
        given: "A DB User"
        User user = createUser()
        1 * userDataBaseRepository.findById(1L) >> Optional.of(user)

        and: "The user is not found on mongoDB"
        1 * userDataMongoRepository.findById(1L) >> Optional.empty()

        and: "Throws a Duplicate Key Exception while register the import on the database"
        1 * userDataBaseRepository.registerImport(1L) >> {throw new DuplicateKeyException("")}

        when: "We try to find the user"
        Optional<UserData> result = userService.findUser(1L)

        then: "It must return the user provided by the mongo interface"
        result.isPresent()

        and: "It must persist the user on mongoDB"
        1 * userDataMongoRepository.save(_)

        and: "Use a transaction"
        1 * transactionTemplate.execute(_ as TransactionCallback) >> {args ->
            (args[0] as TransactionCallback).doInTransaction(null)
        }
    }

    User createUser() {
        UserInfo userInfo = new UserInfo(
            userId: 1L,
            name: "User 01",
            lastName: "Last User 01",
            email: "user1@email.com",
            phoneNumber: "1123412321"
        )

        List<UserAddress> addresses = []
        (1..2).each { index ->
            addresses.add(new UserAddress(
                id: index,
                street: "Street ${index}",
                number: "${index}",
                city: "Sao Paulo",
                state: "SP",
                country: "Brasil"
            ))
        }

        return new User(
                id: 1L,
                login: "user1",
                userInfo: userInfo,
                addresses: addresses
        )
    }
}
