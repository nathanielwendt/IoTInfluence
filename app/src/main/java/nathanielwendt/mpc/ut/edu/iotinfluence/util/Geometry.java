package nathanielwendt.mpc.ut.edu.iotinfluence.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

//Borrowed from: https://github.com/MartinThoma/algorithms/tree/master/crossingLineCheck/Geometry/src
//Found at: http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
public class Geometry {

    public static class LineSegment {
        Point first;
        Point second;
        String name;


        public LineSegment(double ax, double ay, double bx, double by){
            this.first = new Point(ax, ay);
            this.second = new Point(bx, by);
        }

        /**
         * @param a first point of this line
         * @param b the second point of this line
         */
        public LineSegment(Point a, Point b) {
            this.first = a;
            this.second = b;
            this.name = "LineSegment";
        }

        public LineSegment(Point a, Point b, String name) {
            this.first = a;
            this.second = b;
            this.name = name;
        }

        /**
         * Get the bounding box of this line by two points. The first point is in
         * the lower left corner, the second one at the upper right corner.
         *
         * @return the bounding box
         */
        public Point[] getBoundingBox() {
            Point[] result = new Point[2];
            result[0] = new Point(Math.min(first.x, second.x), Math.min(first.y,
                    second.y));
            result[1] = new Point(Math.max(first.x, second.x), Math.max(first.y,
                    second.y));
            return result;
        }

        public Point[] getPointsOnSegment() {
            if(first.x == second.x){
                double diff = second.y - first.y;
                Point[] ret = new Point[(int) diff + 1];
                int count = 0;
                for(double y = first.y; y <= second.y; y++){
                    ret[count++] = new Point(first.x,y);
                }
                return ret;
            } else if(first.y == second.y) {
                double diff = second.x - first.x;
                Point[] ret = new Point[(int) diff + 1];
                int count = 0;
                for(double x = first.x; x <= second.x; x++){
                    ret[count++] = new Point(x,first.y);
                }
                return ret;
            } else {
                throw new RuntimeException("Horizontal or Vertical lines only supported for getPointsonSegment");
            }
        }

        @Override
        public String toString() {
            if (name.equals("LineSegment")) {
                return "LineSegment [" + first + " to " + second + "]";
            } else {
                return name;
            }
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((first == null) ? 0 : first.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((second == null) ? 0 : second.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            LineSegment other = (LineSegment) obj;
            if (first == null) {
                if (other.first != null)
                    return false;
            } else if (!first.equals(other.first))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (second == null) {
                if (other.second != null)
                    return false;
            } else if (!second.equals(other.second))
                return false;
            return true;
        }
    }

    public static class Point {

        public double x;
        public double y;

        /**
         * @param x the x-coordinate
         * @param y the y-coordinate
         */
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + "|" + y + ")";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            long temp;
            temp = Double.doubleToLongBits(x);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(y);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Point other = (Point) obj;
            if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
                return false;
            if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
                return false;
            return true;
        }
    }

    public static final double EPSILON = 0.000001;

    /**
     * Calculate the cross product of two points.
     * @param a first point
     * @param b second point
     * @return the value of the cross product
     */
    public static double crossProduct(Point a, Point b) {
        return a.x * b.y - b.x * a.y;
    }

    /**
     * Check if bounding boxes do intersect. If one bounding box
     * touches the other, they do intersect.
     * @param a first bounding box
     * @param b second bounding box
     * @return <code>true</code> if they intersect,
     *         <code>false</code> otherwise.
     */
    public static boolean doBoundingBoxesIntersect(Point[] a, Point[] b) {
        return a[0].x <= b[1].x && a[1].x >= b[0].x && a[0].y <= b[1].y
                && a[1].y >= b[0].y;
    }

    /**
     * Checks if a Point is on a line
     * @param a line (interpreted as line, although given as line
     *                segment)
     * @param b point
     * @return <code>true</code> if point is on line, otherwise
     *         <code>false</code>
     */
    public static boolean isPointOnLine(LineSegment a, Point b) {
        // Move the image, so that a.first is on (0|0)
        LineSegment aTmp = new LineSegment(new Point(0, 0), new Point(
                a.second.x - a.first.x, a.second.y - a.first.y));
        Point bTmp = new Point(b.x - a.first.x, b.y - a.first.y);
        double r = crossProduct(aTmp.second, bTmp);
        return Math.abs(r) < EPSILON;
    }

