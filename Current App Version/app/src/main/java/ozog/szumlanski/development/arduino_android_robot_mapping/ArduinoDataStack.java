package ozog.szumlanski.development.arduino_android_robot_mapping;

import android.graphics.Point;
import java.util.LinkedList;
import java.util.List;

public class ArduinoDataStack {

    private static List<Point> dataStack;

    static {
        dataStack = new LinkedList<>();
    }

    public static void addRecord(int distanceDelta, int rotationDelta) {
        dataStack.add(new Point(distanceDelta, rotationDelta));
    }

    public static Point getRecord() {

        Point nextPosition = null;

        if (!dataStack.isEmpty()) {
            nextPosition = dataStack.get(0);
            dataStack.remove(0);
        }

        return nextPosition;
    }

    public static void uploadFakeValues() {

        dataStack.add(new Point(200, 0));
        dataStack.add(new Point(200, 0));
        dataStack.add(new Point(0, 90));
        dataStack.add(new Point(200, 0));
        dataStack.add(new Point(0, 90));
        dataStack.add(new Point(200, 0));
        dataStack.add(new Point(0, 90));
        dataStack.add(new Point(200, 0));
        dataStack.add(new Point(0, 90));
    }
}
