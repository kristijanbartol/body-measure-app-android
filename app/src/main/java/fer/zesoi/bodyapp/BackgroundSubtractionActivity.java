package fer.zesoi.bodyapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BackgroundSubtractionActivity extends AppCompatActivity {

    private Bitmap backgroundBitmap = null;
    private Bitmap foregroundBitmap = null;
    private Bitmap personSilhouette = null;
    Button backgroundCaptureButton;
    Button foregroundCaptureButton;
    Button runButton;
    Button showSilhouetteButton;
    Button showMeasurementsButton;

    BackgroundSubtractor backgroundSubtractor = new BackgroundSubtractor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        setContentView(R.layout.activity_background_subtraction);

        ActivityResultLauncher<Intent> backgroundCaptureActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        backgroundBitmap = (Bitmap) data.getExtras().get("data");

                        Matrix matrix = new Matrix();
                        matrix.postRotate(90.0f);
                        backgroundBitmap = Bitmap.createBitmap(backgroundBitmap,
                                0,
                                0,
                                backgroundBitmap.getWidth(),
                                backgroundBitmap.getHeight(),
                                matrix,
                                true);

                        backgroundSubtractor.initBackground(backgroundBitmap);
                    }
                });

        ActivityResultLauncher<Intent> foregroundCaptureActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        foregroundBitmap = (Bitmap) data.getExtras().get("data");

                        Matrix matrix = new Matrix();
                        matrix.postRotate(90.0f);
                        foregroundBitmap = Bitmap.createBitmap(foregroundBitmap,
                                0,
                                0,
                                foregroundBitmap.getWidth(),
                                foregroundBitmap.getHeight(),
                                matrix,
                                true);
                    }

                    if (backgroundBitmap != null) {
                        //runButton.setBackgroundTintList(contextInstance.getResources().getColorStateList(R.color.your_xml_name));
                        runButton.setEnabled(true);
                    }
                });

        backgroundCaptureButton = findViewById(R.id.backgroundCaptureButton);
        backgroundCaptureButton.setOnClickListener(view -> {
            Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            backgroundCaptureActivityResultLauncher.launch(takePicture);
        });

        foregroundCaptureButton = findViewById(R.id.foregroundCaptureButton);
        foregroundCaptureButton.setOnClickListener(view -> {
            Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            foregroundCaptureActivityResultLauncher.launch(takePicture);
        });

        runButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    personSilhouette = backgroundSubtractor.subtract(foregroundBitmap);
                    showSilhouetteButton.setEnabled(true);
                    showMeasurementsButton.setEnabled(true);
                }
            }
        );

        showSilhouetteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchActivitiesWithExtraBitmap(personSilhouette);
                }
            }
        );

        showMeasurementsButton.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      float[] bodyMeasurements = new float[15];
                      for (int i = 0; i < 15; i++) {
                          bodyMeasurements[i] = 0;
                      }

                      switchActivitiesWithExtraFloat(bodyMeasurements);
                  }
              }
        );
    }

    private void switchActivitiesWithExtraBitmap(Bitmap silhouetteBitmap) {
        Intent intent = new Intent(this, SilhouetteViewActivity.class);
        intent.putExtra("bitmap", silhouetteBitmap);
        startActivity(intent);
    }

    private void switchActivitiesWithExtraFloat(float[] bodyMeasurements) {
        Intent intent = new Intent(this, BodyMeasurementsActivity.class);
        intent.putExtra("body_measurements", bodyMeasurements);
        startActivity(intent);
    }
}