package br.com.jbd.user.info.service

import br.com.jbd.user.info.repository.UserDataBaseRepository
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import spock.lang.Specification

class UserLoginServiceImplTest extends Specification {

    UserLoginServiceImpl userLoginService

    UserDataBaseRepository userDataBaseRepository

    ServletRequestAttributes requestAttributes

    def setup() {
        userDataBaseRepository = Mock(UserDataBaseRepository)
        requestAttributes = Mock(ServletRequestAttributes)

        RequestContextHolder.setRequestAttributes(requestAttributes)

        userLoginService = new UserLoginServiceImpl()
        userLoginService.userDataBaseRepository = userDataBaseRepository
    }

    def "UserLoginServiceImpl must be serializable"() {
        expect:
        userLoginService instanceof Serializable
    }

    def "getCurrentUserId must call the repository only once"() {
        given:
        SecurityContext securityContext = Mock(SecurityContext)
        Authentication authentication = Mock(Authentication)
        authentication.name >> "user1"
        securityContext.authentication >> authentication
        SecurityContextHolder.setContext(securityContext)

        and:
        Map<String, Object> attrMap = [:]

        3 * requestAttributes.getAttribute("jbd.user.id", RequestAttributes.SCOPE_SESSION) >> {args -> attrMap.get(args[0])}
        1 * requestAttributes.setAttribute("jbd.user.id", 1L, RequestAttributes.SCOPE_SESSION) >> {args -> attrMap.put(args[0], args[1])}

        when: "We call the current user id multiple times"
        def result1 = userLoginService.getCurrentUserId()
        def result2 = userLoginService.getCurrentUserId()
        def result3 = userLoginService.getCurrentUserId()

        then: "All the results must be equals"
        result1 == result2
        result1 == result3

        and: "It must have called the repository only once"
        1 * userDataBaseRepository.findIdByLogin("user1") >> 1L
    }

}
