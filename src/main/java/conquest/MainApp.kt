package conquest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = arrayOf("conquest",
        "conquest.service"))
open class MainApp {

    fun main(args: Array<String>) {
        runApplication<MainApp>(*args)
    }
}

object start {
    @JvmStatic
    fun main(args: Array<String>) {
        var mainApp = MainApp();
        mainApp.main(args);

    }
}
