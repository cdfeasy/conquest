package conquest

import conquest.service.ClientRepo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Description
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.endpoint.NimbusAuthorizationCodeTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeAuthenticationProvider
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.util.stream.Collectors
import javax.annotation.Resource


//import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;


@Configuration
@EnableWebSecurity
@PropertySource("classpath:application.properties")
open class SecurityConfig : WebSecurityConfigurerAdapter() {
    private val CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration."
    private val clients = arrayListOf("google", "facebook", "vk")
    @Resource
    lateinit var env: Environment
//    @Autowired
//    lateinit var detailsService: UserDetailsServiceImpl
//    @Bean
//    open fun propertyPlaceHolderConfigurer(): PropertySourcesPlaceholderConfigurer {
//        return PropertySourcesPlaceholderConfigurer()
//    }

    @Throws(Exception::class)
    protected override fun configure(http: HttpSecurity) {
      //  http.authorizeRequests().antMatchers("/profile/*").authenticated()
        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .oauth2Login()
                .clientRegistrationRepository(clientRegistrationRepository())
                .authorizedClientService(authorizedClientService())
                .userInfoEndpoint()
                //   .userAuthoritiesMapper(MyAuthoritiesMapper())
                .oidcUserService(MyOAuth2UserService())
        http.csrf().disable();

//        http
//                .authorizeRequests()
//                .antMatchers("/resources/**", "/login", "/map/*").permitAll()
//                .antMatchers("/admin/**").hasRole("ADMIN")
//                .and()
//
//                .formLogin()
//                .loginPage("/login")
//                .defaultSuccessUrl("/admin/home")
//                .failureUrl("/loginfailed")
//                .permitAll()
//                .and()
//
//                .logout()
//                .logoutRequestMatcher(AntPathRequestMatcher("/logout"))
//                .deleteCookies("JSESSIONID")
//                .invalidateHttpSession(true)
  //              .and();
    }


    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        var provider = OidcAuthorizationCodeAuthenticationProvider(accessTokenResponseClient(), OidcUserService())
        provider.setAuthoritiesMapper(MyAuthoritiesMapper())
        auth.authenticationProvider(provider)
    }

    @Bean
    open fun accessTokenResponseClient(): OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

        return NimbusAuthorizationCodeTokenResponseClient()
    }

    @Bean
    open fun authorizedClientService(): OAuth2AuthorizedClientService {
        return ClientRepo(
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

    @Bean
    @Description("Thymeleaf template resolver serving HTML 5")
    open fun templateResolver(): ClassLoaderTemplateResolver {

        val templateResolver = ClassLoaderTemplateResolver()

        templateResolver.prefix = "templates/"
        templateResolver.isCacheable = false
        templateResolver.suffix = ".html"
        templateResolver.setTemplateMode("HTML5")
        templateResolver.characterEncoding = "UTF-8"

        return templateResolver
    }

    @Bean
    @Description("Thymeleaf template engine with Spring integration")
    open fun templateEngine(): SpringTemplateEngine {

        val templateEngine = SpringTemplateEngine()
        templateEngine.setTemplateResolver(templateResolver())
        templateEngine.addDialect(org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect())

        return templateEngine
    }

//    open override fun userDetailsService(): UserDetailsService {
//        return detailsService
//    }


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
        if (client.equals("vk")) {
            val builder = ClientRegistration.withRegistrationId(client)
            builder.clientAuthenticationMethod(ClientAuthenticationMethod.POST)
            builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            builder.redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
            builder.scope("email")
            builder.authorizationUri("https://oauth.vk.com/authorize")
            builder.tokenUri("https://oauth.vk.com/access_token")
            builder.userInfoUri("https://api.vk.com/method/users.get")
            builder.userNameAttributeName("code")
            builder.clientName("Vk")


            return builder
                    .clientId(clientId).clientSecret(clientSecret).build();
        }
        return null
    }
}