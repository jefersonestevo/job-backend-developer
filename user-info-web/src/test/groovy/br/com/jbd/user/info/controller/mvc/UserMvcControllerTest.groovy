package br.com.jbd.user.info.controller.mvc


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class UserMvcControllerTest extends Specification {

    @Autowired(required = false)
    UserMvcController userInfoController

    void "context load"() {
        expect: "Controller must be created"
        userInfoController
    }

}
