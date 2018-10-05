package conquest.entry

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser

data class User(var id:String,var userName:String, var pass:String, var googleId:String, var facebookId:String, var vkId:String, var user:OidcUser ) : OidcUser {
    override fun getName(): String {
        return user.name //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAttributes(): MutableMap<String, Any> {
        return user.attributes
    }

    override fun getIdToken(): OidcIdToken {
        return user.idToken
    }

    override fun getClaims(): MutableMap<String, Any> {
        return user.claims
    }

    override fun getUserInfo(): OidcUserInfo {
        return user.userInfo
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return user.authorities
    }

    override fun toString(): String {
        return "User(id='$id', userName='$userName', pass='$pass', googleId='$googleId', facebookId='$facebookId', vkId='$vkId', user=$user)"
    }


}