package sample

class Map{

    companion object {
        val map1 = mutableListOf(
                mutableListOf(1,1,1,1,1,1,1,1,1,1),// camera here
                mutableListOf(1,0,0,0,0,1,1,0,0,1),
                mutableListOf(1,0,1,0,0,1,1,0,1,1),
                mutableListOf(1,0,1,0,0,0,0,0,0,1),
                mutableListOf(1,0,1,0,0,0,0,0,0,1),
                mutableListOf(1,0,1,0,0,0,0,0,1,1),
                mutableListOf(1,0,1,0,0,0,1,0,0,1),
                mutableListOf(1,0,1,1,1,1,1,0,1,1),
                mutableListOf(1,0,1,0,0,0,0,0,0,1),
                mutableListOf(1,1,1,1,1,1,1,1,1,1)
        )

        fun get(x: Int, y: Int):Int{
            return map1[y][x]
        }
    }
}