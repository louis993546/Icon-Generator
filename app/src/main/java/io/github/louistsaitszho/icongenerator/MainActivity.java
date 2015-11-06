package io.github.louistsaitszho.icongenerator;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

public class MainActivity extends AppCompatActivity {
    ImageView image;
    int currentR = 255;
    int currentG = 0;
    int currentB = 0;
    String currentText = "A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        image = (ImageView) findViewById(R.id.imageView);
        int color = Color.rgb(currentR, currentG, currentB);
        TextDrawable drawable = TextDrawable.builder().buildRound(currentText, color);
        image.setImageDrawable(drawable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            //TODO save image
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
            } else {
                saveImage();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case (4):
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImage();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void saveImage() {
        if (image == null) {
            image = (ImageView) findViewById(R.id.imageView);
        }
        image.setDrawingCacheEnabled(true);
        image.destroyDrawingCache();
        image.buildDrawingCache();
        Bitmap bitmap = image.getDrawingCache();
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, System.currentTimeMillis() + ".png" , "TextDrawable");
        CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);
        Snackbar.make(cl, "Icon Saved", Snackbar.LENGTH_LONG).show();
    }

    public void changeText(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if (image == null) {
            image = (ImageView) findViewById(R.id.imageView);
        }
        EditText et = (EditText) findViewById(R.id.etText);
        Editable etEditable = et.getText();
        if (etEditable.length()<=2) {
            currentText = etEditable.toString();
            TextDrawable drawable = TextDrawable.builder().buildRound(currentText, Color.rgb(currentR, currentG, currentB));
            image.setImageDrawable(drawable);
        } else {
            Snackbar.make(view, "2 letter MAX! Don't you get it!?", Snackbar.LENGTH_LONG).show();
        }
    }

    public void colorPicker(View view) {
        //TODO color picker
        final ColorPicker cp = new ColorPicker(MainActivity.this, currentR, currentG, currentB);
        cp.show();
        Button okColor = (Button)cp.findViewById(R.id.okColorButton);
        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentR = cp.getRed();
                currentG = cp.getGreen();
                currentB = cp.getBlue();
                if (image == null) {
                    image = (ImageView) findViewById(R.id.imageView);
                }
                TextDrawable drawable = TextDrawable.builder().buildRound(currentText, Color.rgb(currentR, currentG, currentB));
                image.setImageDrawable(drawable);
                cp.dismiss();
            }
        });
    }
}
