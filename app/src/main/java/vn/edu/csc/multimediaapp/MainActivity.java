package vn.edu.csc.multimediaapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Button button, button2, button3, btnPlayStreaming;
    private File photoFile;
    private static String fileStreaming = "https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_700KB.mp3";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1001;
    public final static int PICK_PHOTO_REQUEST_CODE = 1046;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        btnPlayStreaming = findViewById(R.id.btnPlayStreamming);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//             MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this,R.raw.classofdream );
//             mediaPlayer.start();
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/classofdream.mp3";
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchCamera();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto();
            }
        });

        btnPlayStreaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playFileInternet();
            }
        });

    }

    private void onLaunchCamera() {
        // tạo Intent để truy cập Camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String photoFileName = "IMG_" + System.currentTimeMillis();
        photoFile = getPhotoFileUri(photoFileName);

        // bao bọc file bởi ContentProvider
        Uri fileProvider = FileProvider.getUriForFile(this,
                "vn.edu.csc.multimediaapp.fileprovider",
                photoFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // thực hiện Intent để chụp ảnh
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {
        // Dùng “getExternalFilesDir” để không cần request external read/write runtime permissions
//        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "TAG");

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
        // Tạo thư mục chứa hình ảnh nếu chưa tồn tại
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("TAG", "failed to create directory");
        }

        // Trả về đối tượng file tương ứng với file name
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // tại thời điểm này, đã có hình ảnh từ camera lưu trên file
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, để tránh lỗi OutOfMemoryError khi hiển thị ảnh lên UI
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(takenImage, 200);
                // Hiển thị hình ảnh vừa chụp lên image view
                imageView.setImageBitmap(takenImage);


                //Save picture into Picture Directory of device not private app
                MediaStore.Images.Media.insertImage(getContentResolver(),
                                            takenImage,
                                       "demo_image",
                                 "demo_image"
                );


            } else { // Xử lý cho trường hợp không chụp ảnh (chọn Cancel ở Camera app)
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_PHOTO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri photoUri = data.getData();
            // Lấy uri của hình ảnh từ data
            Bitmap selectedImage = null;
            try {
                // Tạo đối tượng bitmap tương ứng với uri
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Hiển thị hình ảnh (bitmap) được chọn lên image view
            if (selectedImage != null) {
                imageView.setImageBitmap(selectedImage);
            }
        }
    }

    private void onPickPhoto() {
        // Tạo Intent để chọn hình ảnh từ Gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getPackageManager()) != null) {
            // thực hiện Intent để chọn ảnh
            startActivityForResult(intent, PICK_PHOTO_REQUEST_CODE);
        }
    }

    public void playFileInternet() {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    return false;
                }
            });
            mediaPlayer.setDataSource(fileStreaming);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