    /**
     * Checks if a point is right of a line. If the point is on the
     * line, it is not right of the line.
     * @param a line segment interpreted as a line
     * @param b the point
     * @return <code>true</code> if the point is right of the line,
     *         <code>false</code> otherwise
     */
    public static boolean isPointRightOfLine(LineSegment a, Point b) {
        // Move the image, so that a.first is on (0|0)
        LineSegment aTmp = new LineSegment(new Point(0, 0), new Point(
                a.second.x - a.first.x, a.second.y - a.first.y));
        Point bTmp = new Point(b.x - a.first.x, b.y - a.first.y);
        return crossProduct(aTmp.second, bTmp) < 0;
    }

    /**
     * Check if line segment first touches or crosses the line that is
     * defined by line segment second.
     *
     * @param a line segment interpreted as line
     * @param b line segment
     * @return <code>true</code> if line segment first touches or
     *                           crosses line second,
     *         <code>false</code> otherwise.
     */
    public static boolean lineSegmentTouchesOrCrossesLine(LineSegment a,
                                                          LineSegment b) {
        return isPointOnLine(a, b.first)
                || isPointOnLine(a, b.second)
                || (isPointRightOfLine(a, b.first) ^ isPointRightOfLine(a,
                b.second));
    }

    /**
     * Check if line segments intersect
     * @param a first line segment
     * @param b second line segment
     * @return <code>true</code> if lines do intersect,
     *         <code>false</code> otherwise
     */
    public static boolean doLinesIntersect(LineSegment a, LineSegment b) {
        Point[] box1 = a.getBoundingBox();
        Point[] box2 = b.getBoundingBox();
        return doBoundingBoxesIntersect(box1, box2)
                && lineSegmentTouchesOrCrossesLine(a, b)
                && lineSegmentTouchesOrCrossesLine(b, a);
    }

    /**
     * Check if x is right end of l
     * @param x an x-coordinate of one endpoint
     * @param l a line
     * @return <code>true</code> if p is right end of l
     *         <code>false</code> otherwise
     */
    private static boolean isRightEnd(double x, LineSegment l) {
        // TODO: Do I need EPSILON here?
        return x >= l.first.x && x >= l.second.x;
    }

