package ozog.szumlanski.development.arduino_android_robot_mapping;

public class Spot {

    public float drivenDistance;
    public float currentRotation;
    public float sensor01Data; // Front
    public float sensor02Data; // Right
    public float sensor03Data; // Back
    public float sensor04Data; // Left

    Spot(float drivenDistance, float currentRotation, float sensor01Data, float sensor02Data, float sensor03Data, float sensor04Data) {

        this.drivenDistance = drivenDistance; //distance driven from the previous point
        this.currentRotation = currentRotation; //rotation from the starting position
        this.sensor01Data = sensor01Data; //ultrasonic sensor data
        this.sensor02Data = sensor02Data;
        this.sensor03Data = sensor03Data;
        this.sensor04Data = sensor04Data;
    }
}
