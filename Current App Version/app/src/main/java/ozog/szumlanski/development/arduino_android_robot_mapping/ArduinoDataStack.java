package ozog.szumlanski.development.arduino_android_robot_mapping;

import java.util.LinkedList;
import java.util.List;

public class ArduinoDataStack {

    private static List<Spot> dataStack;

    static {
        dataStack = new LinkedList<>();
    }

    public static void addRecord( float drivenDistance, float currentRotation, float sensor01Data ) {
        dataStack.add(new Spot(drivenDistance, currentRotation, sensor01Data));
    }

    public static Spot getRecord() {

        Spot nextPosition = null;

        if (!dataStack.isEmpty()) {
            nextPosition = dataStack.get(0);
            dataStack.remove(0);
        }

        return nextPosition;
    }

    public static void uploadFakeValues() {

        dataStack.add(new Spot(0, 0, 200 ));
        dataStack.add(new Spot(0, 45, 200 ));
        dataStack.add(new Spot(0, 90, 200 ));
        dataStack.add(new Spot(0, 135, 200 ));
        dataStack.add(new Spot(0, 180, 200 ));
        dataStack.add(new Spot(100, 180, 200 ));
        dataStack.add(new Spot(0, 135, 200 ));
        dataStack.add(new Spot(0, 90, 200 ));
    }
}
