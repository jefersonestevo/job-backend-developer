package br.com.jbd.user.info.controller.mvc

import br.com.jbd.user.info.dto.UserData
import br.com.jbd.user.info.service.UserLoginService
import br.com.jbd.user.info.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = [UserMvcController], secure = false)
class UserMvcControllerTest extends Specification {

    @Autowired
    UserMvcController userInfoController

    @Autowired
    UserService userService

    @Autowired
    UserLoginService userLoginService

    @Autowired
    MockMvc mvc

    void "user-info with valid user"() {
        given: "Any UserData"
        UserData userData = new UserData()
        1 * userLoginService.getCurrentUserId() >> 1L
        1 * userService.findUser(1L) >> Optional.of(userData)

        when: "We invoke the user info url"
        def results = mvc.perform(get("/home"))

        then: "It must return HTTP Status Ok"
        results.andExpect(status().isOk())

        and: "Send the correct view"
        results.andExpect(view().name("home"))

        and: "Populate the model with the UserData content"
        results.andExpect(model().attribute("user", userData))
    }

    void "user-info with not found user"() {
        given: "No UserData found"
        1 * userLoginService.getCurrentUserId() >> 1L
        1 * userService.findUser(1L) >> Optional.empty()

        when: "We invoke the user info url"
        def results = mvc.perform(get("/home"))

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

        @Bean
        UserLoginService userLoginService() {
            return detachedMockFactory.Mock(UserLoginService)
        }
    }

}
