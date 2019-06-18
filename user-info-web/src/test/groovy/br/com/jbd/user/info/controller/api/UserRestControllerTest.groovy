package br.com.jbd.user.info.controller.api

import br.com.jbd.user.info.dto.UserData
import br.com.jbd.user.info.service.UserService
import org.hamcrest.Matchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = [UserRestController], secure = false)
class UserRestControllerTest extends Specification {

    @Autowired
    UserRestController userRestController

    @Autowired
    UserService userService

    @Autowired
    MockMvc mvc

    void "user-info with valid user"() {
        given: "Any UserData"
        UserData userData = new UserData(id: 1, login: "login 1", name: "Name 01")
        1 * userService.findUser(1L) >> Optional.of(userData)

        when: "We invoke the api user info url"
        def results = mvc.perform(get("/api/user/info/1"))

        then: "It must return HTTP Status Ok"
        results.andExpect(status().isOk())

        and: "Must return the contents of the returned UserData on the output json"
        results.andExpect(jsonPath('$.id', Matchers.is(userData.id as int)))
        results.andExpect(jsonPath('$.login', Matchers.is(userData.login)))
        results.andExpect(jsonPath('$.name', Matchers.is(userData.name)))
    }

    void "user-info with not found user"() {
        given: "No UserData found"
        1 * userService.findUser(1L) >> Optional.empty()

        when: "We invoke the user info url"
        def results = mvc.perform(get("/api/user/info/1"))

        then: "It must return HTTP Status Not Found"
        results.andExpect(status().isNotFound())
    }

    @TestConfiguration
    static class TestConfig {
        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()

        @Bean
        UserService userService() {
            return detachedMockFactory.Mock(UserService)
        }
    }

}
