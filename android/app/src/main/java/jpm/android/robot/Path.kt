package jpm.android.robot

import java.util.LinkedList

/**
 * Created by jm on 19/02/17.
 *
 */
/*
https://github.com/mikhaildubov/Computational-geometry
http://geom-java.sourceforge.net/links.html
https://sourceforge.net/projects/jts-topo-suite/
https://sourceforge.net/projects/geom-java/?source=directory
https://sourceforge.net/projects/georegression/?source=directory

https://github.com/Esri/geometry-api-java

http://www.benknowscode.com/2012/09/path-interpolation-using-cubic-bezier_9742.html
http://www.antigrain.com/research/bezier_interpolation/

http://pomax.github.io/bezierinfo/

http://blogs.sitepointstatic.com/examples/tech/canvas-curves/bezier-curve.html
http://learn.scannerlicker.net/2014/04/16/bezier-curves-and-type-design-a-tutorial/

http://stackoverflow.com/questions/8287949/android-how-to-draw-a-smooth-line-following-your-finger/13227841#13227841

Draw the path:
       mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);

Path path = new Path();
if (points.size() > 1) {
    Point prevPoint = null;
    for (int i = 0; i < points.size(); i++) {
        Point point = points.get(i);

        if (i == 0) {
            path.moveTo(point.x, point.y);
        } else {
            float midX = (prevPoint.x + point.x) / 2;
            float midY = (prevPoint.y + point.y) / 2;

            if (i == 1) {
                path.lineTo(midX, midY);
            } else {
                path.quadTo(prevPoint.x, prevPoint.y, midX, midY);
            }
        }
        prevPoint = point;
    }
    path.lineTo(prevPoint.x, prevPoint.y);
}

 */
class Path(val maxSize: Int) {
    private val poses = LinkedList<Pose>()

    fun add(pose: Pose) {
        if(poses.size >= maxSize) poses.removeFirst()
        poses.add(pose)
    }

    fun addPath(path: Path) {
        poses.addAll(path.poses)
    }

    fun get(): List<Pose> = poses

    fun getLatest(numberOfElements: Int): List<Pose> {
        val start = poses.size - numberOfElements

        return if(start >= 0) poses.subList(start, poses.size) else  poses.subList(0, poses.size)
    }

    fun getFrom(time: Long): List<Pose> {

        var start = poses.size

        val it = poses.descendingIterator()

        while(it.hasNext()) {
            val e = it.next()

            if(e.time < time) break
            --start
        }

        return poses.subList(start, poses.size)
    }
}