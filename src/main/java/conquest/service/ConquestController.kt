package conquest.service

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.util.StreamUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Image
import java.awt.image.DataBufferByte
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import javax.imageio.ImageIO
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.BufferedImageOp


@Controller
class ConquestController {

    @GetMapping("/greeting")
    fun greeting(@RequestParam(name = "name", required = false, defaultValue = "World") name: String, model: Model): String {
        model.addAttribute("name", name)
        return "greeting"
    }
    @GetMapping(value = "/greeting/img/{imageId}")
    @ResponseBody
    fun helloWorld(@PathVariable imageId:String):ByteArray  {
        var image = ImageIO.read(File("c:/tmp/orig.jpg"))
        var image1 = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        var g = image1.graphics as Graphics2D

        val graphics = image.graphics

        g.drawImage(image, null, 0,0);

        g.color = Color.LIGHT_GRAY
        // graphics.fillRect(0, 0, 200, 50)
        g.color = Color.BLACK
        g.font = Font("Arial Black", Font.BOLD, 20)
        g.drawString("blabla", 10, 25)
        g.color = Color(255,140,0);
        g.drawPolygon(intArrayOf(100,200,300), intArrayOf(50,150,70), 3);
        g.drawPolygon(intArrayOf(110,200,290), intArrayOf(55,140,75), 3);


        var out=ByteArrayOutputStream()
      //  StreamUtils.copy(inp,out)
        ImageIO.write(image1,"png",out);

        return out.toByteArray()
    }

}