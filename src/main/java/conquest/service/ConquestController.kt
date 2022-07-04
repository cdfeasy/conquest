package conquest.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import conquest.entry.ConquestMap
import conquest.entry.ImagePoint
import conquest.entry.LineType
import conquest.entry.Region
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import com.google.gson.Gson
import com.nimbusds.jose.util.StandardCharset
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.*
import java.net.URLDecoder
import java.security.Principal
import java.util.*
import java.util.function.Predicate


@Controller
class ConquestController {
    @Autowired
    lateinit var repo:MapRepo
    val om:ObjectMapper=jacksonObjectMapper();


    @GetMapping(value=["/map/{id}"])
    fun greeting(@PathVariable(name = "id", required = true) id: String, model: Model, principal: Principal): String {
        //(principal as OAuth2AuthenticationToken). .add(SimpleGrantedAuthority("MAP_ADMIN"))
        model.addAttribute("id", id)
        var typeLst = arrayListOf(LineType("simple"), LineType("rad"));
        model.addAttribute("lineTypes", typeLst)
        model.addAttribute("blabla1", "[]")
        model.addAttribute("data", "[]")
        val map = repo.getById(id)
        model.addAttribute("map", map)
        System.out.println(map?.regions?.size)
        return "map"
    }

    @GetMapping(value=["/map/img/{imageId}"])
    @ResponseBody
    fun helloWorld(@PathVariable imageId: String): ByteArray {
        System.out.println("show " + imageId)
        var image = ImageIO.read(this.javaClass.getResourceAsStream("/orig.jpg"))
        var image1 = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        var g = image1.graphics as Graphics2D

        val graphics = image.graphics

        g.drawImage(image, null, 0, 0);

        g.color = Color.LIGHT_GRAY
        // graphics.fillRect(0, 0, 200, 50)
        g.color = Color.BLACK
        g.font = Font("Arial Black", Font.BOLD, 20)
        g.drawString("blabla", 10, 25)
        g.color = Color(255, 140, 0);
        g.drawPolygon(intArrayOf(100, 200, 300), intArrayOf(50, 150, 70), 3);
        g.drawPolygon(intArrayOf(110, 200, 290), intArrayOf(55, 140, 75), 3);


        var out = ByteArrayOutputStream()
        //  StreamUtils.copy(inp,out)
        ImageIO.write(image1, "png", out);

        return out.toByteArray()
    }

    @PostMapping(value=["/map/add/{id}"])
    fun add(@RequestBody body:String,@PathVariable(name = "id", required = true) id: String, model: Model):String {
        var parts=body.split("&");
        val pointJson:String=URLDecoder.decode(parts[0].substring(5),StandardCharset.UTF_8.name())
        System.out.println("add"+pointJson)
        val map = repo.getById(id)
        var point:ImagePoint =om.readValue(pointJson)
        var region=Region(parts[1].substring(9),parts[1].substring(9),point, null)
        map?.regions?.add(region)
//
//
//        model.addAttribute("id", id)
//        var typeLst = arrayListOf(LineType("simple"), LineType("rad"));
//        model.addAttribute("lineTypes", typeLst)
//        model.addAttribute("map", map)
        return "redirect:/map/"+id;
    }

    @PostMapping(value=["/map/edit/{id}"])
    fun edit(@RequestBody body:String,@PathVariable(name = "id", required = true) id: String, model: Model):String {
        var parts=body.split("&");
        val pointJson:String=URLDecoder.decode(parts[0].substring(5),StandardCharset.UTF_8.name())
        System.out.println(pointJson)
        val map = repo.getById(id)
        var point:ImagePoint =om.readValue(pointJson)
        var name=parts[1].substring(9);
        map?.regions?.filter { s->s.name.equals(name) }?.first()?.point=point;
        return "redirect:/map/"+id;
    }

    @PostMapping( value=["/map/remove/{id}"])
    fun delete(@RequestBody body:String,@PathVariable(name = "id", required = true) id: String, model: Model):String {
        var parts=body.split("&");
        val pointJson:String=URLDecoder.decode(parts[0].substring(5),StandardCharset.UTF_8.name())
        System.out.println(pointJson)
        val map = repo.getById(id)
        var point:ImagePoint =om.readValue(pointJson)
        var name=parts[1].substring(9);
        map?.regions?.removeIf{s->s.name.equals(name)};
        return "redirect:/map/"+id;
    }
}