package fer.zesoi.bodyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SilhouetteViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_silhouette_view);

        Bitmap silhouetteBitmap = (Bitmap) getIntent().getExtras().get("bitmap");

        ImageView mImageView = findViewById(R.id.silhouetteView);
        mImageView.setImageBitmap(silhouetteBitmap);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });
    }

    private void switchActivities() {
        Intent switchActivityIntent = new Intent(this, BackgroundSubtractionActivity.class);
        startActivity(switchActivityIntent);
    }
}