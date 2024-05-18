package com.example.praktikum2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private final int REQ_CODE_PERM_STORAGE = 1;
    private final int REQ_CODE_BUKA_GALLERY = 1;
    private final int REQ_CODE_BUKA_KAMERA = 2;
    private DataFileGambar dataFileGambar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Method untuk membuka gallery
    private void bukaGallery(){
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Intent intent = new Intent(Intent.ACTION_PICK, uri);
        startActivityForResult(intent, REQ_CODE_BUKA_GALLERY);
    }

    // Method untuk menangani klik tombol buka gallery
    public void bukaGalleryClick(View view) {
        int permissionReadStorage;
        if (android.os.Build.VERSION.SDK_INT > 30)
            permissionReadStorage = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_IMAGES);
        else
            permissionReadStorage = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionReadStorage != PackageManager.PERMISSION_GRANTED) {
            if (android.os.Build.VERSION.SDK_INT > 30)
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQ_CODE_PERM_STORAGE);
            else
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQ_CODE_PERM_STORAGE);
        } else {
            bukaGallery();
        }
    }

    // Method untuk menangani hasil dari permintaan izin
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE_PERM_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bukaGallery();
            }
        }
    }

    // Method untuk mendapatkan objek DataFileGambar dari hasil capture kamera
    private DataFileGambar getDataFileGambar() throws Exception {
        DataFileGambar dataFileGambar = null;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("hasil", ".jpg", storageDir);
        dataFileGambar = new DataFileGambar(image.getAbsolutePath(), image);
        return dataFileGambar;
    }

    // Method untuk menangani klik tombol buka kamera
    @SuppressWarnings("deprecation")
    public void bukaKameraClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            dataFileGambar = getDataFileGambar();
            Uri uri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider",
                    dataFileGambar.getFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, REQ_CODE_BUKA_KAMERA);
        } catch (Exception e) { }
    }

    // Method untuk menangani hasil dari activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_BUKA_GALLERY) {
            if (data != null) {
                Cursor cursor;
                String col[] = {"_data"};
                String projection[] = {Arrays.toString(col)};
                cursor = getApplicationContext().getContentResolver().query(data.getData(), projection, null, null, null);
                cursor.moveToFirst();
                int index = cursor.getColumnIndexOrThrow(col[0]);
                String pathGambar = cursor.getString(index);
                cursor.close();
                Bitmap gambar = BitmapFactory.decodeFile(pathGambar);
                imageView.setImageBitmap(gambar);
            }
        } else if (requestCode == REQ_CODE_BUKA_KAMERA) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = BitmapFactory.decodeFile(dataFileGambar.getPathFile());
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
