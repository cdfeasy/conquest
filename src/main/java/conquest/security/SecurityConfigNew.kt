package conquest.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.web.SecurityFilterChain
import java.util.stream.Collectors
import javax.annotation.Resource


@Configuration
@EnableWebSecurity
open class SecurityConfigNew {
    private val clients = arrayListOf("google", "facebook", "vk")
    private val CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration."

    @Resource
    lateinit var env: Environment

    @Bean
    @Throws(Exception::class)
    open fun filterChain(http: HttpSecurity): SecurityFilterChain? {
//        http
//            .authorizeHttpRequests { authz ->
//                authz
//                    .anyRequest().authenticated()
//            }
//            .httpBasic(withDefaults())
        http.authorizeRequests().anyRequest().authenticated()
            .and()
            .oauth2Login()
            .clientRegistrationRepository(clientRegistrationRepository())
            //  .authorizedClientService(authorizedClientService())
            .userInfoEndpoint().userService(CustomOAuth2UserService())
//            //   .userAuthoritiesMapper(MyAuthoritiesMapper())
            .oidcUserService(MyOAuth2UserService())
        http.csrf().disable()

        return http.build()
    }

//    @Bean
//    open fun authorizedClientService(): OAuth2AuthorizedClientService {
//        return ClientRepo(
//            clientRegistrationRepository())
//    }

    @Bean
    open fun clientRegistrationRepository(): ClientRegistrationRepository {
        val registrations = clients.stream()
            .map { c -> getRegistration(c) }
            .filter({ registration -> registration != null })
            .collect(Collectors.toList())

        return InMemoryClientRegistrationRepository(registrations)
    }

    fun getRegistration(client: String): ClientRegistration? {
        var clientId = env?.getProperty(
            CLIENT_PROPERTY_KEY + client + ".client-id"
        );
        if (clientId == null) {
            return null;
        }
        var clientSecret = env?.getProperty(
            CLIENT_PROPERTY_KEY + client + ".client-secret"
        );
        if (client.equals("google")) {
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                .clientId(clientId).clientSecret(clientSecret).build();
        }
        if (client.equals("facebook")) {
            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                .clientId(clientId).clientSecret(clientSecret).build();
        }
        if (client.equals("vk")) {
            val builder = ClientRegistration.withRegistrationId(client)
            builder.clientAuthenticationMethod(ClientAuthenticationMethod.POST)
            builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            builder.redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
            builder.scope("email")
            builder.authorizationUri("https://oauth.vk.com/authorize?v=5.95")
            builder.tokenUri("https://oauth.vk.com/access_token")
            builder.userInfoUri("https://api.vk.com/method/users.get?user_id={user_id}&v=5.95&access_token=" + "cdada599cdada599cdada599e4cdcbdeedccdadcdada5999611768fd68f2d9c68bf4cdb")
            builder.userNameAttributeName("user_id")
            builder.clientName("vk")
            builder.registrationId("vk");
            return builder
                .clientId(clientId).clientSecret(clientSecret).build();
        }
        return null
    }

//    @Bean
//    open fun users(): UserDetailsManager? {
//        val user: UserDetails = User.withDefaultPasswordEncoder()
//            .username("user")
//            .password("password")
//            .roles("USER")
//            .build()
//        val users = InMemoryUserDetailsManager()
//        users.createUser(user)
//        return users
//    }
}