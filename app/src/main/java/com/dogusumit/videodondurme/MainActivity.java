package com.dogusumit.videodondurme;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    final Context context = this;

    static final int REQUEST_KODUM = 100;

    ImageView imgv1;
    TextView tv1;
    Spinner spn1,spn2,spn3;
    Button btn1,btn2;
    FFmpeg ffmpeg;
    String param,alttire;
    String girdiYolu,vcodec,acodec,ciktiDosya,ciktiKlasor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        spn1 = (Spinner) findViewById(R.id.spinner1);
        spn2 = (Spinner) findViewById(R.id.spinner2);
        spn3 = (Spinner) findViewById(R.id.spinner3);
        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);
        imgv1 = (ImageView) findViewById(R.id.imageview1);
        tv1 = (TextView) findViewById(R.id.textview1);
        ffmpeg = FFmpeg.getInstance(context);
        param = "";
        vcodec = "";
        acodec = "";
        ciktiDosya = "";
        girdiYolu = "";
        ciktiKlasor = "";

        spn1.setEnabled(false);
        btn2.setEnabled(false);
        spn2.setEnabled(false);
        spn3.setEnabled(false);


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, REQUEST_KODUM);
                } catch (Exception e) {
                    toastla(e.getMessage());
                }
            }
        });


        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onFailure() {}

                @Override
                public void onSuccess() {}

                @Override
                public void onFinish() {}
            });
        } catch (FFmpegNotSupportedException e) {
            toastla( e.getMessage() );
        }

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ffmpeg.isFFmpegCommandRunning()) {
                    if(ffmpeg.killRunningProcesses()) {
                        btn2.setText(R.string.str2);
                        tv1.setText(R.string.str7);
                    }
                }
                else {
                    File a = new File(girdiYolu);
                    String s1 = a.getAbsoluteFile().getName();
                    ciktiKlasor = a.getAbsoluteFile().getParent();
                    String s2 = s1.substring(s1.lastIndexOf("."));
                    String s3 = s1.substring(0,s1.lastIndexOf("."));
                    ciktiDosya = s3+"_"+alttire+s2;
                    //tv1.setText(ciktiDosya);

                    String komut = "-y -i "+girdiYolu+vcodec+acodec+param+" "+ciktiKlasor+"/"+ciktiDosya;
                    //tv1.setText(komut);
                    ffmpegCalistir(komut);
                }
            }
        });

        spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        param = " -vf transpose=1";
                        imgv1.setRotation(90);
                        imgv1.setScaleX(1f);
                        imgv1.setScaleY(1f);
                        alttire = "90";
                        break;
                    case 2:
                        param = " -vf transpose=2,transpose=2";
                        imgv1.setRotation(180);
                        imgv1.setScaleX(1f);
                        imgv1.setScaleY(1f);
                        alttire = "180";
                        break;
                    case 3:
                        param = " -vf transpose=2";
                        imgv1.setRotation(270);
                        imgv1.setScaleX(1f);
                        imgv1.setScaleY(1f);
                        alttire = "270";
                        break;
                    case 4:
                        param = " -vf vflip";
                        imgv1.setRotation(0);
                        imgv1.setScaleX(1f);
                        imgv1.setScaleY(-1f);
                        alttire = "dikey";
                        break;
                    case 5:
                        param = " -vf hflip";
                        imgv1.setRotation(0);
                        imgv1.setScaleX(-1f);
                        imgv1.setScaleY(1f);
                        alttire = "yatay";
                        break;
                    default:
                        param = "";
                        imgv1.setRotation(0);
                        imgv1.setScaleX(1f);
                        imgv1.setScaleY(1f);
                        alttire = "";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                param = "";
                imgv1.setRotation(0);
                imgv1.setScaleX(1f);
                imgv1.setScaleY(1f);
                alttire = "";
            }
        });

        spn2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0)
                    vcodec = " -c:v " + ( (TextView)view ).getText();
                else
                    vcodec = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                vcodec = "";
            }
        });

        spn3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0)
                    acodec = " -c:a " + ( (TextView)view ).getText();
                else
                    acodec = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                acodec = "";
            }
        });

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_KODUM) {
                String selectedVideoPath;
                selectedVideoPath = uritoPath(data.getData());
                try {
                    if (selectedVideoPath == null) {
                        toastla(getString(R.string.str9));
                    } else {
                        girdiYolu = selectedVideoPath;
                        videoYukle(selectedVideoPath);
                        spn1.setEnabled(true);
                        btn2.setEnabled(true);
                        spn2.setEnabled(true);
                        spn3.setEnabled(true);
                    }
                } catch (Exception e) {
                    toastla(e.getMessage());
                }
            }
        }
    }

    public String uritoPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    void toastla(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    void videoYukle(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            File file = new File(path);
            if (!file.exists()) {
                toastla(getString(R.string.str10));
                return;
            }
            retriever.setDataSource(file.getAbsolutePath());

            imgv1.setImageBitmap(retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST));


        } catch (Exception e) {
            toastla(e.getMessage());
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                toastla(ex.getMessage());
            }
        }
    }

    void ffmpegCalistir (String s) {
        try {
            String[] cmd = s.split(" ");
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    tv1.setText(getString(R.string.str8));
                    btn2.setText(getString(R.string.str4));
                }

                @Override
                public void onProgress(String message) {
                    tv1.setText(getString(R.string.str8)+"\n"+message);
                }

                @Override
                public void onFailure(String message) {
                    tv1.setText(getString(R.string.str6)+"\n"+message);
                }

                @Override
                public void onSuccess(String message) {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(new File(ciktiKlasor+"/"+ciktiDosya));
                    mediaScanIntent.setData(contentUri);
                    context.sendBroadcast(mediaScanIntent);
                    tv1.setText(R.string.str5);
                }

                @Override
                public void onFinish() {
                    btn2.setText(R.string.str2);
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            toastla(e.getMessage());
        }
    }

    private void uygulamayiOyla() {
        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
            } catch (Exception ane) {
                toastla(e.getMessage());
            }
        }
    }

    private void marketiAc() {
        try {
            Uri uri = Uri.parse("market://developer?id=" + getString(R.string.play_store_id));
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/developer?id=" + getString(R.string.play_store_id))));
            } catch (Exception ane) {
                toastla(e.getMessage());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.oyla:
                uygulamayiOyla();
                return true;
            case R.id.market:
                marketiAc();
                return true;
            case R.id.cikis:
                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}