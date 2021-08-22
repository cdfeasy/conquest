package conquest.security

import conquest.entry.User
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.time.Instant
import java.time.temporal.ChronoUnit

open class CustomOAuth2UserService : DefaultOAuth2UserService() {
    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(oAuth2UserRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User: OAuth2User
        oAuth2User = if (oAuth2UserRequest.clientRegistration.registrationId == "vk") {
            loadVkUser(oAuth2UserRequest)
        } else {
            super.loadUser(oAuth2UserRequest)
        }
        return try {
            oAuth2User
            //   return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (ex: AuthenticationException) {
            throw OAuth2AuthenticationException(OAuth2Error(ex.message))
        } catch (ex: Exception) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw InternalAuthenticationServiceException(ex.message, ex.cause)
        }
    }

    private fun loadVkUser(oAuth2UserRequest: OAuth2UserRequest): OAuth2User {
        val template = RestTemplate()
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add("Content-Type", "application/json")
        headers.add(
            "Authorization",
            oAuth2UserRequest.accessToken.tokenType.value + " " + oAuth2UserRequest.accessToken.tokenValue
        )
        val httpRequest: HttpEntity<*> = HttpEntity<Any?>(headers)
        var uri = oAuth2UserRequest.clientRegistration.providerDetails.userInfoEndpoint.uri
        val userNameAttributeName =
            oAuth2UserRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName
        uri = uri.replace("{user_id}", oAuth2UserRequest.additionalParameters["user_id"].toString())
        return try {
            val entity = template.exchange(
                uri, HttpMethod.GET, httpRequest,
                Any::class.java
            )
            val response: Map<String, Any> = entity.body as Map<String, Any>
            val valueList = response["response"] as ArrayList<*>?
            val userAttributes =
                valueList!![0] as MutableMap<String, Any?>
            userAttributes["sub"] = oAuth2UserRequest.additionalParameters["user_id"].toString()
            userAttributes["ssname"] = "vasya"
            userAttributes[userNameAttributeName] = oAuth2UserRequest.additionalParameters[userNameAttributeName]
            val authorities =
                setOf<GrantedAuthority>(OAuth2UserAuthority(userAttributes))
            val token = OidcIdToken(
                "id",
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.DAYS),
                userAttributes
            )
            val userInfo = OidcUserInfo(userAttributes)
            val defaultOAuth2User =
                DefaultOidcUser(authorities, token, userInfo, userNameAttributeName)
            User("12", "123", "123", "123", "dfsf", "dsf", defaultOAuth2User)
        } catch (ex: HttpClientErrorException) {
            ex.printStackTrace()
            throw RuntimeException(ex.message)
        }
    }
}
