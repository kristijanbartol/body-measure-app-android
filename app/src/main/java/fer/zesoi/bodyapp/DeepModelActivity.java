package fer.zesoi.bodyapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeepModelActivity extends AppCompatActivity implements Runnable {

    private Bitmap personBitmap = null;
    private Button personCaptureButton = null;
    private Button runButton = null;
    private Button showMeasurementsButton = null;
    private ProgressBar progressBar;
    private Module mModule = null;
    private float mImgScaleX, mImgScaleY, mIvScaleX, mIvScaleY, mStartX, mStartY;

    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_model);

        ActivityResultLauncher<Intent> personCaptureActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        personBitmap = (Bitmap) data.getExtras().get("data");

                        Matrix matrix = new Matrix();
                        matrix.postRotate(90.0f);
                        personBitmap = Bitmap.createBitmap(personBitmap,
                                0,
                                0,
                                personBitmap.getWidth(),
                                personBitmap.getHeight(),
                                matrix,
                                true);
                    }
                });

        personCaptureButton = findViewById(R.id.captureButton);
        runButton = findViewById(R.id.runButton);
        showMeasurementsButton = findViewById(R.id.bodyMeasurementsButton);

        personCaptureButton.setOnClickListener(view -> {
            Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            personCaptureActivityResultLauncher.launch(takePicture);

            runButton.setEnabled(true);
            showMeasurementsButton.setEnabled(true);
        });

        runButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                progressBar.setVisibility(ProgressBar.VISIBLE);
                runButton.setText(getString(R.string.running_model));

                Thread thread = new Thread(DeepModelActivity.this);
                thread.start();
            }
        });

        try {
            mModule = LiteModuleLoader.load(DeepModelActivity.assetFilePath(getApplicationContext(), "deeplabv3_scripted_optimized.ptl"));
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("classes.txt")));
            String line;
            List<String> classes = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                classes.add(line);
            }
            PrePostProcessor.mClasses = new String[classes.size()];
            classes.toArray(PrePostProcessor.mClasses);
        } catch (IOException e) {
            Log.e("Object Detection", "Error reading assets", e);
            finish();
        }

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

    private void switchActivitiesWithExtraFloat(float[] bodyMeasurements) {
        Intent intent = new Intent(this, BodyMeasurementsActivity.class);
        intent.putExtra("body_measurements", bodyMeasurements);
        startActivity(intent);
    }

    @Override
    public void run() {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(personBitmap, PrePostProcessor.mInputWidth, PrePostProcessor.mInputHeight, true);
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap, PrePostProcessor.NO_MEAN_RGB, PrePostProcessor.NO_STD_RGB);
        Map<String, IValue> outTensors = mModule.forward(IValue.from(inputTensor)).toDictStringKey();
        final Tensor outputTensor = outTensors.get("out").toTensor();
        final float[] scores = outputTensor.getDataAsFloatArray();

        runOnUiThread(() -> {
            runButton.setText(getString(R.string.run_model));
            progressBar.setVisibility(ProgressBar.INVISIBLE);
        });
    }
}