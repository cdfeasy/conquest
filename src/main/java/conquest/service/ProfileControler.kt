package conquest.service

import conquest.entry.LineType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.security.Principal

@Controller
class ProfileControler {

    @GetMapping("/profile/{id}")
    fun greeting(@PathVariable(name = "id", required = true) id: String, model: Model, principal: Principal): String {
        //(principal as OAuth2AuthenticationToken). .add(SimpleGrantedAuthority("MAP_ADMIN"))
        model.addAttribute("id", id)
        return "profile"
    }
}