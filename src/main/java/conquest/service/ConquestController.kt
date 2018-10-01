package conquest.service

import com.fasterxml.jackson.databind.ObjectMapper
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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@Controller
class ConquestController {
    @Autowired
    lateinit var repo:MapRepo


    @GetMapping("/greeting/{id}")
    fun greeting(@PathVariable(name = "id", required = true) id: String, model: Model): String {
        model.addAttribute("id", id)
        var typeLst = arrayListOf(LineType("simple"), LineType("rad"));
        model.addAttribute("lineTypes", typeLst)
        model.addAttribute("map", repo.getById(id))
        return "greeting"
    }

    @GetMapping(value = "/greeting/img/{imageId}")
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

    @PostMapping(value = "/greeting/add/{id}")
    fun bla(@RequestBody body:String,@PathVariable(name = "id", required = true) id: String):String {
        System.out.println(body)
        return "greeting"
    }
}