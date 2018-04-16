package ozog.szumlanski.development.arduino_android_robot_mapping;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.*;

public class MappingDisplay extends AppCompatActivity {

    ImageView arduino;
    Timer timer;
    Handler handler;
    RelativeLayout rl;

    Spot nextPosition;
    static int acc;

    float currentX, currentY, currentRotation;
    float currentMiddleX, currentMiddleY;
    float newX, newY, newRotation;
    float pointerX, pointerY;

    static BufferedReader reader;
    static String receivedDataLine;

    Thread readingThread = new Thread(){
        public void run(){

            beginListenForData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapping_display);

        arduino = findViewById(R.id.arduino);
        rl = findViewById(R.id.rl);

        //Starting a thread to read data and store to stack. Uncomment to work with arduino
        //readingThread.start();

        //fake values for testing. Commend this out to work with arduino
        ArduinoDataStack.uploadFakeValues();

        acc = 0;

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

        try{
            reader.close();
        }
        catch(Exception e){

            Log.e("Exception: ", e.toString());
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void positionUpdate() {

        currentX = arduino.getX();
        currentY = arduino.getY();

        currentMiddleX = arduino.getX() + 100;
        currentMiddleY = arduino.getY() + 100;

        Log.d("Current X: ", Float.toString(currentX));
        Log.d("Current Y: ", Float.toString(currentY));

        currentRotation = arduino.getRotation();

        nextPosition = ArduinoDataStack.getRecord();

        // Keep that order - straight movement/rotation/sensorReading.

        // Straight movement
        if (nextPosition != null && nextPosition.drivenDistance != 0) {
            newX = currentX + calculateArduinoCoordsToCartesianCoords(nextPosition.drivenDistance, currentRotation).x;
            newY = currentY - calculateArduinoCoordsToCartesianCoords(nextPosition.drivenDistance, currentRotation).y;
            arduino.setX(newX);
            arduino.setY(newY);
            currentX = newX;
            currentY = newY;
        }

        // Rotation
        if (nextPosition != null) {
            newRotation = nextPosition.currentRotation;

            arduino.setRotation(newRotation);
            currentRotation = newRotation;
        }

        // Sensor 01 Data Visualization
        if (nextPosition != null && nextPosition.sensor01Data != 0) {
            pointerX = currentMiddleX + calculateArduinoCoordsToCartesianCoords(nextPosition.sensor01Data, currentRotation).x;
            pointerY = currentMiddleY - calculateArduinoCoordsToCartesianCoords(nextPosition.sensor01Data, currentRotation).y;

            Log.d("CurrentX: ", Float.toString(currentX));
            Log.d("CurrentY: ", Float.toString(currentY));

            Log.d("PointerX: ", Float.toString(pointerX));
            Log.d("PointerY: ", Float.toString(pointerY));

            // Pointer image
            Drawable newPoint;
            ImageView iv = new ImageView(getApplicationContext());
            try {
                InputStream stream = getAssets().open("pointer.png");
                newPoint = Drawable.createFromStream(stream, null);
                iv.setImageDrawable(newPoint);

                iv.setScaleX(10);
                iv.setScaleY(10);

                iv.setX(pointerX);
                iv.setY(pointerY);
                iv.setRotation(currentRotation);

                rl.addView(iv);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Point calculateArduinoCoordsToCartesianCoords(float distanceDelta, float currentRotation) {

        float x, y;

        x = (float) Math.sin(Math.toRadians(currentRotation)) * distanceDelta;
        y = (float) Math.cos(Math.toRadians(currentRotation)) * distanceDelta;

        Point cartesianCoords = new Point((int) x, (int) y);
        return cartesianCoords;
    }

    static void beginListenForData() {

        Log.d("Data Listening Started", "");

        reader = new BufferedReader(new InputStreamReader(MainActivity.mmInputStream));

        float drivenDistance = 0;
        float currentRotation = 0;
        float sensor01Data = 0;

        try {
            // Keep looping to listen for received messages
            while ((receivedDataLine = reader.readLine()) != null) {
                System.out.println("Message: " + receivedDataLine);

                acc++;

                // Distance
                if (acc % 3 == 1)
                    drivenDistance = Float.parseFloat(receivedDataLine);

                // Rotation
                if (acc % 3 == 2)
                    currentRotation = Float.parseFloat(receivedDataLine);

                // Sensor 01 Data
                if (acc % 3 == 0) {
                    sensor01Data = Float.parseFloat(receivedDataLine);
                    // Send it to the stack
                    ArduinoDataStack.addRecord(drivenDistance, currentRotation, sensor01Data);
                }
            }
        }
        catch(Exception e){

            Log.e("Exception: ", e.toString());
        }
    }
}



