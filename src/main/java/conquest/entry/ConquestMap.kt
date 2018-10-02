package conquest.entry

import java.util.*

data class ConquestMap(var name:String?,var id:String?, var description: String?,var regions:ArrayList<Region>) {

}

data class ImagePoint(var x: Int, var y: Int) {

}

data class Region(var name: String, var points: ArrayList<ImagePoint>, var description: String = "") {
}

