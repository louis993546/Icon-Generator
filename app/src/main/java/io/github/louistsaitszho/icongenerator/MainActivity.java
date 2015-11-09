package io.github.louistsaitszho.icongenerator;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ImageView image;
    int currentR = 255;
    int currentG = 0;
    int currentB = 0;
    String currentText = "A";

    private static Bitmap eraseBG(Bitmap src, int color) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap b = src.copy(Bitmap.Config.ARGB_8888, true);
        b.setHasAlpha(true);

        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < width * height; i++) {
            if (pixels[i] == color) {
                pixels[i] = 0;
            }
        }

        b.setPixels(pixels, 0, width, 0, 0, width, height);

        return b;
    }

    public static int randInt() {
        Random rand = new Random();
        return rand.nextInt(255);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentR = randInt();
        currentG = randInt();
        currentB = randInt();

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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
            } else {
                saveImage();
            }
            return true;
        } else if (id == R.id.action_about) {
            new LibsBuilder()
                    .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                    .withAutoDetect(true)
                    .withAboutVersionShown(true)
                    .withAboutIconShown(true)
                    .withVersionShown(true)
                    .withLibraries("AndroidMaterialColorPickerDialog", "TextDrawable")
                    .withActivityTitle("About")
                    .withAboutAppName("Icon Generator")
                    .withAboutDescription("This app allows you to generate a simple text-based icon in seconds")
                    .withSortEnabled(true)
                    .start(this);
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
        CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);
        String message;
        if (image == null) {
            image = (ImageView) findViewById(R.id.imageView);
        }
        image.setDrawingCacheEnabled(true);
        image.destroyDrawingCache();
        image.buildDrawingCache();
        Bitmap bitmap = eraseBG(image.getDrawingCache(), -16777216);

        try {
            String path = Environment.getExternalStorageDirectory().toString();
            File filename = new File(path, System.currentTimeMillis() + ".png");
            OutputStream fOut = new FileOutputStream(filename);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                message = "Icon saved";
            } else
                message = "Something is wrong";
        } catch (IOException e) {
            e.printStackTrace();
            message = "Something is wrong";
        } catch (NullPointerException e) {
            e.printStackTrace();
            message = "Something is wrong: Null pointer exception";
        }

        Snackbar.make(cl, message, Snackbar.LENGTH_LONG).show();
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
            Snackbar.make(view, "2 letter MAX! Idiot!?", Snackbar.LENGTH_LONG).show();
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
