package fer.zesoi.bodyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class BodyMeasurementsActivity extends AppCompatActivity {

    public static final String[] MEASURES_TEMPLATES  = {
            "1. Head circumference %.2f cm",
            "2. Neck circumference %.2f cm",
            "3. Shoulder-to-crotch %.2f cm",
            "4. Chest circumference %.2f cm",
            "5. Waist circumference %.2f cm",
            "6. Hip circumference %.2f cm",
            "7. Wrist circumference %.2f cm",
            "8. Bicep circumference %.2f cm",
            "9. Forearm circumference %.2f cm",
            "10. Arm length %.2f cm",
            "11. Inside-leg length %.2f cm",
            "12. Thigh circumference %.2f cm",
            "13. Calf circumference %.2f cm",
            "14. Ankle circumference %.2f cm",
            "15. Shoulder breadth %.2f cm"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_measurements);
        float[] bodyMeasures = getIntent().getFloatArrayExtra("body_measurements");

        EditText[] textMeasureElements = new EditText[15];
        for (int i = 0; i < 15; i++) {
            String elementStringId = String.format("textMeasure%d", i + 1);
            int elementId = getResources().getIdentifier(elementStringId, "id", getPackageName());
            textMeasureElements[i] = (EditText) findViewById(elementId);
            textMeasureElements[i].setText(String.format(MEASURES_TEMPLATES[i], bodyMeasures[i]));
        }
    }
}