package prockt.sketches.sk032

import prockt.KApplet
import prockt.api.Coord
import prockt.api.Beam

class Sketch032: KApplet() {

    private val mirrorCount = 50
    private val mirrors = mutableListOf<MirrorObject>()

    override fun setup() {
        repeat(mirrorCount){
            mirrors.add( MirrorObject(Coord(random(width), random(height)), random(20f, 100f), radians(random(0f, 360f))))
        }

        noCursor()
    }

    override fun draw() {
        background(BLACK)
        stroke(WHITE)

        val center = Coord(width/2, height/2)
        val mouse = Coord(mouseX, mouseY)
        val mouseBeam = Beam(center, mouse)

        mirrors.forEach { mirror ->
            mirror.draw(this)
        }

        run(mouseBeam)

        mouseBeam.draw(this, color(255, 50))

        fill(GREEN)
        circle(mouse, 8)

        fill(WHITE)
        circle(center, 5)

        noLoop()
    }

    override fun mouseMoved() {
        loop()
    }

    fun run(beam: Beam?){


        if(beam == null) return

        val reflextion = processBeam(beam)

        run(reflextion)
    }

    fun processBeam(beam: Beam?): Beam?{
        if(beam == null) return null

        var closestMirror: MirrorObject?  = null
        var closestDistance = width * width * height.toFloat()
        var closestCollisionCoord: Coord? = null
        mirrors.forEach { mirror ->
            if(mirror != beam.originMirror) {
                val collisionCoord = mirror.collision(beam)
                if (collisionCoord != null) {
                    val distance = beam.start.dist(collisionCoord)

                    if (distance < closestDistance) {
                        closestDistance = distance
                        closestMirror = mirror
                        closestCollisionCoord = collisionCoord
                    }
                }
            }
        }

        if(closestMirror != null){
            val collisionBeam = Beam(beam.start, closestCollisionCoord!!)
            collisionBeam.draw(this, MAGENTA)
        }

        val reflectBeam = closestMirror?.reflection(beam)
        reflectBeam?.setOrigin(closestMirror)

        if(reflectBeam == null){
            //Beam exits drawing area
            beam.draw(this, MAGENTA)
        }

        return reflectBeam
    }

    override fun keyPressed() {
        mirrors.clear()
        repeat(mirrorCount){
            mirrors.add(MirrorObject(Coord(random(width), random(height)), random(20f, 100f), radians(random(0f, 360f))))
        }
    }

    override fun mouseClicked() {
        val mouseCoord = Coord(mouseX, mouseY)
        var closestMirror: MirrorObject? = null
        var closestDistance = width * height
        mirrors.forEach { mirror ->
            val distance = mouseCoord.dist(mirror.coord)
            if (distance < closestDistance) {
                closestDistance = distance.toInt()
                closestMirror = mirror
            }
        }
        closestMirror!!.flip()
    }
}