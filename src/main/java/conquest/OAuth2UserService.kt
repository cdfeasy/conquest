package conquest

import conquest.entry.User
import org.springframework.core.ParameterizedTypeReference
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.util.StringUtils
import java.util.*

class MyOAuth2UserService : OAuth2UserService<OidcUserRequest, OidcUser> {
    override fun loadUser(userRequest: OidcUserRequest): OidcUser {
        val authority = OidcUserAuthority(userRequest.idToken, null)
        val authorities = HashSet<GrantedAuthority>()
        authorities.add(authority)
        authorities.add(SimpleGrantedAuthority("BLABLA"))

        val user: OidcUser

        val userNameAttributeName = userRequest.clientRegistration
                .providerDetails.userInfoEndpoint.userNameAttributeName
        var userInfo:OidcUserInfo=OidcUserInfo(hashMapOf<String,Any>("sssname" to "vasya"))
        user = DefaultOidcUser(authorities, userRequest.idToken, userInfo, "sssname")
        var _user:User =User("12","123","123","123","dfsf","dsf",user)
        return _user
    }
}