    /**
     * Get all interectionLines by applying a sweep line algorithm.
     * @param lines all lines you want to check, in no order
     * @return a list that contains all pairs of intersecting lines
     */
    public static Set<LineSegment[]> getAllIntersectingLines(LineSegment[] lines) {
        // TODO: This one is buggy! See tests
        class EventPointLine implements Comparable<EventPointLine> {
            Double sortingKey;
            LineSegment line;

            public EventPointLine(double sortingKey, LineSegment line) {
                this.sortingKey = sortingKey;
                this.line = line;
            }

            @Override
            public int compareTo(EventPointLine o) {
                return sortingKey.compareTo(o.sortingKey);
            }
        }

        class SweepLineComperator implements Comparator<LineSegment> {
            @Override
            public int compare(LineSegment o1, LineSegment o2) {
                double o1FirstX = o1.first.x < o1.second.x ? o1.first.y
                        : o1.second.y;
                double o2FirstX = o2.first.x < o2.second.x ? o2.first.y
                        : o2.second.y;

                if (Math.abs(o1FirstX - o2FirstX) < EPSILON) {
                    return 0;
                } else if (o1FirstX > o2FirstX) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }

        Set<LineSegment[]> intersections = new HashSet<LineSegment[]>();
        List<EventPointLine> eventPointSchedule = new ArrayList<EventPointLine>();

        for (LineSegment line : lines) {
            eventPointSchedule.add(new EventPointLine(line.first.x, line));
            eventPointSchedule.add(new EventPointLine(line.second.x, line));
        }

        Collections.sort(eventPointSchedule);

        SweepLineComperator comperator = new SweepLineComperator();
        TreeSet<LineSegment> sweepLine = new TreeSet<LineSegment>(comperator);

        for (EventPointLine p : eventPointSchedule) {
            // TODO: an schnittpunkten aendert sich die Reihenfolge
            // der Kanten
            if (isRightEnd(p.sortingKey, p.line)) {
                LineSegment above = sweepLine.higher(p.line);
                LineSegment below = sweepLine.lower(p.line);
                sweepLine.remove(p.line);

                if (below != null && above != null
                        && doLinesIntersect(above, below)) {
                    LineSegment[] tmp = new LineSegment[2];
                    tmp[0] = above;
                    tmp[1] = p.line;
                    intersections.add(tmp);
                }
            } else {
                if (Math.abs(p.line.first.x - p.line.second.x) < EPSILON) {
                    // this is a vertical line

                    for (LineSegment tmpLine : sweepLine) {
                        if (doLinesIntersect(tmpLine, p.line)) {
                            LineSegment[] tmp = new LineSegment[2];
                            tmp[0] = tmpLine;
                            tmp[1] = p.line;
                            intersections.add(tmp);
                        }
                    }
                } else {

                    // Get identical lines
                    NavigableSet<LineSegment> h = sweepLine.subSet(p.line,
                            true, p.line, true);

                    for (LineSegment tmpLine : h) {
                        if (doLinesIntersect(tmpLine, p.line)) {
                            LineSegment[] tmp = new LineSegment[2];
                            tmp[0] = tmpLine;
                            tmp[1] = p.line;
                            intersections.add(tmp);
                        }

                    }

                    sweepLine.add(p.line);

                    // check if it intersects with line above or below
                    LineSegment above = sweepLine.higher(p.line);
                    LineSegment below = sweepLine.lower(p.line);

                    if (above != null && doLinesIntersect(above, p.line)) {
                        LineSegment[] tmp = new LineSegment[2];
                        tmp[0] = above;
                        tmp[1] = p.line;
                        intersections.add(tmp);
                    }

                    if (below != null && doLinesIntersect(below, p.line)) {
                        LineSegment[] tmp = new LineSegment[2];
                        tmp[0] = below;
                        tmp[1] = p.line;
                        intersections.add(tmp);
                    }
                }
            }
        }

        /* Check if endpoints are equal */
        for (int i = 0; i < eventPointSchedule.size(); i++) {
            int j = i + 1;
            while (j < eventPointSchedule.size()
                    && Math.abs(eventPointSchedule.get(i).sortingKey
                    - eventPointSchedule.get(j).sortingKey) < EPSILON) {
                j += 1;

                LineSegment[] tmp = new LineSegment[2];
                tmp[0] = eventPointSchedule.get(i).line;
                tmp[1] = eventPointSchedule.get(j).line;
                if (doLinesIntersect(tmp[0], tmp[1])
                        && !intersections.contains(tmp)) {
                    intersections.add(tmp);
                }
            }
        }

        return intersections;
    }

    /**
     * Get all interectionLines by applying a brute force algorithm.
     * @param lines all lines you want to check, in no order
     * @return a list that contains all pairs of intersecting lines
     */
    public static Set<LineSegment[]> getAllIntersectingLinesByBruteForce(
            LineSegment[] lines) {
        Set<LineSegment[]> intersections = new HashSet<LineSegment[]>();

        for (int i = 0; i < lines.length; i++) {
            for (int j = i + 1; j < lines.length; j++) {
                if (doLinesIntersect(lines[i], lines[j])) {
                    LineSegment[] tmp = new LineSegment[2];
                    tmp[0] = lines[i];
                    tmp[1] = lines[j];
                    intersections.add(tmp);
                }
            }
        }

        return intersections;
    }

    public static boolean isLeftBend(Point i, Point j, Point k) {
        Point pi = new Point(i.x, i.y);
        Point pj = new Point(j.x, j.y);
        Point pk = new Point(k.x, k.y);

        // Move pi to (0,0) and pj and pk with it
        pj.x -= pi.x;
        pk.x -= pi.x;
        pj.y -= pi.y;
        pk.y -= pi.y;
        LineSegment s = new LineSegment(pi, pj);

        // Move pj to (0,0) and pk with it
        pk.x -= pj.x;
        pk.y -= pj.y;

        return !(isPointRightOfLine(s, pk) || isPointOnLine(s, pk));
    }

    /**
     * Calculate the convex hull of points with Graham Scan
     * @param points a list of points in any order
     * @return the convex hull (can be rotated)
     */
    public static List<Point> getConvexHull(List<Point> points) {
        // TODO: Doesn't work by now
        List<Point> l = new ArrayList<Point>();

        // find lowest point. If there is more than one lowest point
        // take the one that is left
        Point pLow = new Point(0, Double.POSITIVE_INFINITY);
        for (Point point : points) {
            if (point.y < pLow.y || (point.y == pLow.y && point.x < pLow.x)) {
                pLow = point;
            }
        }

        // Order all other points by angle
        class PointComparator implements Comparator<Point> {
            Point pLow;

            public PointComparator(Point pLow) {
                this.pLow = pLow;
            }

            private double getAngle(Point p) {
                // TODO: This is buggy
                double deltaX = pLow.x - p.x;
                double deltaY = pLow.y - p.y;
                if (deltaX < EPSILON) {
                    return 0;
                } else {
                    return deltaY / deltaX;
                }
            }

            @Override
            public int compare(Point o1, Point o2) {
                double a1 = getAngle(o1);
                double a2 = getAngle(o2);
                if (Math.abs(a1 - a2) < EPSILON) {
                    return 0;
                } else {
                    return a1 < a2 ? -1 : 1;
                }
            }
        }

        PointComparator comparator = new PointComparator(pLow);

        Collections.sort(points, comparator);

        // go through all points
        for (Point tmp : points) {
            boolean loop = true;

            while (loop) {
                if (l.size() < 3) {
                    l.add(tmp);
                    loop = false;
                } else if (!isLeftBend(l.get(l.size() - 2),
                        l.get(l.size() - 1), tmp)) {
                    l.add(tmp);
                    loop = false;
                } else {
                    l.remove(l.size() - 1);
                }
            }
        }

        return l;
    }


    //Added from:http://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
    //--------------------------------------

    //Compute the dot product AB . AC
    private double DotProduct(double[] pointA, double[] pointB, double[] pointC)
    {
        double[] AB = new double[2];
        double[] BC = new double[2];
        AB[0] = pointB[0] - pointA[0];
        AB[1] = pointB[1] - pointA[1];
        BC[0] = pointC[0] - pointB[0];
        BC[1] = pointC[1] - pointB[1];
        double dot = AB[0] * BC[0] + AB[1] * BC[1];

        return dot;
    }

    //Compute the cross product AB x AC
    private double CrossProduct(double[] pointA, double[] pointB, double[] pointC)
    {
        double[] AB = new double[2];
        double[] AC = new double[2];
        AB[0] = pointB[0] - pointA[0];
        AB[1] = pointB[1] - pointA[1];
        AC[0] = pointC[0] - pointA[0];
        AC[1] = pointC[1] - pointA[1];
        double cross = AB[0] * AC[1] - AB[1] * AC[0];

        return cross;
    }

    //Compute the distance from A to B
    private double Distance(double[] pointA, double[] pointB)
    {
        double d1 = pointA[0] - pointB[0];
        double d2 = pointA[1] - pointB[1];

        return Math.sqrt(d1 * d1 + d2 * d2);
    }

    public double segmentToPointDistance2D(LineSegment segment, Point point) {
        return lineToPointDistance2D(new double[]{segment.first.x, segment.first.y},
                new double[]{segment.second.x, segment.second.y},
                                    new double[]{point.x, point.y}, true);
    }

    //Compute the distance from AB to C
    //if isSegment is true, AB is a segment, not a line.
    public double lineToPointDistance2D(double[] pointA, double[] pointB, double[] pointC,
                                 boolean isSegment)
    {
        double dist = CrossProduct(pointA, pointB, pointC) / Distance(pointA, pointB);
        if (isSegment)
        {
            double dot1 = DotProduct(pointA, pointB, pointC);
            if (dot1 > 0)
                return Distance(pointB, pointC);

            double dot2 = DotProduct(pointB, pointA, pointC);
            if (dot2 > 0)
                return Distance(pointA, pointC);
        }
        return Math.abs(dist);
    }

}
