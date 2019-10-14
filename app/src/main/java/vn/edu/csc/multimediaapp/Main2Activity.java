package vn.edu.csc.multimediaapp;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class Main2Activity extends AppCompatActivity {
    VideoView videoView;
    Button btnPlayVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btnPlayVideo = findViewById(R.id.btnPlayVideo);
        videoView = findViewById(R.id.videoView);

        btnPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                videoView.setVideoURI(Uri.parse("android.resource://"
//                        + getPackageName() +"/"+ R.raw.file_check));
//                videoView.setMediaController(new MediaController(Main2Activity.this));
//                videoView.requestFocus();
//                videoView.start();

                videoView.setVideoPath("http://techslides.com/demos/sample-videos/small.mp4");
                MediaController mediaController = new MediaController(Main2Activity.this);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);
                videoView.requestFocus();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer mp) {
                        videoView.start();
                    }
                });
            }
        });


    }
}
