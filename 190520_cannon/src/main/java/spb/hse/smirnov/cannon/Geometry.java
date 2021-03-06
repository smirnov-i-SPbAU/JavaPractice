package spb.hse.smirnov.cannon;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.jetbrains.annotations.NotNull;

/**
 * Class to manipulate with segments, lines and points
 */
// I tried to find some library in net, but the only one I found was for maven only
public class Geometry {
    /** Checks if circle intersects segment */
    public static boolean circleIntersectsSegment(@NotNull Circle circle, @NotNull Line segment) {
       return distanceFromPointToSegment(new Point2D(circle.getCenterX(), circle.getCenterY()), segment) <=
               circle.getRadius();
    }

    /** Checks if two circles intersect */
    public static boolean circleIntersectsCircle(@NotNull Circle circle1, @NotNull Circle circle2) {
        var point1 = new Point2D(circle1.getCenterX(), circle1.getCenterY());
        var point2 = new Point2D(circle2.getCenterX(), circle2.getCenterY());
        return point1.distance(point2) <= circle1.getRadius() + circle2.getRadius();
    }

    /** Returns angle of vector that is perpendicular to given segment */
    public static double getNormalAngle(@NotNull Line segment) {
        var pointingVector = getNormalizedPointingVector(segment);
        return Math.atan2(pointingVector.getX(), pointingVector.getY());
    }

    /** Rotates line on given angle around its center */
    public static Line rotateLine(@NotNull Line line, double angle) {
        var middlePoint = new Point2D((line.getStartX() + line.getEndX()) / 2,
                                      (line.getStartY() + line.getEndY()) / 2);
        double length = length(line);
        var pointingVector = getNormalizedPointingVector(line);
        var rotatedVector = new Point2D(
                pointingVector.getX() * Math.sin(angle) - pointingVector.getY() * Math.cos(angle),
                pointingVector.getX() * Math.cos(angle) - pointingVector.getY() * Math.sin(angle));
        var sizedVector = new Point2D(rotatedVector.getX() * length / 2, rotatedVector.getY() * length / 2);
        var rotatedLine = new Line();
        rotatedLine.setStartX(middlePoint.getX() - sizedVector.getX());
        rotatedLine.setStartY(middlePoint.getY() - sizedVector.getY());
        rotatedLine.setEndX(middlePoint.getX() + sizedVector.getX());
        rotatedLine.setEndY(middlePoint.getY() + sizedVector.getY());
        return rotatedLine;
    }

    /**
     * Returns distance between point and segment
     * Implemented with ternary search because I don't want think about formulas...
     */
    private static double distanceFromPointToSegment(@NotNull Point2D point, @NotNull Line segment) {
        final double epsilon = 1e-3;
        var pointStart = new Point2D(segment.getStartX(), segment.getStartY());
        double length = length(segment);
        var pointingVector = getNormalizedPointingVector(segment);
        double left = 0;
        double right = length;
        while (right - left > epsilon) {
            double middleLeft = left + (right - left) / 3;
            double middleRight = middleLeft + (right - left) / 3;
            if (point.distance(makePoint(pointStart, pointingVector, middleLeft)) <
                    point.distance(makePoint(pointStart, pointingVector, middleRight))) {
                right = middleRight;
            } else {
                left = middleLeft;
            }
        }
        return point.distance(makePoint(pointStart, pointingVector, left));
    }

    /** Normalizes given segment */
    private static Point2D getNormalizedPointingVector(@NotNull Line segment) {
        double length = length(segment);
        return new Point2D((segment.getEndX() - segment.getStartX()) / length,
                           (segment.getEndY() - segment.getStartY()) / length);
    }

    /** Returns point moved on {@code distance} from {@code pointStart} along {@code vector} */
    private static Point2D makePoint(@NotNull Point2D pointStart, @NotNull Point2D vector, double distance) {
        return new Point2D(pointStart.getX() + vector.getX() * distance,
                           pointStart.getY() + vector.getY() * distance);
    }

    /** Returns segment's length */
    private static double length(@NotNull Line segment) {
        return Math.hypot(segment.getStartX() - segment.getEndX(),
                          segment.getStartY() - segment.getEndY());
    }
}
