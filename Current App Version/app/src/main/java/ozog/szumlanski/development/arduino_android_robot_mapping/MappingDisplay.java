package ozog.szumlanski.development.arduino_android_robot_mapping;

import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.*;

public class MappingDisplay extends AppCompatActivity {

    ImageView arduino;
    Timer timer;
    Handler handler;

    Point nextPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapping_display);

        arduino = findViewById(R.id.arduino);

        ArduinoDataStack.uploadFakeValues();

        timer = new Timer();
        handler = new Handler();

        // Time update for timers and messages
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        positionUpdate();
                    }
                });
            }
        }, 0, 1000);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void positionUpdate() {

        float x = arduino.getX();
        float y = arduino.getY();

        // Current position (angle).
        float currentRotation = arduino.getRotation();

        float newX;
        float newY;
        float newRotation;

        nextPosition = ArduinoDataStack.getRecord();

        // Rotation
        if (nextPosition != null && nextPosition.x == 0) {
            newRotation = currentRotation + nextPosition.y;
            arduino.setRotation(newRotation);
        }
        // Straight movement
        else if (nextPosition != null && nextPosition.y == 0) {
            newX = x + calculateArduinoCoordsToCartesianCoords(nextPosition.x, (int)currentRotation).x;
            newY = y - calculateArduinoCoordsToCartesianCoords(nextPosition.x, (int)currentRotation).y;
            arduino.setX(newX);
            arduino.setY(newY);
        }

        Log.d("New X: ", Float.toString(arduino.getX() + 50));
        Log.d("New Y: ", Float.toString(arduino.getY() + 50));
        Log.d("BREAK: ", "---------");
    }

    public static Point calculateArduinoCoordsToCartesianCoords(int distanceDelta, int currentRotation) {

        double x, y;

        x = Math.sin(Math.toRadians(currentRotation)) * distanceDelta;
        y = Math.cos(Math.toRadians(currentRotation)) * distanceDelta;

        Point cartesianCoords = new Point((int)x, (int)y);
        return cartesianCoords;
    }
}
