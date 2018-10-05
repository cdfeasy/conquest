package conquest

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
import java.util.*

class MyAuthoritiesMapper: GrantedAuthoritiesMapper {
    override fun mapAuthorities(authorities: MutableCollection<out GrantedAuthority>?): MutableCollection<out GrantedAuthority> {
        var lst= ArrayList(authorities);
        lst.add(SimpleGrantedAuthority("map_owner"))
        return lst;

    }
}