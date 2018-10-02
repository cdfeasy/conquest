package conquest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import java.util.stream.Collectors
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import javax.annotation.Resource


@Configuration
@EnableWebSecurity
@PropertySource("classpath:application.properties")
open class SecurityConfig : WebSecurityConfigurerAdapter() {
    private val CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration."
    private val clients = arrayListOf("google", "facebook")
    @Resource
    lateinit var env: Environment

//    @Bean
//    open fun propertyPlaceHolderConfigurer(): PropertySourcesPlaceholderConfigurer {
//        return PropertySourcesPlaceholderConfigurer()
//    }

    @Throws(Exception::class)
    protected override fun configure(http: HttpSecurity) {
        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .oauth2Login()
                .clientRegistrationRepository(clientRegistrationRepository())
                .authorizedClientService(authorizedClientService());
    }

    @Bean
    open fun authorizedClientService(): OAuth2AuthorizedClientService {
        return InMemoryOAuth2AuthorizedClientService(
                clientRegistrationRepository())
    }

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
                CLIENT_PROPERTY_KEY + client + ".client-id");
        if (clientId == null) {
            return null;
        }
        var clientSecret = env?.getProperty(
                CLIENT_PROPERTY_KEY + client + ".client-secret");
        if (client.equals("google")) {
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(clientId).clientSecret(clientSecret).build();
        }
        if (client.equals("facebook")) {
            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                    .clientId(clientId).clientSecret(clientSecret).build();
        }
        return null;
    }
}