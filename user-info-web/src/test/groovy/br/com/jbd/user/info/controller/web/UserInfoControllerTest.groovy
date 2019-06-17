package br.com.jbd.user.info.controller.web


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class UserInfoControllerTest extends Specification {

    @Autowired(required = false)
    UserInfoController userInfoController

    void "context load"() {
        expect: "Controller must be created"
        userInfoController
    }

}
