package com.phunware.parkassistdemo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.phunware.parkassist.ParkAssistSDK;
import com.phunware.parkassist.networking.Callback;

/**
 * Created by mrand on 3/15/16.
 */
public class DetailActivity extends AppCompatActivity {
    private String mUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUuid = getIntent().getStringExtra("UUID");
        String mapName = getIntent().getStringExtra("map");
        final int x = getIntent().getIntExtra("xCoord", 0);
        final int y = getIntent().getIntExtra("yCoord", 0);
        setContentView(R.layout.activity_detail);
        TextView uuid = (TextView)findViewById(R.id.detail_uuid);
        uuid.setText("UUID: " + mUuid);
        TextView mapNameView = (TextView)findViewById(R.id.detail_map);
        mapNameView.setText("Map: " + mapName);
        final ImageView plateImage = (ImageView)findViewById(R.id.thumb_image);
        final ImageView mapImage = (ImageView)findViewById(R.id.map_image);

        ParkAssistSDK parkAssistSDK = StaticSDK.getInstance(getString(R.string.app_secret), getString(R.string.site_slug));

        Callback<Bitmap> thumbCallback = new Callback<Bitmap>() {
            @Override
            public void onSuccess(Bitmap data) {
                plateImage.setImageBitmap(data);
            }

            @Override
            public void onFailed(Throwable e) {
                Toast.makeText(DetailActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        Callback<Bitmap> mapCallback = new Callback<Bitmap>() {
            @Override
            public void onSuccess(Bitmap data) {
                Paint paint = new Paint();
                paint.setColor(Color.BLUE);
                paint.setStrokeWidth(5);
                paint.setAntiAlias(true);
                Bitmap canvasBitmap = Bitmap.createBitmap(data.getWidth(), data.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(canvasBitmap);
                canvas.drawBitmap(data, 0, 0, paint);
                canvas.drawCircle((float)x, (float)y, 10, paint);

                mapImage.setImageBitmap(canvasBitmap);
            }

            @Override
            public void onFailed(Throwable e) {
                Toast.makeText(DetailActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        parkAssistSDK.getVehicleThumbnail(mUuid, thumbCallback);
        parkAssistSDK.getMapImage(mapName, mapCallback);
    }
}
