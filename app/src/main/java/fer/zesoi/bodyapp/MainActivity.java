package fer.zesoi.bodyapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button switchToV1Activity;
    Button selectV2Activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchToV1Activity = findViewById(R.id.v1button);
        switchToV1Activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities(0);
            }
        });

        selectV2Activity = findViewById(R.id.v2button);
        selectV2Activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //someActivityResultLauncher.launch(takePicture);
                switchActivities(1);
            }
        });
    }

    private void switchActivities(Integer selection) {
        Intent switchActivityIntent = null;
        if (selection == 0) {
            switchActivityIntent = new Intent(this, BackgroundSubtractionActivity.class);
        }
        else {
            switchActivityIntent = new Intent(this, DeepModelActivity.class);
        }
        startActivity(switchActivityIntent);
    }
}
