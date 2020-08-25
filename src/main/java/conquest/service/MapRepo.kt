package conquest.service

import conquest.entry.ConquestMap
import conquest.entry.ImagePoint
import conquest.entry.Region
import org.springframework.stereotype.Component
import java.util.*

@Component()
class MapRepo {
    var maps: HashMap<String, ConquestMap> = HashMap()

    constructor() {
        val reg1 = Region("reg1", "1", ImagePoint(200, 200), null)
        val reg2 = Region("reg2", "2", ImagePoint(200, 400), null)
        val img = ConquestMap("map", "1", "map1", arrayListOf(reg1, reg2))
        maps.put("1", img)
    }

    fun getById(id: String): ConquestMap? {
        return maps.get(id)
    }

    fun put(id: String, map: ConquestMap) {
        maps.put(id, map)
    }
}