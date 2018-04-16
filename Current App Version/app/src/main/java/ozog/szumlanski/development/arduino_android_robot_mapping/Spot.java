package ozog.szumlanski.development.arduino_android_robot_mapping;

public class Spot {

    public float drivenDistance;
    public float currentRotation;
    public float sensor01Data;

    Spot(float drivenDistance, float currentRotation, float sensor01Data) {

        this.drivenDistance = drivenDistance; //distance driven from the previous point
        this.currentRotation = currentRotation; //rotation from the starting position
        this.sensor01Data = sensor01Data; //ultrasonic sensor data
    }
}
