package conquest

import org.junit.Test
import java.awt.Color
import javax.imageio.ImageIO
import java.awt.Color.LIGHT_GRAY
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.File


class Tests{
    @Test
    fun test(){
        val key = "Sample"
        val image = ImageIO.read(File("c:/tmp/orig.jpg"))
        val graphics = image.graphics
        graphics.color = Color.LIGHT_GRAY
       // graphics.fillRect(0, 0, 200, 50)
        graphics.color = Color.BLACK
        graphics.font = Font("Arial Black", Font.BOLD, 20)
        graphics.drawString(key, 10, 25)
        graphics.color = Color(255,140,0);
        graphics.drawPolygon(intArrayOf(100,200,300), intArrayOf(50,150,70), 3);
        graphics.drawPolygon(intArrayOf(110,200,290), intArrayOf(55,140,75), 3);
        ImageIO.write(image, "jpg", File(
                "C:/tmp/1.jpg"))
        println("Image Created")
    }


}