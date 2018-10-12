package conquest.service

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap

data class User(var id: String, var pass: String) {

}
open class ClientRepo : OAuth2AuthorizedClientService {

    val authorizedClients = ConcurrentHashMap<String, OAuth2AuthorizedClient>()
    lateinit var clientRegistrationRepository: ClientRegistrationRepository

    constructor(clientRegistrationRepository: ClientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository
    }


    override fun removeAuthorizedClient(clientRegistrationId: String?, principalName: String) {
        val registration = this.clientRegistrationRepository.findByRegistrationId(clientRegistrationId)
        if (registration != null) {
            this.authorizedClients.remove(this.getIdentifier(registration, principalName))
        }
    }

    override fun saveAuthorizedClient(authorizedClient: OAuth2AuthorizedClient, principal: Authentication) {
      //  principal.authorities= arrayListOf(GrantedAuthority("map_admin"))
   //     (principal as OAuth2AuthenticationToken).authorities.add()
        this.authorizedClients.put(this.getIdentifier(authorizedClient.getClientRegistration(), principal.getName()),
                                    authorizedClient)
    }

    override fun <T : OAuth2AuthorizedClient?> loadAuthorizedClient(clientRegistrationId: String, principalName: String): T? {
        val registration = this.clientRegistrationRepository.findByRegistrationId(clientRegistrationId) ?: return null
        return this.authorizedClients[this.getIdentifier(registration, principalName)] as T
    }

    private fun getIdentifier(registration: ClientRegistration, principalName: String): String {
        val identifier = "[" + registration.registrationId + "][" + principalName + "]"
        return Base64.getEncoder().encodeToString(identifier.toByteArray())
    }

}