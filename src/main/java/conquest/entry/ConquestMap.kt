package conquest.entry
data class ConquestMap(var regions:List<Region>) {


}

data class ImagePoint(var x: Int, var y: Int) {

}

data class Region(var name: String, var points: List<ImagePoint>, var description: String = "") {
}

