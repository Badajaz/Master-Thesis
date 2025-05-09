package com.example.android.boardshot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static android.speech.SpeechRecognizer.ERROR_NETWORK_TIMEOUT;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener,GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {


    private Button btnCapture;
    private Button btnCaptureWithPieces;
    private Button btnToturial;
    private TextureView textureView;

    //Check state orientation of output image
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;

    //Save to FILE
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    private Button camera;
    private Button recognizeBlocks;
    private TextView textViewCodes;
    private TextToSpeech ts;
    private Timer timer;
    private TimerTask repeatedTask;

    private int start;

    private static final String CAMERA_PATH = "/storage/emulated/0/";
    private static final String CAMERA_PATH_PHONE = "/Memória/DCIM/Camera";

    private TextToSpeech engine;

    private DatabaseReference database;
    private static final int THRESHOLD = 150;
    private int recPieces = 0;
    private int board = 0;
    private int tutorialPieces = 0;
    private String instruction = "";
    private String sequenceDB = "";
    private boolean hasFinal = false;

    private Python py;
    private String rec;
    private String boardRec;
    HashMap<String, String> tabuleiro;
    private int RECOGNIZER_RESULT = 1;

    private String speechResult = "";
    private int loop = 0;
    private String instrucao = "";
    private TimerTask TaskMenu;
    private Timer timerMenu;

    private int menu = 0;

    private int orangeArea = -1;
    private int robotLine;
    private int robotCollumn;
    private String boardAux = "";


    private GestureDetector gd;
    private PopupWindow pw;
    private int left;
    private int top;
    private int width;
    private int height;
    private int recBoardPopup;

    private String Levels;
    private int speechCount = 0;
    private int explanationCount = 0;

    private TimerTask taskTalk;


    private StorageReference mStorageRef;
    private String user;

    private int contador;
    private boolean times = false;
    private boolean ciclo = false;
    private ArrayList<Integer> pointsList;
    private TimerTask task;
    private TextToSpeech speech;
    Timer timerExplanation = new Timer("Timer");
    private Bitmap myBitmap;
    private List<Integer> coordenadas = new ArrayList<>();
    boolean terminar = false;


    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle intent = getIntent().getExtras();
        Levels = intent.getString("levels");
        user = intent.getString("user");
        //pointsList = intent.getIntegerArrayList("pointsList");
        invalidateOptionsMenu();
        ColorDrawable c = new ColorDrawable();
        c.setColor(Color.parseColor("#ff781f"));
        getSupportActionBar().setBackgroundDrawable(c);
        getSupportActionBar().setTitle("Reconhecimento");
        speech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int arg0) {
                if (arg0 == TextToSpeech.SUCCESS) {
                    //speech.setLanguage(Locale.US);
                    speech.setLanguage(new Locale("pt", "PT"));

                    if (Levels.equals("freestyle")) {
                        gameExplanationFreestyle();
                    } else if(Levels.equals("accelaration")){
                        gameExplanationFreestyle();
                    }else{
                        gameExplanation();
                    }
                }
            }
        });


        if (OpenCVLoader.initDebug()) {

            // Toast.makeText(getApplicationContext(),"sucesso",Toast.LENGTH_LONG).show();

        } else {
            //Toast.makeText(getApplicationContext(),"Insucesso",Toast.LENGTH_LONG).show();

        }


        sequenceDB = "";

        //PyObject pyf = py.getModule("parameter");
        //PyObject obj = pyf.callAttr("passParameter","WOW");

        //Toast.makeText(getApplicationContext(),obj.toString(),Toast.LENGTH_LONG).show();


        database = FirebaseDatabase.getInstance().getReference("");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        textureView = (TextureView) findViewById(R.id.textureView);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);

        TextView touch = findViewById(R.id.textoo);
        /*touch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Toast.makeText(getApplicationContext(),"TOUUUUUCHHHHHHDOOOWN",Toast.LENGTH_LONG).show();
                return false;
            }
        });*/


        touch.setOnTouchListener(this);
        gd = new GestureDetector(this, this);

        final Button boardRecognition = (Button) findViewById(R.id.boarRecButton);
        boardRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recBoardPopup = 1;
                takePicture();
            }
        });


    }

    private void takePicture() {
        if (cameraDevice == null)
            return;
        MediaActionSound sound = new MediaActionSound();
        sound.play(MediaActionSound.SHUTTER_CLICK);

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null)
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        .getOutputSizes(ImageFormat.JPEG);

            //Capture image with custom size
            int width = 640;
            int height = 480;
            if (jpegSizes != null && jpegSizes.length > 0) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            final ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurface = new ArrayList<>(2);
            outputSurface.add(reader.getSurface());
            outputSurface.add(new Surface(textureView.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            //Check orientation base on device
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            file = new File(Environment.getExternalStorageDirectory() + "/" + UUID.randomUUID().toString() + ".jpg");
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        myBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
                        save(bytes);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        {
                            if (image != null)
                                image.close();

                        }
                    }
                }

                private void save(byte[] bytes) throws IOException {
                    OutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(file);
                        outputStream.write(bytes);


                    } finally {
                        if (outputStream != null)
                            outputStream.close();
                    }


                    if (recBoardPopup == 1) {
                        boardAux = "";
                        recBoardPopup = 0;
                        boardAux = getTopCodeCorners(file);
                        if(boardAux == null || boardAux.equals("") ){
                            speech.speak( "códigos não reconhecidos",TextToSpeech.QUEUE_FLUSH, null, null);
                        }else{
                            ShowPopUpBoard();
                        }

                    }

                    if (recPieces == 1) {
                        //Toast.makeText(MainActivity.this, "recPieces", Toast.LENGTH_SHORT).show();
                        sequenceDB = "";
                        List<TopCode> listaTopCodes = recognizeTopcodes(file.getPath());
                        if (!listaTopCodes.isEmpty() && hasFinalCode(listaTopCodes)) {
                            hasFinal = true;
                            //Toast.makeText(MainActivity.this, "!Empty && final code", Toast.LENGTH_SHORT).show();
                            for (Integer t : instructionsRecognition(listaTopCodes)) {
                                                 Log.d("Topcodes", t+"");
                                if (t == 31) {
                                    sequenceDB += "E_";
                                } else if (t == 47) {
                                    sequenceDB += "D_";
                                } else if (t == 55) {
                                    sequenceDB += "C_";
                                } else if (t == 59) {
                                    sequenceDB += "LE_";
                                } else if (t == 107) {
                                    sequenceDB += "F";
                                }else if (t == 61) {
                                    sequenceDB += "LB_2_";

                                }

                            }
                        }


                    }
                    if (tutorialPieces == 1) {
                        tutorialPieces = 0;
                        List<TopCode> listaTopCodes = recognizeTopcodes(file.getPath());
                        if (!listaTopCodes.isEmpty()) {
                            for (TopCode t : listaTopCodes) {
                                //Toast.makeText(MainActivity.this, "Topcode = "+t.code, Toast.LENGTH_SHORT).show();
                                if (t.code == 31) {
                                    instruction = "Esquerda";

                                } else if (t.code == 47) {
                                    instruction = "Direita";

                                } else if (t.code == 55) {
                                    instruction = "Cima";

                                } else if (t.code == 59) {
                                    instruction = "fim de ciclo";

                                } else if (t.code == 107) {
                                    instruction = "Fim";

                                }else if (t.code == 61){
                                    instruction = "ciclo duas vezes";
                                }

                            }

                            engine = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status == TextToSpeech.SUCCESS) {
                                        engine.setLanguage(new Locale("pt", "PT"));
                                        engine.speak("A peça corresponde à instrução " + instruction,
                                                TextToSpeech.QUEUE_FLUSH, null, null);
                                    }

                                }
                            });


                        }
                    }

                    if (recPieces == 1 || board == 1) {

                        if (board == 1) {
                            //boardRec = boardRecognition2();
                            boardRec = getTopCodeCorners(file);

                            if(boardRec == null){

                                engine = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                    @Override
                                    public void onInit(int status) {
                                        engine.setLanguage(new Locale("pt", "PT"));
                                        engine.speak("o mapa não foi bem reconhecido, volta a tocar uma vez no ecrã",
                                                TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                });


                            }else{
                                if (Levels.equals("accelaration")){

                                    introSpeach();
                                    while (speechCount < getNumberOfWaitingLines());
                                    lauchSpeechRecognitionAccelaration();

                                }else{

                                    introSpeach();
                                    while (speechCount < getNumberOfWaitingLines());
                                    lauchSpeechRecognition();


                                }


                            }



                        }


                        if (recPieces == 1 && hasFinal) {
                            timer.cancel();
                            repeatedTask.cancel();
                            engine = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    engine.setLanguage(new Locale("pt", "PT"));
                                    engine.speak("Sequencia reconhecida",
                                            TextToSpeech.QUEUE_FLUSH, null, null);
                                }
                            });
                            //database.child("sequencia").setValue(sequenceDB);

                            recPieces = 0;
                            boardRec = getTopCodeCorners(file);
                            if (boardRec == null){

                                 engine = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                     @Override
                                     public void onInit(int status) {
                                         engine.setLanguage(new Locale("pt", "PT"));
                                         engine.speak("o mapa não foi bem reconhecido, volta a tocar duas vezes no ecrã",
                                                 TextToSpeech.QUEUE_FLUSH, null, null);
                                     }
                                 });
                                

                            }else {


                                if (!sequenceDB.equals("")) {
                                    Log.d("TopcodesSeq",sequenceDB);

                                  if( containsSameNumberofLoopEndsAsBegins(sequenceDB)){

                                      Handler mHandler = new Handler(getMainLooper());
                                      mHandler.post(new Runnable() {
                                          @Override
                                          public void run() {
                                              Intent intent = new Intent(MainActivity.this, BoardDraw.class);
                                              intent.putExtra("message", boardRec);
                                              intent.putExtra("map", tabuleiro);
                                              intent.putExtra("sequencia", sequenceDB);
                                              intent.putExtra("roboLinha", robotLine);
                                              intent.putExtra("roboColuna", robotCollumn);
                                              intent.putExtra("levels", Levels);
                                              intent.putExtra("user", user);
                                              intent.putExtra("activity", "MainActivity");
                                              //intent.putIntegerArrayListExtra("pointsList",pointsList);
                                              startActivity(intent);
                                          }
                                      });


                                  }else{


                                       engine = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                           @Override
                                           public void onInit(int status) {
                                               engine.setLanguage(new Locale("pt", "PT"));
                                               engine.speak("Não colocou um final de algum ciclo",
                                                       TextToSpeech.QUEUE_FLUSH, null, null);
                                           }
                                       });

                                  }


















                                }
                            }
                        }


                        board = 0;

                        //Toast.makeText(MainActivity.this, "saved", Toast.LENGTH_SHORT).show();

                        Uri filed = Uri.fromFile(file);

                        StorageReference riversRef = mStorageRef.child("images");
                        Toast.makeText(MainActivity.this, "chego aqui", Toast.LENGTH_SHORT).show();

                        riversRef.putFile(filed)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content
                                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        Toast.makeText(MainActivity.this, "image Uploaded", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        // ...
                                        Toast.makeText(MainActivity.this, "image Upload falhou", Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }
                }
            };

            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    //Toast.makeText(MainActivity.this, "Saved "+file, Toast.LENGTH_SHORT).show();

                    createCameraPreview();
                }
            };

            cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try {
                        cameraCaptureSession.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            }, mBackgroundHandler);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }






    private void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if (cameraDevice == null)
                        return;
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(MainActivity.this, "Changed", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (cameraDevice == null)
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {

            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            //Check realtime permission if run higher API 23
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You can't use camera without permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if (textureView.isAvailable())
            openCamera();
        else
            textureView.setSurfaceTextureListener(textureListener);
    }

    @Override
    protected void onPause() {
        stopBackgroundThread();
        super.onPause();
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }


    private void deleteAllPhotos() {
        File cameraFolder = new File(CAMERA_PATH);

        if (cameraFolder.isDirectory()) {
            File[] photos = cameraFolder.listFiles();
            if (photos != null) {
                String concatNameFiles = "";
                for (File image : photos) {
                    image.delete();

                }

            } else {
                Toast.makeText(getApplicationContext(), "Não contém fotos", Toast.LENGTH_LONG).show();
            }

        }

    }


    private String getLastPhotoTaken() {

        File cameraFolder = new File(CAMERA_PATH);

        Map<String, Date> photoDates = new HashMap<>();

        if (cameraFolder.isDirectory()) {
            File[] photos = cameraFolder.listFiles();
            if (photos != null) {
                String concatNameFiles = "";
                for (File image : photos) {
                    Date date = new Date(image.lastModified());
                    photoDates.put(image.getName(), date);
                }


            } else {
                Toast.makeText(getApplicationContext(), "Não contém fotos", Toast.LENGTH_LONG).show();
            }

        }

        return getLastName(photoDates);
    }


    private static String getLastName(@NonNull Map<String, Date> photoDates) {

        String lastPhoto = "";
        Date lastDate = new Date("05/05/1970");
        for (Map.Entry<String, Date> entry1 : photoDates.entrySet()) {
            if (!(entry1.getKey().equals("cache")) && entry1.getValue().after(lastDate)) {
                lastDate = entry1.getValue();
                lastPhoto = entry1.getKey();
            }
        }
        return lastPhoto;
    }


    private List<TopCode> recognizeTopcodes(String nameLastPhoto) {
        //Toast.makeText(getApplicationContext(),"path ="+ nameLastPhoto, Toast.LENGTH_LONG).show();

        List<TopCode> lista = null;
        Bitmap bmImg = BitmapFactory.decodeFile(nameLastPhoto);
        if (bmImg != null) {
            Scanner sc = new Scanner();
            lista = sc.scan(bmImg);
            if (lista.isEmpty())
                Toast.makeText(getApplicationContext(), "lista de scan vazia", Toast.LENGTH_LONG).show();
            String codes = "";
            for (TopCode t : lista) {
                codes += t.getCode() + " " + "x = " + t.getCenterX() + " " + "y = " + t.getCenterY() + "\n";
            }
            //Toast.makeText(getApplicationContext(), codes, Toast.LENGTH_LONG).show();
        } else {

            Toast.makeText(getApplicationContext(), "Não tem fotos tiradas ou luz pode não permitir o reconhecimento do topcode", Toast.LENGTH_LONG).show();

        }
        return lista;
    }

    private boolean hasFinalCode(List<TopCode> lt) {

        for (TopCode t : lt) {

            if (t.getCode() == 107) {
                return true;
            }
        }

        return false;
    }

    private List<Integer> instructionsRecognition(List<TopCode> listaTopCodes) {

        String finalInstructions = "";
        List<Float> sortYCoordenate = new ArrayList<>();
        List<Integer> TopcodeInstructions = new ArrayList<>();
        if (listaTopCodes != null) {
            for (TopCode t : listaTopCodes) {
                sortYCoordenate.add(t.getCenterY());

            }
            Collections.sort(sortYCoordenate);
            Collections.reverse(sortYCoordenate);
            for (float f : sortYCoordenate) {
                for (TopCode t : listaTopCodes) {
                    if (f == t.getCenterY()) {
                        TopcodeInstructions.add(t.getCode());
                    }
                }
            }


        } else {
            //Toast.makeText(getApplicationContext(), "a lista está vazia", Toast.LENGTH_LONG).show();

        }


        return TopcodeInstructions;
    }

    private void printMatrixAdjusted(List<PyObject> matrixAdjusted) {
        for (PyObject py : matrixAdjusted) {
            Log.d("Matrix", py.asList() + "");
        }

    }

    private String getRobotCoordenates(Map<String, Integer> blackareas) {

        List<Integer> list = new ArrayList<>(blackareas.values());
        Collections.sort(list);
        int value = list.get(list.size() - 1);
        for (Map.Entry<String, Integer> entry : blackareas.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }

        }
        return "";

    }

    private String boardRecognition() throws IOException {

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(getApplicationContext()));
        }

        if (Python.isStarted()) {
            //Toast.makeText(getApplicationContext(),"Python Started",Toast.LENGTH_LONG).show();
        }

        Python py = Python.getInstance();
        PyObject cv2 = py.getModule("cv2");
        PyObject numpy = py.getModule("numpy");


        Mat matrix = Imgcodecs.imread(file.getPath());


        List<MatOfPoint> contours = new ArrayList<>();


        PyObject vhlines = py.getModule("verticalAndHorizontal");
        TextView tv = findViewById(R.id.textoo);
        PyObject obj3 = vhlines.callAttr("squares", file.getPath());
        List<PyObject> squares = obj3.asList();


        //brigth image
        Mat matrixBright = new Mat();
        matrix.convertTo(matrixBright, -1, 1, 200);

        Mat matrixRobot = new Mat();
        matrix.convertTo(matrixRobot, -1, 1, 0);


        Mat cropped = null;
        Mat croppedNormal = null;
        rec = "";
        int count = 0;
        Map<String, Integer> blackareas = new HashMap();
        tabuleiro = new HashMap();


        //Toast.makeText(getApplicationContext(),squares.size()+"",Toast.LENGTH_LONG).show();
        int linha = 0;

        Toast.makeText(getApplicationContext(), "squaressssss" + squares.size() + " ", Toast.LENGTH_LONG).show();
        Log.d("SQUARE", squares.size() + "");
        for (int i = 0; i < squares.size(); i++) {
            int a = squares.get(i).asList().get(0).toInt();
            int b = squares.get(i).asList().get(1).toInt();
            int c = squares.get(i).asList().get(2).toInt();
            int d = squares.get(i).asList().get(3).toInt();
            // Log.d("SQUARES",a+" "+b+" "+c+" "+d);


            Rect roi = new Rect(a, b, c - a, d - b);
            cropped = new Mat(matrixBright, roi);
            croppedNormal = new Mat(matrixRobot, roi);


            int w = c - a, h = d - b;


            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
            Canvas canvas = new Canvas(bmp);
            Utils.matToBitmap(cropped, bmp);

            Bitmap.Config conf2 = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp2 = Bitmap.createBitmap(w, h, conf2); // this creates a MUTABLE bitmap
            Canvas canvas2 = new Canvas(bmp2);
            Utils.matToBitmap(croppedNormal, bmp2);


            int redColors = 0;
            int greenColors = 0;
            int blueColors = 0;
            int pixelCount = 0;

            for (int y = 0; y < bmp.getHeight(); y++) {
                for (int x = 0; x < bmp.getWidth(); x++) {
                    int color = bmp.getPixel(x, y);
                    pixelCount++;
                    redColors += Color.red(color);
                    greenColors += Color.green(color);
                    blueColors += Color.blue(color);


                }
            }


            int red = (redColors / pixelCount);
            int green = (greenColors / pixelCount);
            int blue = (blueColors / pixelCount);
            //int yellow = ((redColors+greenColors)/pixelCount);


            if (green >= red && green >= blue) {
                tabuleiro.put("" + linha + "" + count, "O");
            } else if (red >= green && red >= blue) {
                tabuleiro.put("" + linha + "" + count, "F");
            } else if (blue >= red && blue >= green) {
                tabuleiro.put("" + linha + "" + count, "X");
            }


            MatOfByte matOfByte = new MatOfByte();
            Imgcodecs.imencode(".jpg", croppedNormal, matOfByte);
            byte[] byteArray = matOfByte.toArray();

            //save method
            OutputStream outputStream2 = null;
            try {
                outputStream2 = new FileOutputStream(file);
                outputStream2.write(byteArray);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream2 != null)
                    outputStream2.close();
            }

            PyObject orangeLines = py.getModule("OrangeLines");
            PyObject orangeLinesMod = orangeLines.callAttr("getStartPosition", file.getPath());
            int orangeAux = orangeLinesMod.toInt();
            // Log.d("AREAS",orangeArea+" , "+orangeAux+"  "+"("+linha+","+count+")");


            if (orangeArea < orangeAux) {
                orangeArea = orangeAux;
                robotLine = linha;
                robotCollumn = count;
            }


            count++;
            if (count == 12) {
                rec += "\n";
                count = 0;
                linha++;
            }

        }
        //String robotCoordenates = getRobotCoordenates(blackareas);
        tabuleiro.put(("" + robotLine + "" + robotCollumn), "R");

        rec = "";
        for (int i = 0; i < 12; i++) {

            for (int j = 0; j < 12; j++) {
                rec += tabuleiro.get("" + i + "" + j) + " ";


            }
            rec += "\n";

        }


        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, matOfByte);
        byte[] byteArray = matOfByte.toArray();

        //save method
        OutputStream outputStream2 = null;
        try {
            outputStream2 = new FileOutputStream(file);
            outputStream2.write(byteArray);


        } finally {
            if (outputStream2 != null)
                outputStream2.close();
        }
        return rec;
    }


    private String boardRecognition2() throws IOException {

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(getApplicationContext()));
        }

        if (Python.isStarted()) {
            //Toast.makeText(getApplicationContext(),"Python Started",Toast.LENGTH_LONG).show();
        }

        Python py = Python.getInstance();
        PyObject cv2 = py.getModule("cv2");
        PyObject numpy = py.getModule("numpy");


        Mat matrix = Imgcodecs.imread(file.getPath());
        Mat matrixBrightAttempt = new Mat();


        PyObject image = py.getModule("improvedImage");
        PyObject imageAlphaBeta = image.callAttr("automatic_brightness_and_contrast", file.getPath());
        List<PyObject> alphaBeta = imageAlphaBeta.asList();
        matrix.convertTo(matrixBrightAttempt, -1, alphaBeta.get(0).toFloat(), alphaBeta.get(1).toFloat());
        int brightness = getValueOfLuminosity(file);
        Log.d("Brightness", brightness + "");

        Mat matrixBrightAttempt2 = new Mat();

        if (brightness >= 0 && brightness < 70) {
            matrixBrightAttempt.convertTo(matrixBrightAttempt2, -1, 1, 240);
        } else if (brightness >= 70 && brightness < 75) {
            matrixBrightAttempt.convertTo(matrixBrightAttempt2, -1, 1, 220);
        } else if (brightness >= 75 && brightness < 90) {
            matrixBrightAttempt.convertTo(matrixBrightAttempt2, -1, 1, 150);
        } else if (brightness >= 95 && brightness < 100) {
            matrixBrightAttempt.convertTo(matrixBrightAttempt2, -1, 1, 150);
        } else if (brightness >= 100 && brightness <= 200) {
            matrixBrightAttempt.convertTo(matrixBrightAttempt2, -1, 1, 100);
        } else if (brightness > 200) {
            matrixBrightAttempt.convertTo(matrixBrightAttempt2, -1, 1, 1);
        }


        savePhoto(matrixBrightAttempt2, file);


        PyObject vhlines = py.getModule("verticalAndHorizontal");
        TextView tv = findViewById(R.id.textoo);
        PyObject obj3 = vhlines.callAttr("squares", file.getPath());
        List<PyObject> squares = obj3.asList();


        Mat cropped = null;
        Mat croppedRobot = null;
        rec = "";
        int count = 0;
        Map<String, Integer> blackareas = new HashMap();
        tabuleiro = new HashMap();
        Mat croppedRobotFinal = new Mat();


        if (brightness >= 0 && brightness < 70) {
            matrix.convertTo(croppedRobotFinal, -1, 1, 100);
        } else if (brightness >= 70 && brightness < 75) {
            matrix.convertTo(croppedRobotFinal, -1, 1, 50);
        } else if (brightness >= 75 && brightness < 90) {
            matrix.convertTo(croppedRobotFinal, -1, 1, 50);
        } else if (brightness >= 95 && brightness < 100) {
            matrix.convertTo(croppedRobotFinal, -1, 1, 1);
        } else if (brightness >= 100 && brightness <= 200) {
            matrix.convertTo(croppedRobotFinal, -1, 1, 1);
        } else if (brightness > 200) {
            matrix.convertTo(croppedRobotFinal, -1, 1, 1);
        }


        //if(brightness >=100 && brightness <= 200 ) {

        //}


        //Toast.makeText(getApplicationContext(),squares.size()+"",Toast.LENGTH_LONG).show();
        int linha = 0;

        for (int i = 0; i < squares.size(); i++) {
            int a = squares.get(i).asList().get(0).toInt();
            int b = squares.get(i).asList().get(1).toInt();
            int c = squares.get(i).asList().get(2).toInt();
            int d = squares.get(i).asList().get(3).toInt();


            Rect roi = new Rect(a, b, c - a, d - b);
            cropped = new Mat(matrixBrightAttempt2, roi);
            croppedRobot = new Mat(croppedRobotFinal, roi);


            int w = c - a, h = d - b;


            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
            Canvas canvas = new Canvas(bmp);
            Utils.matToBitmap(cropped, bmp);


            int redColors = 0;
            int greenColors = 0;
            int blueColors = 0;
            int pixelCount = 0;

            for (int y = 0; y < bmp.getHeight(); y++) {
                for (int x = 0; x < bmp.getWidth(); x++) {
                    int color = bmp.getPixel(x, y);
                    pixelCount++;
                    redColors += Color.red(color);
                    greenColors += Color.green(color);
                    blueColors += Color.blue(color);


                }
            }


            int red = (redColors / pixelCount);
            int green = (greenColors / pixelCount);
            int blue = (blueColors / pixelCount);
            //Log.d("PIXEL",linha+""+count+" "+"("+redColors+","+greenColors+","+blueColors+")");
            //Log.d("PIXEL",linha+""+count+" "+"("+red+","+green+","+blue+")");

            if ((green >= red && green >= blue)) {
                tabuleiro.put("" + linha + "" + count, "O");
                //Log.d("PIXEL","O");

            } else if (red >= green && red >= blue) {
                tabuleiro.put("" + linha + "" + count, "F");
                //Log.d("PIXEL","F");

            } else if (blue >= red && blue >= green) {
                tabuleiro.put("" + linha + "" + count, "X");
                //Log.d("PIXEL","X");

            }

            savePhoto(croppedRobot, file);


            PyObject orangeLines = py.getModule("OrangeLines");
            PyObject orangeLinesMod = orangeLines.callAttr("getStartPosition", file.getPath());
            int orangeAux = orangeLinesMod.toInt();
            // Log.d("AREAS",orangeArea+" , "+orangeAux+"  "+"("+linha+","+count+")");


            if (orangeArea <= orangeAux) {
                orangeArea = orangeAux;
                robotLine = linha;
                robotCollumn = count;
                Log.d("AREA", orangeArea + "" + linha + "," + count);
                coordenadas.add(linha);
                coordenadas.add(count);

            }

            savePhoto(cropped, file);


            count++;
            if (count == 12) {
                rec += "\n";
                count = 0;
                linha++;
            }

        }
        //String robotCoordenates = getRobotCoordenates(blackareas);
        tabuleiro.put(("" + robotLine + "" + robotCollumn), "R");

        //boardCorrection(coordenadas);


        rec = "";
        for (int i = 0; i < 12; i++) {

            for (int j = 0; j < 12; j++) {
                rec += tabuleiro.get("" + i + "" + j) + " ";


            }
            rec += "\n";

        }

        savePhoto(matrix, file);

        return rec;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if (requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK) {

            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches == null) {
                Toast.makeText(getApplicationContext(), "matches null", Toast.LENGTH_LONG).show();
            }
            speechResult = matches.get(0);

            if (Levels.equals("level1")) {
                if (speechResult.contains("esquerda")) {
                    sequenceDB += "E_";
                    engine.speak("RECEBIDO A INSTRUÇÃO ESQUERDA! MAIS ALGUMA INSTRUÇÃO?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("direita")) {
                    sequenceDB += "D_";
                    engine.speak("RECEBIDO A INSTRUÇÃO DIREITA! MAIS ALGUMA INSTRUÇÃO?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("frente") || speechResult.contains("cima")) {
                    sequenceDB += "C_";
                    engine.speak("RECEBIDO A INSTRUÇÃO FRENTE! MAIS ALGUMA INSTRUÇÃO?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("não")) {
                    engine.speak("ENTÂO, DIZ TERMINAR!", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("sim")) {
                    engine.speak("ACHAS QUE DEVA IR PARA A FRENTE OU VIRAR PARA A DIREITA OU  VIRAR  A ESQUERDA?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("terminar")) {

                    terminar = true;
                    sequenceDB += "F";
                    if (!sequenceDB.equals("")) {
                        Handler mHandler = new Handler(getMainLooper());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, BoardDraw.class);
                                intent.putExtra("message", boardRec);
                                intent.putExtra("map", tabuleiro);
                                intent.putExtra("sequencia", sequenceDB);
                                intent.putExtra("roboLinha", robotLine);
                                intent.putExtra("roboColuna", robotCollumn);
                                intent.putExtra("user", user);
                                intent.putExtra("levels", Levels);
                                intent.putExtra("activity", "MainActivity");
                                //intent.putIntegerArrayListExtra("pointsList",pointsList);
                                startActivity(intent);
                            }
                        });
                    }
                } else {
                    engine.speak("Não entendi o que quiseste dizer! Repete por favor!", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                }


            } else if (Levels.equals("level2")) {


                if (speechResult.contains("esquerda")) {
                    instrucao = "E_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO ESQUERDA! Achas que devia executar essa instrução quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if (speechResult.contains("direita")) {
                    instrucao = "D_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO DIREITA! Achas que devia executar essa instrução quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("frente") || speechResult.contains("cima")) {
                    instrucao = "C_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO FRENTE! Achas que devia executar essa instrução quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (times && (speechResult.contains("vezes") || speechResult.contains("vez"))) {
                    times = false;
                    sequenceDB += getRepeatedStringByTimesNumber(instrucao, getNumberOfTimes(speechResult));
                    engine.speak("MAIS ALGUMA INSTRUÇÃO?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("não")) {
                    engine.speak("ENTÂO, DIZ TERMINAR!", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("sim")) {
                    engine.speak("ACHAS QUE DEVA IR PARA A FRENTE OU VIRAR PARA A DIREITA OU  VIRAR  A ESQUERDA?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("terminar")) {
                    terminar = true;
                    sequenceDB += "F";
                    if (!sequenceDB.equals("")) {
                        Handler mHandler = new Handler(getMainLooper());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, BoardDraw.class);
                                intent.putExtra("message", boardRec);
                                intent.putExtra("map", tabuleiro);
                                intent.putExtra("sequencia", sequenceDB);
                                intent.putExtra("roboLinha", robotLine);
                                intent.putExtra("roboColuna", robotCollumn);
                                intent.putExtra("user", user);
                                intent.putExtra("levels", Levels);
                                intent.putExtra("activity", "MainActivity");

                                //intent.putIntegerArrayListExtra("pointsList",pointsList);

                                startActivity(intent);
                            }
                        });
                    }
                } else {
                    engine.speak("Não entendi o que quiseste dizer! Repete por favor!", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                }

            } else if (Levels.equals("level3")) {


                if (speechResult.contains("esquerda")) {
                    instrucao = "E_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO ESQUERDA! Achas que devia executar essa instrução quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if (speechResult.contains("direita")) {
                    instrucao = "D_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO DIREITA! Achas que devia executar essa instrução quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("frente") || speechResult.contains("cima")) {
                    instrucao = "C_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO FRENTE! Achas que devia executar essa instrução quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (times && (speechResult.contains("vezes") || speechResult.contains("vez"))) {
                    times = false;
                    sequenceDB += getRepeatedStringByTimesNumber(instrucao, getNumberOfTimes(speechResult));
                    engine.speak("MAIS ALGUMA INSTRUÇÃO?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("não")) {
                    engine.speak("ENTÂO, DIZ TERMINAR!", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("sim")) {
                    engine.speak("ACHAS QUE DEVA IR PARA A FRENTE OU VIRAR PARA A DIREITA OU  VIRAR  A ESQUERDA?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("terminar")) {
                    terminar = true;
                    sequenceDB += "F";
                    if (!sequenceDB.equals("")) {
                        Handler mHandler = new Handler(getMainLooper());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, BoardDraw.class);
                                intent.putExtra("message", boardRec);
                                intent.putExtra("map", tabuleiro);
                                intent.putExtra("sequencia", sequenceDB);
                                intent.putExtra("roboLinha", robotLine);
                                intent.putExtra("roboColuna", robotCollumn);
                                intent.putExtra("user", user);
                                intent.putExtra("levels", Levels);
                                intent.putExtra("activity", "MainActivity");

                                //intent.putIntegerArrayListExtra("pointsList",pointsList);

                                startActivity(intent);
                            }
                        });
                    }
                } else {
                    engine.speak("Não entendi o que quiseste dizer! Repete por favor!", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                }

            } else if (Levels.equals("level3")) {


                if (speechResult.contains("esquerda")) {
                    instrucao = "E_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO ESQUERDA! Achas que devia executar essa instrução quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if (speechResult.contains("direita")) {
                    instrucao = "D_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO DIREITA! Achas que devia executar essa instrução quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("frente") || speechResult.contains("cima")) {
                    instrucao = "C_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO FRENTE! Achas que devia executar essa instrução quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (times && (speechResult.contains("vezes") || speechResult.contains("vez"))) {
                    times = false;
                    sequenceDB += getRepeatedStringByTimesNumber(instrucao, getNumberOfTimes(speechResult));
                    engine.speak("MAIS ALGUMA INSTRUÇÃO?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("não")) {
                    engine.speak("ENTÂO, DIZ TERMINAR!", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("sim")) {
                    engine.speak("ACHAS QUE DEVA IR PARA A FRENTE OU VIRAR PARA A DIREITA OU  VIRAR  A ESQUERDA?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("terminar")) {
                    terminar = true;
                    sequenceDB += "F";
                    if (!sequenceDB.equals("")) {
                        Handler mHandler = new Handler(getMainLooper());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, BoardDraw.class);
                                intent.putExtra("message", boardRec);
                                intent.putExtra("map", tabuleiro);
                                intent.putExtra("sequencia", sequenceDB);
                                intent.putExtra("roboLinha", robotLine);
                                intent.putExtra("roboColuna", robotCollumn);
                                intent.putExtra("user", user);
                                intent.putExtra("levels", Levels);
                                intent.putExtra("activity", "MainActivity");
                                //intent.putIntegerArrayListExtra("pointsList",pointsList);

                                startActivity(intent);
                            }
                        });
                    }
                } else {
                    engine.speak("Não entendi o que quiseste dizer! Repete por favor!", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                }


            } else if (Levels.equals("level4")) {


                if (speechResult.contains("esquerda")) {
                    instrucao = "E_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO ESQUERDA! Achas que devia executar essa instrução quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if (speechResult.contains("direita")) {
                    instrucao = "D_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO DIREITA! Achas que devia executar essa instrução quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("frente") || speechResult.contains("cima")) {
                    instrucao = "C_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO FRENTE! Achas que devia executar essa instrução quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (times && (speechResult.contains("vezes") || speechResult.contains("vez"))) {
                    times = false;
                    sequenceDB += getRepeatedStringByTimesNumber(instrucao, getNumberOfTimes(speechResult));
                    engine.speak("MAIS ALGUMA INSTRUÇÃO?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("não")) {
                    engine.speak("ENTÂO, DIZ TERMINAR!", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("sim")) {
                    engine.speak("ACHAS QUE DEVA IR PARA A FRENTE OU VIRAR PARA A DIREITA OU  VIRAR  A ESQUERDA?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("terminar")) {
                    terminar = true;
                    sequenceDB += "F";
                    if (!sequenceDB.equals("")) {
                        Handler mHandler = new Handler(getMainLooper());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, BoardDraw.class);
                                intent.putExtra("message", boardRec);
                                intent.putExtra("map", tabuleiro);
                                intent.putExtra("sequencia", sequenceDB);
                                intent.putExtra("roboLinha", robotLine);
                                intent.putExtra("roboColuna", robotCollumn);
                                intent.putExtra("user", user);
                                intent.putExtra("levels", Levels);
                                intent.putExtra("activity", "MainActivity");
                                //intent.putIntegerArrayListExtra("pointsList",pointsList);

                                startActivity(intent);
                            }
                        });
                    }
                } else {
                    engine.speak("Não entendi o que quiseste dizer! Repete por favor!", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                }


            } else if (Levels.equals("level5")) {

                if (speechResult.contains("esquerda") && ciclo) {
                    sequenceDB += "E_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO ESQUERDA!SE ACHAS QUE JÀ CHEGASTE AO FIM DO CICLO  DIZ TERMINAR CICLO!", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if (speechResult.contains("direita") && ciclo) {
                    sequenceDB += "D_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO DIREITA!SE ACHAS QUE JÀ CHEGASTE AO FIM DO CICLO  DIZ TERMINAR CICLO!", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if (ciclo && (speechResult.contains("frente") || speechResult.contains("cima"))) {
                    sequenceDB += "C_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO FRENTE! já terminaste o ciclo?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if (speechResult.contains("esquerda")) {
                    instrucao = "E_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO ESQUERDA! Achas que devia executar essa instrução quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if (speechResult.contains("direita")) {
                    instrucao = "D_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO DIREITA! Achas que devia executar essa instrução quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if (speechResult.contains("frente") || speechResult.contains("cima")) {
                    instrucao = "C_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO FRENTE! Achas que devia executar essa instrução quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if (times && (speechResult.contains("vezes") || speechResult.contains("vez"))) {
                    sequenceDB += getRepeatedStringByTimesNumber(instrucao, getNumberOfTimes(speechResult));
                    engine.speak("MAIS ALGUMA INSTRUÇÃO?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if ((speechResult.contains("ciclo") || speechResult.contains("loop")) && ciclo == false) {
                    sequenceDB += "LB_";
                    ciclo = true;
                    engine.speak("ACHAS QUE DEVIA EXECUTAR O CICLO QUANTAS VEZES?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if (ciclo && (speechResult.contains("vezes") || speechResult.contains("vez"))) {
                    sequenceDB += getNumberOfTimes(speechResult) + "_";
                    engine.speak("QUE INSTRUÇÕES ACHAS QUE DEVES REPETIR PARA CONSTRUIR O CICLO? DIZENDO UMA DE CADA VEZ! FRENTE,DIREITA OU ESQUERDA?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if ((speechResult.contains("sim") || speechResult.contains("terminar ciclo")) && ciclo) {
                    sequenceDB += "LE_";
                    ciclo = false;
                    engine.speak("CICLO TERMINADO!QUERES CONTINUAR OU TERMINAR O PROGRAMA ?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("não") && ciclo) {
                    engine.speak("QUE INSTRUÇÕES ACHAS QUE DEVES REPETIR PARA CONSTRUIR O CICLO? DIZENDO UMA DE CADA VEZ! FRENTE,DIREITA OU ESQUERDA?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("continuar")) {
                    times = false;
                    engine.speak("ACHAS QUE DEVA IR PARA A FRENTE OU VIRAR PARA A DIREITA OU  VIRAR  A ESQUERDA OU FAZER UM CICLO?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("não") && times) {
                    times = false;
                    engine.speak("Então diz terminar", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("sim") && times) {
                    engine.speak("ACHAS QUE DEVA IR PARA A FRENTE OU VIRAR PARA A DIREITA OU  VIRAR  A ESQUERDA OU FAZER UM CICLO?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("terminar")) {
                    terminar = true;
                    times = false;
                    sequenceDB += "F";

                    if (!sequenceDB.equals("")) {
                        Handler mHandler = new Handler(getMainLooper());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, BoardDraw.class);
                                intent.putExtra("message", boardRec);
                                intent.putExtra("map", tabuleiro);
                                intent.putExtra("sequencia", sequenceDB);
                                intent.putExtra("roboLinha", robotLine);
                                intent.putExtra("roboColuna", robotCollumn);
                                intent.putExtra("user", user);
                                intent.putExtra("levels", Levels);
                                intent.putExtra("activity", "MainActivity");
                                //intent.putIntegerArrayListExtra("pointsList",pointsList);

                                startActivity(intent);
                            }
                        });
                        Toast.makeText(getApplicationContext(), sequenceDB, Toast.LENGTH_LONG).show();
                    }else{
                        engine.speak("Não entendi o que quiseste dizer! Repete por favor!", TextToSpeech.QUEUE_FLUSH, null, null);

                    }


                }


            }else if (Levels.equals("freestyle")) {

                if (speechResult.contains("esquerda") && ciclo) {
                    sequenceDB += "E_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO ESQUERDA!SE ACHAS QUE JÀ CHEGASTE AO FIM DO CICLO  DIZ TERMINAR CICLO!", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if (speechResult.contains("direita") && ciclo) {
                    sequenceDB += "D_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO DIREITA!SE ACHAS QUE JÀ CHEGASTE AO FIM DO CICLO  DIZ TERMINAR CICLO!", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if (ciclo && (speechResult.contains("frente") || speechResult.contains("cima"))) {
                    sequenceDB += "C_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO FRENTE!  se já terminaste o ciclo, diz Terminar Ciclo", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if (speechResult.contains("esquerda")) {
                    instrucao = "E_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO ESQUERDA! Achas que devia executar essa instrução quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if (speechResult.contains("direita")) {
                    instrucao = "D_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO DIREITA! Achas que devia executar essa instrução quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if (speechResult.contains("frente") || speechResult.contains("cima")) {
                    instrucao = "C_";
                    times = true;
                    engine.speak("RECEBIDO A INSTRUÇÃO FRENTE! Achas que devia executar essa instrução quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if (ciclo && (speechResult.contains("vezes") || speechResult.contains("vez"))) {
                sequenceDB += getNumberOfTimes(speechResult) + "_";
                engine.speak("QUE INSTRUÇÕES ACHAS QUE DEVES REPETIR PARA CONSTRUIR O CICLO? DIZENDO UMA DE CADA VEZ! FRENTE,DIREITA OU ESQUERDA?", TextToSpeech.QUEUE_FLUSH, null, null);
                //lauchSpeechRecognition();
                } else if (times && (speechResult.contains("vezes") || speechResult.contains("vez"))) {
                    times = false;
                    sequenceDB += getRepeatedStringByTimesNumber(instrucao, getNumberOfTimes(speechResult));
                    engine.speak("MAIS ALGUMA INSTRUÇÃO?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if ((speechResult.contains("ciclo") || speechResult.contains("loop")) && ciclo == false) {
                    sequenceDB += "LB_";
                    ciclo = true;
                    engine.speak("ACHAS QUE DEVIA EXECUTAR O CICLO QUANTAS VEZES?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();



                } else if ((speechResult.contains("sim") || speechResult.contains("terminar ciclo")) && ciclo) {
                    sequenceDB += "LE_";
                    ciclo = false;
                    engine.speak("CICLO TERMINADO!QUERES CONTINUAR OU TERMINAR O PROGRAMA ?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("não") && ciclo) {
                    engine.speak("QUE INSTRUÇÕES ACHAS QUE DEVES REPETIR PARA CONSTRUIR O CICLO? DIZENDO UMA DE CADA VEZ! FRENTE,DIREITA OU ESQUERDA?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                }else if (speechResult.contains("sim") && !ciclo) {
                    times = true;
                    engine.speak("ACHAS QUE DEVA IR PARA A FRENTE OU VIRAR PARA A DIREITA OU  VIRAR  A ESQUERDA OU FAZER UM CICLO?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                }else if (speechResult.contains("não") && !ciclo) {
                    times = true;
                    engine.speak("Então diz terminar", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();

                } else if (speechResult.contains("continuar")) {
                    times = false;
                    engine.speak("ACHAS QUE DEVA IR PARA A FRENTE OU VIRAR PARA A DIREITA OU  VIRAR  A ESQUERDA OU FAZER UM CICLO?", TextToSpeech.QUEUE_FLUSH, null, null);
                    //lauchSpeechRecognition();
                } else if (speechResult.contains("terminar")) {
                    terminar = true;
                    times = false;
                    sequenceDB += "F";

                    if (!sequenceDB.equals("")) {
                        Handler mHandler = new Handler(getMainLooper());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, BoardDraw.class);
                                intent.putExtra("message", boardRec);
                                intent.putExtra("map", tabuleiro);
                                intent.putExtra("sequencia", sequenceDB);
                                intent.putExtra("roboLinha", robotLine);
                                intent.putExtra("roboColuna", robotCollumn);
                                intent.putExtra("user", user);
                                intent.putExtra("levels", Levels);
                                intent.putExtra("activity", "MainActivity");
                                //intent.putIntegerArrayListExtra("pointsList",pointsList);

                                startActivity(intent);
                            }
                        });
                        Toast.makeText(getApplicationContext(), sequenceDB, Toast.LENGTH_LONG).show();
                    }


                }else{
                    engine.speak("Não entendi o que quiseste dizer! Repete por favor!", TextToSpeech.QUEUE_FLUSH, null, null);
                }


        }else if (Levels.equals("accelaration")) {


                if (speechResult.contains("sim")) {
                    engine.speak("sequência confirmada", TextToSpeech.QUEUE_FLUSH, null, null);
                    sequenceDB += "F";
                    if (!sequenceDB.equals("")) {
                        Handler mHandler = new Handler(getMainLooper());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, BoardDraw.class);
                                intent.putExtra("message", boardRec);
                                intent.putExtra("map", tabuleiro);
                                intent.putExtra("sequencia", sequenceDB);
                                intent.putExtra("roboLinha", robotLine);
                                intent.putExtra("roboColuna", robotCollumn);
                                intent.putExtra("user", user);
                                intent.putExtra("levels", Levels);
                                intent.putExtra("activity", "MainActivity");
                                startActivity(intent);
                            }
                        });
                    }

                } else if (speechResult.contains("não")) {
                    sequenceDB = "";
                    engine.speak("repete a sequência", TextToSpeech.QUEUE_FLUSH, null, null);
                    lauchSpeechRecognitionAccelaration();
                } else {
                    String confirmation = "";
                    String[] ArrayWordSpeech = speechResult.split(" ");
                    for (String s : ArrayWordSpeech) {

                        if (s.contains("esquerda")) {
                            sequenceDB += "E_";
                            confirmation += "esquerda,";

                        } else if (s.contains("direita")) {
                            sequenceDB += "D_";
                            confirmation += "direita,";

                        } else if (s.contains("frente") || s.contains("cima")) {
                            sequenceDB += "C_";
                            confirmation += "frente,";
                        }

                    }
                    engine.speak("RECEBIDO A SEQUÊNCIA" + confirmation + " confirmas a sequência?", TextToSpeech.QUEUE_FLUSH, null, null);
                    lauchSpeechRecognitionAccelaration();
                }
            }

                /*if (speechResult.contains("terminar ciclo")) {
                    sequenceDB += "LE_";
                    loop = 0;
                    engine.speak("o que queres que faça agora?", TextToSpeech.QUEUE_FLUSH, null, null);
                    lauchSpeechRecognition();
                } else if (speechResult.contains("ciclo")) {
                    sequenceDB += "LB_";
                    loop = 1;
                    engine.speak("queres executar o ciclo quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    lauchSpeechRecognition();
                } else if (speechResult.contains("direita") && loop == 1) {
                    sequenceDB += "D_";
                    //sequenceDB+= getRepeatedStringByTimesNumber("D_",getNumberOfTimes());
                    engine.speak("indica outra instrução ou indica terminar ciclo", TextToSpeech.QUEUE_FLUSH, null, null);
                    lauchSpeechRecognition();

                } else if (speechResult.contains("esquerda") && loop == 1) {
                    sequenceDB += "E_";
                    engine.speak("indica outra instrução ou indica terminar ciclo", TextToSpeech.QUEUE_FLUSH, null, null);
                    lauchSpeechRecognition();
                } else if ((speechResult.contains("cima") || speechResult.contains("frente")) && loop == 1) {
                    sequenceDB += "C_";
                    engine.speak("indica outra instrução ou indica terminar ciclo", TextToSpeech.QUEUE_FLUSH, null, null);
                    lauchSpeechRecognition();
                } else if ((speechResult.contains("baixo") || speechResult.contains("trás")) && loop == 1) {
                    sequenceDB += "B_";
                    engine.speak("indica outra instrução ou indica terminar ciclo", TextToSpeech.QUEUE_FLUSH, null, null);
                    lauchSpeechRecognition();
                } else if (speechResult.contains("direita")) {
                    instrucao = "D_";
                    engine.speak("queres ir para a direita quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    lauchSpeechRecognition();

                } else if (speechResult.contains("esquerda")) {
                    instrucao = "E_";
                    engine.speak("queres ir para a esquerda quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    lauchSpeechRecognition();
                } else if (speechResult.contains("cima") || speechResult.contains("frente")) {
                    instrucao = "C_";
                    engine.speak("queres ir para a frente quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    lauchSpeechRecognition();
                } else if (speechResult.contains("baixo") || speechResult.contains("trás")) {
                    instrucao = "B_";
                    engine.speak("queres ir para a baixo quantas vezes?", TextToSpeech.QUEUE_FLUSH, null, null);
                    lauchSpeechRecognition();
                } else if (speechResult.contains("vezes") || speechResult.contains("vez")) {

                    if (loop == 1) {
                        sequenceDB += getNumberOfTimes(speechResult) + "_";
                        engine.speak("diga uma instrução do loop", TextToSpeech.QUEUE_FLUSH, null, null);
                        lauchSpeechRecognition();

                    } else {
                        sequenceDB += getRepeatedStringByTimesNumber(instrucao, getNumberOfTimes(speechResult));
                        engine.speak("o que queres que faça agora?", TextToSpeech.QUEUE_FLUSH, null, null);
                        lauchSpeechRecognition();

                    }


                } else if (speechResult.contains("terminar")) {
                    sequenceDB += "F";
                    if (!sequenceDB.equals("")) {
                        Handler mHandler = new Handler(getMainLooper());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, BoardDraw.class);
                                intent.putExtra("message", boardRec);
                                intent.putExtra("map", tabuleiro);
                                intent.putExtra("sequencia", sequenceDB);
                                intent.putExtra("roboLinha", robotLine);
                                intent.putExtra("roboColuna", robotCollumn);
                                intent.putExtra("levels", Levels);
                                intent.putExtra("user", user);
                                intent.putExtra("activity", "MainActivity");
                                //intent.putIntegerArrayListExtra("pointsList",pointsList);

                                startActivity(intent);
                            }
                        });

                    }

                }else{
                    engine.speak("Não entendi o que quiseste dizer! Repete por favor!", TextToSpeech.QUEUE_FLUSH, null, null);
                    lauchSpeechRecognition();

                }*/
        }


        if ((resultCode == ERROR_NETWORK_TIMEOUT || requestCode == ERROR_NETWORK_TIMEOUT) && !terminar) {
            //Toast.makeText(getApplicationContext(), "timeout", Toast.LENGTH_LONG).show();
            Log.d("TIMEOUTSPEECH","TIMEOUT "+terminar);
            if (Levels.equals("accelaration")){

            }else{
                lauchSpeechRecognition();
            }
        }
        //


        super.onActivityResult(requestCode, resultCode, data);

    }


    private void lauchSpeechRecognition() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-PT");
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speach to text");
                speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, new Long(50000));
                speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, new Long(50000));
                startActivityForResult(speechIntent, RECOGNIZER_RESULT);

            }
        }, 8000);
    }

    private void lauchSpeechRecognitionAccelaration() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-PT");
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speach to text");
                speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, new Long(50000));
                speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, new Long(50000));
                startActivityForResult(speechIntent, RECOGNIZER_RESULT);

            }
        }, 5000);
    }


    private String getRepeatedStringByTimesNumber(String ins, int numberTimes) {
        String concat = "";
        for (int i = 0; i < numberTimes; i++) {
            concat += ins;
        }
        return concat;
    }


    private int getNumberOfTimes(String frase) {

        String[] splited = frase.split(" ");
        ArrayList<String> fraseArrayList = convertArrayToArrayList(splited);
        String[] Numbers = {"um", "dois", "três", "quatro", "cinco", "seis", "sete", "oito", "nove", "dez", "onze", "doze"
                , "treze", "quatorze", "quinze", "dezasseis", "dessassete", "dezoito", "dezanove", "vinte"};
        String[] NumberFormat = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
                , "13", "14", "15", "16", "17", "18", "19", "20"};

        if (fraseArrayList.contains("uma")) {
            return 1;
        } else if (fraseArrayList.contains("duas")) {
            return 2;
        } else {

            for (int i = 0; i < Numbers.length; i++) {
                if (fraseArrayList.contains(Numbers[i])) {
                    return converter(Numbers[i]);
                }

                if (fraseArrayList.contains(NumberFormat[i])) {
                    return Integer.parseInt(NumberFormat[i]);
                }

            }

        }


        return 0;


    }

    private int converter(String number) {

        switch (number) {
            case "um":
                return 1;
            case "dois":
                return 2;

            case "três":
                return 3;

            case "quatro":
                return 4;

            case "cinco":
                return 5;
            case "seis":
                return 6;

            case "sete":
                return 7;
            case "oito":
                return 8;
            case "nove":
                return 9;
            case "dez":
                return 10;
            case "onze":
                return 11;
            case "doze":
                return 12;
            case "treze":
                return 13;
            case "quatorze":
                return 14;
            case "quinze":
                return 15;
            case "dezasseis":
                return 16;
            case "dessassete":
                return 17;
            case "dezoito":
                return 16;
            case "dezanove":
                return 19;
            case "vinte":
                return 19;


        }
        return 0;
    }


    private ArrayList<String> convertArrayToArrayList(String[] array) {
        ArrayList<String> current = new ArrayList<>();

        for (String s : array) {
            current.add(s);
        }
        return current;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        gd.onTouchEvent(motionEvent);
        //Toast.makeText(getApplicationContext()," TOUCH",Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        //Toast.makeText(getApplicationContext(),"scroll",Toast.LENGTH_LONG).show();
/*
        if (menu < 5) {
            TaskMenu = new TimerTask() {
                @Override
                public void run() {

                    if (menu < 5) {
                        String menuVoz = "";
                        if (menu == 0) {
                            menuVoz = "Tens 3 opções de escolha:";
                        } else if (menu == 1) {
                            menuVoz = "Para o reconhecimento com voz carregue uma vez no ecrã";

                        } else if (menu == 2) {
                            menuVoz = "Para o reconhecimento com peças carregue duas vezes no ecrã";

                        } else if (menu == 3) {
                            menuVoz = "Para saber qual é a instrução a que corresponde a peça pressione o ecrã durante algum tempo";

                        }else if(menu == 4){
                            menuVoz = "Para ouvir novamente as instruções deslize o dedo para baixo no ecrã";
                        }

                        final String finalMenuVoz = menuVoz;
                        engine = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {


                                engine.setLanguage(new Locale("pt", "PT"));
                                engine.speak(finalMenuVoz,
                                        TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        });
                        menu++;
                    }else{
                        TaskMenu.cancel();
                    }
                }
            };
            timerMenu = new Timer("Timer");
            long delay = 1;
            long period = 5000;
            timerMenu.scheduleAtFixedRate(TaskMenu, delay, period);

        }*/
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        //Toast.makeText(getApplicationContext(),"LONG PRESS",Toast.LENGTH_LONG).show();
        int levelInt = indexLevelPoints();
        if (levelInt > 6 || Levels.equals("freestyle")) {
            takePicture();
            tutorialPieces = 1;
        }

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        //Toast.makeText(getApplicationContext(),"SINGLE TAP",Toast.LENGTH_LONG).show();
        int levelInt = indexLevelPoints();
        if (levelInt <= 6 || Levels.equals("freestyle")) {
            takePicture();
            board = 1;
        }

        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        //Toast.makeText(getApplicationContext(),"Double Tap",Toast.LENGTH_LONG).show();
        int levelInt = indexLevelPoints();
        if (levelInt > 6 || Levels.equals("freestyle")) {
            repeatedTask = new TimerTask() {
                @Override
                public void run() {

                    takePicture();
                }
            };
            timer = new Timer("Timer");
            long delay = 1;
            long period = 30000;
            timer.scheduleAtFixedRate(repeatedTask, delay, period);
            recPieces = 1;
        }

        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        //Toast.makeText(getApplicationContext(),"Double Tap",Toast.LENGTH_LONG).show();
        return true;
    }

    private String cameras(String[] id) {
        String c = "";
        for (String s : id) {
            c += s + " ";
        }
        return c;
    }


    public void ShowPopUpBoard() {
        String[] messageArray = boardAux.split(" ");
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity

            LayoutInflater inflater = (LayoutInflater) MainActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.cutompopup,
                    (ViewGroup) findViewById(R.id.popup_element));
            //Inflate the view from a predefined XML layout

            left = 50; // initial start position of rectangles (50 pixels from left)
            top = 50; // 50 pixels from the top
            width = 30;
            height = 30;
            Bitmap bg = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
            int index = 0;
            int col;
            for (int row = 0; row < 12; row++) { // draw 2 rows
                for (col = 0; col < 12; col++) { // draw 4 columns
                    Paint paint = new Paint();
                    if (messageArray[index].contains("O")) {
                        //Log.d("AA","O ,("+row+","+col+")");
                        //Log.d("AA","O"+index);
                        paint.setColor(Color.parseColor("#008000"));

                    } else if (messageArray[index].contains("X")) {
                        //Log.d("AA","X"+index);
                        //Log.d("AA","X ,("+row+","+col+")");

                        paint.setColor(Color.parseColor("#CD5C5C"));

                    } else if (messageArray[index].contains("F")) {
                        //Log.d("AA","F"+index);
                        //Log.d("AA","F ,("+row+","+col+")");


                        paint.setColor(Color.parseColor("#3792cb"));

                    } else if (messageArray[index].contains("R")) {
                        //Log.d("AA", "R"+index);
                        //Toast.makeText(getApplicationContext(),"RRRRRRR",Toast.LENGTH_LONG).show();
                        Log.d("AA", "R ,(" + row + "," + col + ")");
                        paint.setColor(Color.parseColor("#000000"));
                    }


                    Canvas canvas = new Canvas(bg);
                    canvas.drawRect(left, top, left + width, top + height, paint);
                    left = (left + width + 10); // set new left co-ordinate + 10 pixel gap
                    // Do other things here
                    // i.e. change colour
                    index++;
                }

                top = top + height + 10; // move to new row by changing the top co-ordinate
                left = 50;
            }

            RelativeLayout ll = (RelativeLayout) layout.findViewById(R.id.popup_element);
            ImageView iV = new ImageView(this);
            iV.setImageBitmap(bg);
            ll.addView(iV);

            // create a 300px width and 470px height PopupWindow
            pw = new PopupWindow(layout, 1110, 1110, true);
            // display the popup in the center
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);


            ImageView tv = layout.findViewById(R.id.cross);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pw.dismiss();
                    boardAux = "";
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void introSpeach() {


        engine = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                engine.setLanguage(new Locale("pt", "PT"));
            }
        });


        final Timer t = new Timer("Timer");
        taskTalk = new TimerTask() {
            public void run() {

                if (Levels.equals("level1")) {

                    if (speechCount == 0) {
                        engine.speak("Eu sou a torre de controlo do robô! O robô precisa da tua Ajuda!", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount == 1) {
                        engine.speak("Estou no planeta nabida, preciso de sair deste autêntico deserto! Ajuda-me", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount == 2) {
                        engine.speak("Ele não consegue andar sozinho sem as minhas instruções! MAS eu não consigo ver o caminho para lhe dizer ao robô! ", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount == 3) {
                        engine.speak("Preciso da tua ajudas para dizer o caminho ao robô! ", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount == 4) {
                        engine.speak("O robô está a pedir ajuda! Podes ajudar-nos a dizer o caminho ao robô? ", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount == 5) {
                        engine.speak("Achas que deva ir para a frente ou virar para a direita ou virar para a esquerda?", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount > 5) {

                        taskTalk.cancel();
                        t.cancel();
                    }
                    speechCount++;
                } else if (Levels.equals("level2")) {

                    if (speechCount == 0) {
                        engine.speak("O robot chegou ao planeta Dorlo!Oh nao!Está ser perseguido por darlianos maus! Ajuda-me a dizer-lhe o caminho correto para escapar!", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount == 1) {
                        engine.speak("Achas que deva ir para a frente ou virar para a direita ou virar para a esquerda?", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount > 1) {
                        taskTalk.cancel();
                        t.cancel();
                    }
                    speechCount++;

                } else if (Levels.equals("level3")) {

                    if (speechCount == 0) {
                        engine.speak("Estou no planeta yavin! Fui feito prisioneiro!Tenho que escapar desta prisão! Ajuda-me a sair daqui", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount == 1) {
                        engine.speak("Achas que deva ir para a frente ou virar para a direita ou virar para a esquerda?", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount > 1) {
                        taskTalk.cancel();
                        t.cancel();
                    }
                    speechCount++;

                } else if (Levels.equals("level4")) {

                    if (speechCount == 0) {
                        engine.speak("Estou no planeta xakiva! Preciso de encontrar amigos que se escondem por estas bandas!Ajuda-me a encontra-los", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount == 1) {
                        engine.speak("Achas que deva ir para a frente ou virar para a direita ou virar para a esquerda?", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount > 1) {
                        taskTalk.cancel();
                        t.cancel();
                    }
                    speechCount++;

                } else if (Levels.equals("level5")) {

                    if (speechCount == 0) { //ESTOU A  ULTRAPASSAR UMA CINTURA DE ASTEROIDES! AJUDA-ME! !
                        engine.speak("Estou a ultrapassar uma cintura de asteroides! ajuda-me!", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount == 1) {
                        engine.speak("Neste Nível é possível que tenhas de construir um ciclo", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount == 2) {
                        engine.speak("O ciclo é uma repetição de uma ou mais instruções", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount == 3) {
                        engine.speak("Então vamos iniciar", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount == 4) {
                        engine.speak("ACHAS QUE DEVA IR PARA A FRENTE OU VIRAR PARA A DIREITA OU  VIRAR  A ESQUERDA OU FAZER UM CICLO?", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount > 4) {
                        taskTalk.cancel();
                        t.cancel();
                    }
                    speechCount++;
                } else if (Levels.equals("freestyle")) {

                    if (speechCount == 0) { //ESTOU A  ULTRAPASSAR UMA CINTURA DE ASTEROIDES! AJUDA-ME! !
                        engine.speak("ACHAS QUE DEVA IR PARA A FRENTE OU VIRAR PARA A DIREITA OU  VIRAR  A ESQUERDA OU FAZER UM CICLO?", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount > 0) {
                        taskTalk.cancel();
                        t.cancel();
                    }
                    speechCount++;
                }else if (Levels.equals("accelaration")) {

                    if (speechCount == 0) {
                        engine.speak("Indica a sequência que queres executar!", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else if (speechCount > 0) {
                        taskTalk.cancel();
                        t.cancel();
                    }
                    speechCount++;
                }


            }
        };


        long delay = 10000;
        long period = 10000;

        t.scheduleAtFixedRate(taskTalk, delay, period);


    }

    private int getNumberOfWaitingLines() {
        int number = 0;

        if (Levels.equals("level1")) {
            number = 6;
        } else if (Levels.equals("level2")) {
            number = 2;
        } else if (Levels.equals("level3")) {
            number = 2;
        } else if (Levels.equals("level4")) {
            number = 2;
        } else if (Levels.equals("level5")) {
            number = 5;
        } else if (Levels.equals("freestyle")) {
            number = 1;
        }else if (Levels.equals("accelaration")) {
            number = 1;
        }
        return number;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_item_one);
        if (item.getTitle().equals("Camera")) {
            item.setTitle(user);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MainActivity.this, LevelsActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                return true;
            case R.id.action_item_one:
                Intent intent2 = new Intent(MainActivity.this, EditUser.class);
                intent2.putExtra("user", user);
                intent2.putExtra("activity", "MainActivity");
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void gameExplanation() {
        final int levelInt = indexLevelPoints();
        timerExplanation = new Timer("Timer");
        task = new TimerTask() {
            public void run() {
                if (explanationCount == 0) {
                    speech.speak("Bem vind ao Nível " + indexLevelPoints(), TextToSpeech.QUEUE_FLUSH, null, null);
                } else if (explanationCount == 1) {
                    speech.speak("Eu sou o botnik, e o robô é o ozobot!", TextToSpeech.QUEUE_FLUSH, null, null);
                } else if (explanationCount == 2) {
                    speech.speak("Ele precisa da tua ajuda para voltar para casa!", TextToSpeech.QUEUE_FLUSH, null, null);
                } else if (explanationCount == 3 && levelInt <= 6) {
                    speech.speak("Para iniciar o nível  carrega uma vez no ecrã!", TextToSpeech.QUEUE_FLUSH, null, null);
                } else if (explanationCount == 3 && levelInt > 6) {
                    speech.speak("Para iniciar o nível  carrega duas vezes no ecrã!", TextToSpeech.QUEUE_FLUSH, null, null);
                } else if (explanationCount == 4) {
                    speech.speak("Boa sorte nesta aventura!", TextToSpeech.QUEUE_FLUSH, null, null);
                } else if (explanationCount > 4) {

                    task.cancel();
                    timerExplanation.cancel();
                }
                explanationCount++;
            }
        };


        long delay = 3000;
        long period = 5000;

        timerExplanation.scheduleAtFixedRate(task, delay, period);


    }


    public void gameExplanationFreestyle() {
        final int levelInt = indexLevelPoints();
        timerExplanation = new Timer("Timer");
        task = new TimerTask() {
            public void run() {

                if (explanationCount == 0) {
                    if (Levels.equals("freestyle")) {
                        speech.speak("Bem vind ao Nível de estilo livre", TextToSpeech.QUEUE_FLUSH, null, null);
                    }else{
                        speech.speak("Bem vind ao Nível de modo acelarado", TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                } else if (explanationCount == 1) {
                    speech.speak("Eu sou o botnik, e o robô é o ozobot!", TextToSpeech.QUEUE_FLUSH, null, null);
                } else if (explanationCount == 2) {
                    speech.speak("Ele precisa da tua ajuda para voltar para casa!", TextToSpeech.QUEUE_FLUSH, null, null);
                } else if (explanationCount == 3) {
                    speech.speak("Para iniciar modo por voz  carrega uma vez no ecrã!", TextToSpeech.QUEUE_FLUSH, null, null);
                } else if (explanationCount == 4) {
                    speech.speak("Para iniciar modo por blocos  carrega duas vezes no ecrã!", TextToSpeech.QUEUE_FLUSH, null, null);
                } else if (explanationCount == 5) {
                    speech.speak("Para ouvir a que instrução corresponde o bloco pressiona o ecrã durante alguns segundos", TextToSpeech.QUEUE_FLUSH, null, null);
                } else if (explanationCount == 6) {
                    speech.speak("Boa sorte nesta aventura!", TextToSpeech.QUEUE_FLUSH, null, null);
                } else if (explanationCount > 6) {

                    task.cancel();
                    timerExplanation.cancel();
                }
                explanationCount++;
            }
        };


        long delay = 3000;
        long period = 5000;

        timerExplanation.scheduleAtFixedRate(task, delay, period);


    }


    private int indexLevelPoints() {
        int level = 0;
        if (Levels.equals("level1")) {
            level = 1;
        } else if (Levels.equals("level2")) {
            level = 2;
        } else if (Levels.equals("level3")) {
            level = 3;
        } else if (Levels.equals("level4")) {
            level = 4;
        } else if (Levels.equals("level5")) {
            level = 5;
        } else if (Levels.equals("level6")) {
            level = 6;
        } else if (Levels.equals("level7")) {
            level = 7;
        } else if (Levels.equals("level8")) {
            level = 8;
        } else if (Levels.equals("level9")) {
            level = 9;
        } else if (Levels.equals("level10")) {
            level = 10;
        } else if (Levels.equals("level11")) {
            level = 11;
        } else if (Levels.equals("level12")) {
            level = 12;
        }
        return level;

    }

    private int getValueOfLuminosity(File photo) {

        Mat matOriginal = Imgcodecs.imread(photo.getPath());
        org.opencv.core.Size s = matOriginal.size();
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap((int) s.width, (int) s.height, conf); // this creates a MUTABLE bitmap
        Canvas canvas = new Canvas(bmp);
        Utils.matToBitmap(matOriginal, bmp);
        int sumPixels = 0;
        for (int y = 0; y < bmp.getHeight(); y++) {
            for (int x = 0; x < bmp.getWidth(); x++) {
                int color = bmp.getPixel(x, y);
                sumPixels += (Color.red(color) + Color.green(color) + Color.blue(color)) / 3;

            }
        }

        int level = sumPixels / (int) (s.width * s.height);
        return level;
    }


    private String getImageString(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imagesBytes = baos.toByteArray();
        String encodedImage = android.util.Base64.encodeToString(imagesBytes, Base64.DEFAULT);
        return encodedImage;

    }

    private void savePhoto(Mat mat, File f) throws IOException {


        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, matOfByte);
        byte[] byteArray = matOfByte.toArray();

        //save method
        OutputStream outputStream2 = null;
        try {
            outputStream2 = new FileOutputStream(f);
            outputStream2.write(byteArray);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream2 != null)
                outputStream2.close();
        }
    }

    private void boardCorrection(List<Integer> coordenadas) {


        boolean condition = false;
        for (int i = coordenadas.size() - 1; i >= 1; i -= 2) {

            int linha = coordenadas.get(i - 1);
            int coluna = coordenadas.get(i);
            Log.d("ROBOTLINES", linha + "," + coluna);


            if ((coluna == 0 && linha == 0) || (coluna == 11 && linha == 0) || (coluna == 11 && linha == 11) || (coluna == 0 && linha == 11)) {
                condition = false;

            } else if (coluna == 0 && (linha != 11 || linha != 0)) {
                condition = (tabuleiro.get((linha) + "" + (coluna + 1)).equals("O") && tabuleiro.get((linha + 1) + "" + (coluna + 1)).equals("X") && tabuleiro.get((linha - 1) + "" + (coluna + 1)).equals("X"));
            } else if (coluna == 11 && (linha != 11 || linha != 0)) {
                condition = (tabuleiro.get((linha) + "" + (coluna - 1)).equals("O") && tabuleiro.get((linha + 1) + "" + (coluna - 1)).equals("X") && tabuleiro.get((linha - 1) + "" + (coluna - 1)).equals("X"));
            } else if (linha == 0 && (coluna != 11 || coluna != 0)) {
                condition = (tabuleiro.get((linha + 1) + "" + (coluna)).equals("O") && tabuleiro.get((linha + 1) + "" + (coluna - 1)).equals("X") && tabuleiro.get((linha + 1) + "" + (coluna + 1)).equals("X"));

            } else if (linha == 11 && (coluna != 11 || coluna != 0)) {
                condition = (tabuleiro.get((linha - 1) + "" + (coluna)).equals("O") && tabuleiro.get((linha - 1) + "" + (coluna - 1)).equals("X") && tabuleiro.get((linha - 1) + "" + (coluna + 1)).equals("X"));

            } else {
                condition = (tabuleiro.get((linha) + "" + (coluna + 1)).equals("O") && tabuleiro.get((linha + 1) + "" + (coluna + 1)).equals("X") && tabuleiro.get((linha - 1) + "" + (coluna + 1)).equals("X"))
                        || (tabuleiro.get((linha - 1) + "" + (coluna)).equals("O") && tabuleiro.get((linha - 1) + "" + (coluna + 1)).equals("X") && tabuleiro.get((linha - 1) + "" + (coluna - 1)).equals("X"))
                        || (tabuleiro.get((linha) + "" + (coluna - 1)).equals("O") && tabuleiro.get((linha - 1) + "" + (coluna - 1)).equals("X") && tabuleiro.get((linha + 1) + "" + (coluna - 1)).equals("X"))
                        || (tabuleiro.get((linha + 1) + "" + (coluna)).equals("O") && tabuleiro.get((linha + 1) + "" + (coluna - 1)).equals("X") && tabuleiro.get((linha + 1) + "" + (coluna + 1)).equals("X"));
            }

            if (condition) {
                tabuleiro.put(linha + "" + coluna, "R");
                robotLine = linha;
                robotCollumn = coluna;
                Log.d("ROBOTLINES", "ESTE = " + robotLine + "," + robotCollumn);
                break;
            }


        }


    }


    private String boardRecognitionTopCode() throws IOException {

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(getApplicationContext()));
        }

        if (Python.isStarted()) {
            //Toast.makeText(getApplicationContext(),"Python Started",Toast.LENGTH_LONG).show();
        }

        Python py = Python.getInstance();
        PyObject cv2 = py.getModule("cv2");
        PyObject numpy = py.getModule("numpy");


        Mat matrix = Imgcodecs.imread(file.getPath());


        PyObject vhlines = py.getModule("verticalAndHorizontal");
        TextView tv = findViewById(R.id.textoo);
        PyObject obj3 = vhlines.callAttr("squares", file.getPath());
        List<PyObject> squares = obj3.asList();


        Mat cropped = null;
        Mat croppedRobot = null;
        rec = "";
        int count = 0;
        Map<String, Integer> blackareas = new HashMap();
        tabuleiro = new HashMap();
        Mat croppedRobotFinal = new Mat();


        int linha = 0;

        for (int i = 0; i < squares.size(); i++) {
            int a = squares.get(i).asList().get(0).toInt();
            int b = squares.get(i).asList().get(1).toInt();
            int c = squares.get(i).asList().get(2).toInt();
            int d = squares.get(i).asList().get(3).toInt();


            Rect roi = new Rect(a, b, c - a, d - b);
            cropped = new Mat(matrix, roi);


            int w = c - a, h = d - b;


            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
            Canvas canvas = new Canvas(bmp);
            Utils.matToBitmap(cropped, bmp);

            Scanner scanTopcode = new Scanner();

            List<TopCode> topcodeCrop = scanTopcode.scan(bmp);


            if (topcodeCrop.isEmpty()) {
                tabuleiro.put("" + linha + "" + count, "O");
            } else {
                if (containTopCode(topcodeCrop, 793)) {
                    tabuleiro.put("" + linha + "" + count, "R");

                } else if (containTopCode(topcodeCrop, 713)) {
                    tabuleiro.put("" + linha + "" + count, "X");

                } else if (containTopCode(topcodeCrop, 681)) {
                    tabuleiro.put("" + linha + "" + count, "F");
                }

            }


            savePhoto(cropped, file);


            count++;
            if (count == 12) {
                rec += "\n";
                count = 0;
                linha++;
            }

        }

        rec = "";
        for (int i = 0; i < 12; i++) {

            for (int j = 0; j < 12; j++) {
                rec += tabuleiro.get("" + i + "" + j) + " ";


            }
            rec += "\n";

        }

        savePhoto(matrix, file);

        return rec;
    }


    private String getTopCodeCorners(File file) throws IOException {

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(getApplicationContext()));
        }

        if (Python.isStarted()) {
            //Toast.makeText(getApplicationContext(),"Python Started",Toast.LENGTH_LONG).show();
        }


        Mat matrixTop = Imgcodecs.imread(file.getPath());
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        Scanner scanTopcode = new Scanner();
        List<TopCode> topcodePhoto = scanTopcode.scan(bitmap);

        int topLeftX = 0;
        int topLeftY = 0;

        int topRightX = 0;
        int topRightY = 0;

        int bottomRightX = 0;
        int bottomRightY = 0;

        int bottomLeftX = 0;
        int bottomLeftY = 0;

        boolean topleft = false;
        boolean topRight = false;

        boolean bottomRight = false;
        boolean bottomLeft = false;

        int countCorners  = 0;

        for (TopCode t : topcodePhoto) {
            if (t.getCode() == 425) {
                Log.d("Topcodes", "TOP_LEFT (" + t.getCenterX() + " ," + t.getCenterY() + ")");
                topLeftX = (int) t.getCenterX();
                topLeftY = (int) t.getCenterY();
                topleft = true;
                countCorners++;

            } else if (t.getCode() == 805) {
                Log.d("Topcodes", "TOP_RIGHT (" + t.getCenterX() + " ," + t.getCenterY() + ")");
                topRight = true;
                topRightX = (int) t.getCenterX();
                topRightY = (int) t.getCenterY();
                countCorners++;

            }
            if (t.getCode() == 713) {
                Log.d("Topcodes", "BOTTOM_LEFT (" + t.getCenterX() + " ," + t.getCenterY() + ")");
                bottomLeft = true;
                bottomLeftX = (int) t.getCenterX();
                bottomLeftY = (int) t.getCenterY();
                countCorners++;

            }
            if (t.getCode() == 793) {
                Log.d("Topcodes", "BOTTOM_RIGHT (" + t.getCenterX() + " ," + t.getCenterY() + ")");
                bottomRightX = (int) t.getCenterX();
                bottomRightY = (int) t.getCenterY();
                bottomRight = true;
                countCorners++;
            }


        }

        int h = 0, w = 0;
        Mat cropped = null;

        if (topcodePhoto.size() <= 1 || (countCorners <= 1 && containMoreTopcodes(topcodePhoto))) {
            speech.speak( "códigos não reconhecidos",TextToSpeech.QUEUE_FLUSH, null, null);

        } else {

        int beginX = 0, beginY = 0;
        int endX = 0, endY = 0;
        if(topleft == true && bottomRight == true && topRight == true && bottomLeft == true){
            h = Math.abs(topLeftY - bottomRightY);
            w = Math.abs(topLeftX - bottomRightX);
            //Rect roi = new Rect(topLeftX, topLeftY, w, h);
            //cropped = new Mat(matrixTop, roi);
            Log.d("Topcodes", "h = " + h + " w = " + w);

            beginX = topLeftX;
            beginY = topLeftY;

            endX = bottomRightX;
            endY = bottomRightY;

        }
        else if (topleft == true && bottomRight == true && (topRight == false || bottomLeft == false)) {
            h = Math.abs(topLeftY - bottomRightY);
            w = Math.abs(topLeftX - bottomRightX);
            //Rect roi = new Rect(topLeftX, topLeftY, w, h);
            //cropped = new Mat(matrixTop, roi);
            Log.d("Topcodes", "h = " + h + " w = " + w);

            beginX = topLeftX;
            beginY = topLeftY;

            endX = bottomRightX;
            endY = bottomRightY;
        } else if ((topleft == false || bottomRight == false) && topRight == true && bottomLeft == true) {
            h = Math.abs(topRightY - bottomLeftY);
            w = Math.abs(bottomLeftX - topRightX);
            //Rect roi = new Rect(bottomLeftX, topRightY, w, h);
            //cropped = new Mat(matrixTop, roi);
            Log.d("Topcodes", "h = " + h + " w = " + w);

            beginX = bottomLeftX;
            beginY = topRightY;

            endX = topRightX;
            endY = bottomLeftY;
        } else if (topleft == true && topRight == true && bottomLeft == false && bottomRight == false) {
            h = Math.abs(topLeftX - topRightX);
            w = Math.abs(topLeftX - topRightX);

            Log.d("Topcodes", "h = " + h + " w = " + w);


            beginX = topLeftX;
            beginY = topLeftY;

            endX = bottomRightX;
            endY = bottomRightY + h;


        } else if (topleft == true && topRight == false && bottomLeft == true && bottomRight == false) {

            h = Math.abs(topLeftY - bottomLeftY);
            w = Math.abs(topLeftY - bottomLeftY);

            Log.d("Topcodes", "h = " + h + " w = " + w);


            beginX = topLeftX;
            beginY = topLeftY;

            endX = bottomLeftX + h;
            endY = bottomLeftY;


        } else if (topleft == false && topRight == true && bottomLeft == false && bottomRight == true) {

            h = Math.abs(topRightY - bottomRightY);
            w = Math.abs(topRightY - bottomRightY);

            Log.d("Topcodes", "h = " + h + " w = " + w);


            beginX = topRightX - h;
            beginY = topRightY;

            endX = bottomRightX;
            endY = bottomRightY;


        } else if (topleft == false && topRight == false && bottomLeft == true && bottomRight == true) {

            h = Math.abs(bottomLeftX - bottomRightX);
            w = Math.abs(bottomLeftX - bottomRightX);

            Log.d("Topcodes", "h = " + h + " w = " + w);


            beginX = bottomLeftX;
            beginY = bottomLeftY - h;

            endX = bottomRightX;
            endY = bottomRightY;


        }





          /*Bitmap.Config conf = Bitmap.Config.ARGB_8888;
          Bitmap bmp = Bitmap.createBitmap(w, h, conf);
          Canvas canvas = new Canvas(bmp);
          Utils.matToBitmap(cropped, bmp);
          savePhoto(cropped, file);*/
            savePhoto(matrixTop, file);

            Python py = Python.getInstance();
            PyObject vhlines = py.getModule("test");
            PyObject obj3 = vhlines.callAttr("squares", file.getPath(), beginX, beginY, endX, endY);
            List<PyObject> squares = obj3.asList();
            PyObject image = py.getModule("improvedImage");

            Mat matrixTopHistogram = new Mat();
            PyObject imageAlphaBeta = image.callAttr("automatic_brightness_and_contrast", file.getPath());
            List<PyObject> alphaBeta = imageAlphaBeta.asList();
            matrixTop.convertTo(matrixTopHistogram, -1, alphaBeta.get(0).toFloat(), alphaBeta.get(1).toFloat());
            savePhoto(matrixTopHistogram, file);


            Mat crop = null;
            rec = "";
            int count = 0;
            tabuleiro = new HashMap();
            Log.d("cropppp", squares.size() + "");


            int linha = 0;

            for (int i = 0; i < squares.size(); i++) {
                int a = squares.get(i).asList().get(0).toInt();
                int b = squares.get(i).asList().get(1).toInt();
                int c = squares.get(i).asList().get(2).toInt();
                int d = squares.get(i).asList().get(3).toInt();
                Log.d("cropppp", a + ", " + b + ", " + c + ", " + d);


                Rect roi1 = new Rect(a, b, c - a, d - b);
                crop = new Mat(matrixTopHistogram, roi1);
                // Mat cropBright =  new Mat();
                //crop.convertTo(cropBright, -1, 1, 50);


                int w1 = c - a, h1 = d - b;

                //Mat cropBright = new Mat();

                //crop.convertTo(cropBright,-1,1,50);

                Bitmap.Config conf2 = Bitmap.Config.ARGB_8888; // see other conf types
                Bitmap bmp2 = Bitmap.createBitmap(w1, h1, conf2); // this creates a MUTABLE bitmap
                Canvas canvas2 = new Canvas(bmp2);
                Utils.matToBitmap(crop, bmp2);


                int redColors = 0;
                int greenColors = 0;
                int blueColors = 0;
                int white = 0;

                int red = 0;
                int green = 0;
                int blue = 0;


                Scanner scanBegin = new Scanner();
                List<TopCode> topcodeBegin = scanBegin.scan(bmp2);


                for (int y = 0; y < bmp2.getHeight(); y++) {
                    for (int x = 0; x < bmp2.getWidth(); x++) {
                        int color = bmp2.getPixel(x, y);
                        redColors = Color.red(color);
                        greenColors = Color.green(color);
                        blueColors = Color.blue(color);

                        if (greenColors >= 150 && redColors >= 150 && blueColors >= 150) {
                            white++;
                        } else if ((greenColors >= redColors && greenColors >= blueColors)) {
                            green++;
                        } else if (redColors >= greenColors && redColors >= blueColors) {
                            red++;
                        } else if (blueColors >= redColors && blueColors >= greenColors) {
                            blue++;
                        }


                    }
                }
                Log.d("Topcodes", "(" + linha + " , " + count + ") =" + red + " , " + green + " , " + blue + " , " + white);


                if (white >= green && white >= blue && white >= red) {
                    Log.d("Topcodes", "(" + linha + " , " + count + ") = WHITTTTTTTTTE");
                    robotLine = linha;
                    robotCollumn = count;

                    tabuleiro.put("" + linha + "" + count, "R");


              /*if(!topcodeBegin.isEmpty()){
                if (topcodeBegin.get(0).code == 369 )
                    tabuleiro.put("" + linha + "" + count, "R");

              }else*/
                } else if ((green >= red && green >= blue)) {
                    tabuleiro.put("" + linha + "" + count, "O");
                    //Log.d("PIXEL","O");

                } else if (red >= green && red >= blue) {
                    tabuleiro.put("" + linha + "" + count, "F");
                    //Log.d("PIXEL","F");

                } else if (blue >= red && blue >= green) {
                    tabuleiro.put("" + linha + "" + count, "X");
                    //Log.d("PIXEL","X");

                }


                savePhoto(crop, file);

                Point inicio = new Point(a, b);
                Point fim = new Point(c, b);
                Imgproc.line(matrixTop, inicio, fim, new Scalar(255, 0, 0, 255));

                Point inicio1 = new Point(a, d);
                Point fim1 = new Point(c, d);
                Imgproc.line(matrixTop, inicio1, fim1, new Scalar(255, 0, 0, 255));

                Point inicio2 = new Point(a, b);
                Point fim2 = new Point(a, d);
                Imgproc.line(matrixTop, inicio2, fim2, new Scalar(255, 0, 0, 255));

                Point inicio3 = new Point(c, b);
                Point fim3 = new Point(c, d);
                Imgproc.line(matrixTop, inicio3, fim3, new Scalar(255, 0, 0, 255));


                count++;
                if (count == 12) {
                    rec += "\n";
                    count = 0;
                    linha++;
                }
            }


            for (int i = 0; i < 12; i++) {

                for (int j = 0; j < 12; j++) {
                    rec += tabuleiro.get("" + i + "" + j) + " ";


                }
                rec += "\n";

            }
        }
        savePhoto(matrixTop, file);

        return rec;


    }


    private boolean containTopCode(List<TopCode> topcodes, int codigo) {
        for (TopCode t : topcodes) {
            if (t.getCode() == codigo) {
                return true;
            }
        }
        return false;
    }

    private boolean containMoreTopcodes(List<TopCode> topcodePhoto){
        for (TopCode t : topcodePhoto) {
            if (t.getCode() != 425 || t.getCode() != 805 ||t.getCode() != 713 || t.getCode() != 793) {
                return true;
            }
        }

         return false;
    }



     private boolean containsSameNumberofLoopEndsAsBegins(String sequenceDB) {
        String[] ArraySequencia  = sequenceDB.split("_");
        int countLB = 0 ,countLE = 0;
        for (String s :ArraySequencia){
            if(s.equals("LB")){
                 countLB++;
            }

             if(s.equals("LE")){
                 countLE++;
             }

        }
         return countLB == countLE;
     }

}

