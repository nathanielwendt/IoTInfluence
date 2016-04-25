package nathanielwendt.mpc.ut.edu.iotinfluence;

import junit.framework.Assert;

import org.junit.Test;

import nathanielwendt.mpc.ut.edu.iotinfluence.util.Geometry;

/**
 * Created by nathanielwendt on 4/19/16.
 */
public class IntersectionTest {
    @Test
    public void testVertical() {
        Geometry.LineSegment vertical = new Geometry.LineSegment(1,0,1,5);

        Assert.assertTrue(Geometry.doLinesIntersect(vertical, new Geometry.LineSegment(1, 0, 1, 5)));
        Assert.assertTrue(Geometry.doLinesIntersect(vertical, new Geometry.LineSegment(1, 2, 1, 7)));
        Assert.assertTrue(Geometry.doLinesIntersect(vertical, new Geometry.LineSegment(1, -1, 1, 3)));
        Assert.assertTrue(Geometry.doLinesIntersect(vertical, new Geometry.LineSegment(1, 2, 1, 3)));
        Assert.assertTrue(Geometry.doLinesIntersect(vertical, new Geometry.LineSegment(1, -1, 1, 7)));

        Assert.assertFalse(Geometry.doLinesIntersect(vertical, new Geometry.LineSegment(1, -2, 1, -1)));
        Assert.assertFalse(Geometry.doLinesIntersect(vertical, new Geometry.LineSegment(1, 7, 1, 9)));
    }

    @Test
    public void testHorizontal() {
        Geometry.LineSegment horizontal = new Geometry.LineSegment(0,1,5,1);

        Assert.assertTrue(Geometry.doLinesIntersect(horizontal, new Geometry.LineSegment(0, 1, 5, 1)));
        Assert.assertTrue(Geometry.doLinesIntersect(horizontal, new Geometry.LineSegment(2, 1, 7, 1)));
        Assert.assertTrue(Geometry.doLinesIntersect(horizontal, new Geometry.LineSegment(-1, 1, 3, 1)));
        Assert.assertTrue(Geometry.doLinesIntersect(horizontal, new Geometry.LineSegment(2, 1, 3, 1)));
        Assert.assertTrue(Geometry.doLinesIntersect(horizontal, new Geometry.LineSegment(-1, 1, 7, 1)));

        Assert.assertFalse(Geometry.doLinesIntersect(horizontal, new Geometry.LineSegment(-2, 1, -1, 1)));
        Assert.assertFalse(Geometry.doLinesIntersect(horizontal, new Geometry.LineSegment(7, 1, 9, 1)));
    }

    @Test
    public void testDiagonals() {
        Geometry.LineSegment vertical = new Geometry.LineSegment(1,0,1,5);
        Assert.assertTrue(Geometry.doLinesIntersect(vertical, new Geometry.LineSegment(0, 2, 2, 0)));
        Assert.assertTrue(Geometry.doLinesIntersect(vertical, new Geometry.LineSegment(2, 0, 0, 2)));
        Assert.assertTrue(Geometry.doLinesIntersect(vertical, new Geometry.LineSegment(0, 0, 2, 2)));
        Assert.assertTrue(Geometry.doLinesIntersect(vertical, new Geometry.LineSegment(0, 3, 3, 3)));
    }

    @Test
    public void testSpecial() {

        System.out.println(Geometry.doLinesIntersect(new Geometry.LineSegment(10, 8, 10, 14), new Geometry.LineSegment(9,11,12,11)));
        System.out.println(Geometry.doLinesIntersect(new Geometry.LineSegment(10, 8, 10, 14), new Geometry.LineSegment(12,11,9,11)));
        System.out.println(Geometry.doLinesIntersect(new Geometry.LineSegment(10, 8, 10, 14), new Geometry.LineSegment(12,11,9,11)));
        System.out.println(Geometry.doLinesIntersect(new Geometry.LineSegment(12,11,9,11), new Geometry.LineSegment(10, 8, 10, 14)));

    }
}
