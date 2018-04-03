package ozog.szumlanski.development.arduino_android_robot_mapping;

public class Spot {

    public float drivenDistance;
    public float currentRotation;
    public float sensor01Data;

    Spot(float drivenDistance, float currentRotation, float sensor01Data) {

        this.drivenDistance = drivenDistance;
        this.currentRotation = currentRotation;
        this.sensor01Data = sensor01Data;
    }
}
