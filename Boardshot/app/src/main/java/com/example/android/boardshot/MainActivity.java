package com.example.android.boardshot;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.speech.tts.TextToSpeech;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;



public class MainActivity extends AppCompatActivity {

    private Button btnCapture;
    private Button btnCaptureWithPieces;
    private Button btnToturial;
    private TextureView textureView;

    //Check state orientation of output image
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static{
        ORIENTATIONS.append(Surface.ROTATION_0,90);
        ORIENTATIONS.append(Surface.ROTATION_90,0);
        ORIENTATIONS.append(Surface.ROTATION_180,270);
        ORIENTATIONS.append(Surface.ROTATION_270,180);
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
    private boolean hasFinal =false;





    private StorageReference mStorageRef;

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
            cameraDevice=null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }

        if (Python.isStarted()){
            Toast.makeText(getApplicationContext(),"Python Started",Toast.LENGTH_LONG).show();
        }

        Python py = Python.getInstance();
        PyObject pyf = py.getModule("parameter");
        PyObject obj = pyf.callAttr("passParameter","WOW");

        Toast.makeText(getApplicationContext(),obj.toString(),Toast.LENGTH_LONG).show();




        database = FirebaseDatabase.getInstance().getReference("");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        textureView = (TextureView)findViewById(R.id.textureView);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);
        btnCapture = (Button)findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                takePicture();
                board = 1;

            }
        });

        btnCaptureWithPieces = (Button)findViewById(R.id.btnCapturePieces);
        btnCaptureWithPieces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 repeatedTask = new TimerTask() {
                    @Override
                    public void run() {

                        takePicture();
                    }
                };
                timer = new Timer("Timer");
                long delay = 1;
                long period = 5000;
                timer.scheduleAtFixedRate(repeatedTask, delay, period);
                recPieces = 1;
            }
        });

        btnToturial = (Button)findViewById(R.id.Tutorial);
        btnToturial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
                tutorialPieces = 1;
            }
        });






    }

    private void takePicture() {
        if(cameraDevice == null)
            return;
        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try{
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if(characteristics != null)
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        .getOutputSizes(ImageFormat.JPEG);

            //Capture image with custom size
            int width = 640;
            int height = 480;
            if(jpegSizes != null && jpegSizes.length > 0)
            {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            final ImageReader reader = ImageReader.newInstance(width,height,ImageFormat.JPEG,1);
            List<Surface> outputSurface = new ArrayList<>(2);
            outputSurface.add(reader.getSurface());
            outputSurface.add(new Surface(textureView.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            //Check orientation base on device
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATIONS.get(rotation));

            file = new File(Environment.getExternalStorageDirectory()+"/"+UUID.randomUUID().toString()+".jpg");
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    Image image = null;
                    try{
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);

                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally {
                        {
                            if(image != null)
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

                    if (recPieces == 1) {
                        Toast.makeText(MainActivity.this, "recPieces", Toast.LENGTH_SHORT).show();
                        List<TopCode> listaTopCodes = recognizeTopcodes(file.getPath());
                        if (!listaTopCodes.isEmpty() && hasFinalCode(listaTopCodes)) {
                            hasFinal = true;
                            Toast.makeText(MainActivity.this, "!Empty && final code", Toast.LENGTH_SHORT).show();
                            for (Integer t : instructionsRecognition(listaTopCodes)) {

                                if (t == 31) {
                                    sequenceDB += "E_";
                                } else if (t == 47) {
                                    sequenceDB += "D_";
                                } else if (t == 55) {
                                    sequenceDB += "C_";
                                } else if (t == 59) {
                                    sequenceDB += "B_";
                                } else if (t == 107) {
                                    sequenceDB += "F";
                                    Toast.makeText(MainActivity.this, "FIMMMMMMM", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }


                    }
                    if (tutorialPieces == 1) {
                        tutorialPieces = 0;
                        List<TopCode> listaTopCodes = recognizeTopcodes(file.getPath());
                        if (!listaTopCodes.isEmpty()) {
                            for (TopCode t : listaTopCodes) {
                                Toast.makeText(MainActivity.this, "Topcode = "+t.code, Toast.LENGTH_SHORT).show();
                                if (t.code == 31) {
                                    instruction = "Esquerda";

                                } else if (t.code == 47) {
                                    instruction = "Direita";

                                } else if (t.code == 55) {
                                    instruction = "Cima";

                                } else if (t.code == 59) {
                                    instruction = "Baixo";

                                } else if (t.code == 107) {
                                    instruction = "Fim";

                                }
                            }

                            engine = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status == TextToSpeech.SUCCESS){
                                        engine.setLanguage(new Locale("pt", "PT"));
                                        engine.speak("A peça corresponde à instrução "+instruction,
                                                TextToSpeech.QUEUE_FLUSH, null, null);
                                    }

                                }
                            });




                        }
                    }

                    if (recPieces == 1 || board == 1) {

                        if (recPieces ==1 && hasFinal){
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
                            database.child("sequencia").setValue(sequenceDB);
                            recPieces = 0;
                            sequenceDB ="";
                        }


                        board = 0;

                        Toast.makeText(MainActivity.this, "saved", Toast.LENGTH_SHORT).show();

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

            reader.setOnImageAvailableListener(readerListener,mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(MainActivity.this, "Saved "+file, Toast.LENGTH_SHORT).show();

                    createCameraPreview();
                }
            };

            cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try{
                        cameraCaptureSession.capture(captureBuilder.build(),captureListener,mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            },mBackgroundHandler);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreview() {
        try{
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(),imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if(cameraDevice == null)
                        return;
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(MainActivity.this, "Changed", Toast.LENGTH_SHORT).show();
                }
            },null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if(cameraDevice == null)
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,CaptureRequest.CONTROL_MODE_AUTO);
        try{
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(),null,mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void openCamera() {
        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try{
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            //Check realtime permission if run higher API 23
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId,stateCallback,null);

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
        if(requestCode == REQUEST_CAMERA_PERMISSION)
        {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "You can't use camera without permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if(textureView.isAvailable())
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
        try{
            mBackgroundThread.join();
            mBackgroundThread= null;
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




    private void deleteAllPhotos(){
        File cameraFolder = new File(CAMERA_PATH);

        if (cameraFolder.isDirectory()){
            File[] photos = cameraFolder.listFiles();
            if(photos != null) {
                String concatNameFiles = "";
                for (File image : photos) {
                    image.delete();

                }

            }else{
                Toast.makeText(getApplicationContext(), "Não contém fotos", Toast.LENGTH_LONG).show();
            }

        }

    }


    private String getLastPhotoTaken(){

        File cameraFolder = new File(CAMERA_PATH);

        Map<String,Date> photoDates = new HashMap<>();

        if (cameraFolder.isDirectory()){
            File[] photos = cameraFolder.listFiles();
            if(photos != null) {
                String concatNameFiles = "";
                for (File image : photos) {
                    Date date = new Date(image.lastModified());
                    photoDates.put(image.getName(),date);
                }


            }else{
                Toast.makeText(getApplicationContext(), "Não contém fotos", Toast.LENGTH_LONG).show();
            }

        }

        return getLastName(photoDates);
    }



    private static String getLastName(@NonNull Map<String, Date> photoDates){

        String lastPhoto = "";
        Date lastDate = new Date("05/05/1970");
        for (Map.Entry<String,Date> entry1 : photoDates.entrySet()) {
            if (!(entry1.getKey().equals("cache")) && entry1.getValue().after(lastDate) ){
                lastDate = entry1.getValue();
                lastPhoto = entry1.getKey();
            }
        }
        return  lastPhoto;
    }



    private List<TopCode> recognizeTopcodes(String nameLastPhoto){
        Toast.makeText(getApplicationContext(),"path ="+ nameLastPhoto, Toast.LENGTH_LONG).show();

        List<TopCode> lista = null;
        Bitmap bmImg = BitmapFactory.decodeFile(nameLastPhoto);
        if (bmImg != null) {
            Scanner sc = new Scanner();
            lista = sc.scan(bmImg);
            if (lista.isEmpty())
                Toast.makeText(getApplicationContext(), "lista de scan vazia", Toast.LENGTH_LONG).show();
            String codes = "";
            for (TopCode t : lista) {
                codes += t.getCode() + " "+"x = "+t.getCenterX()+ " "+"y = "+t.getCenterY()+"\n";
            }
            //Toast.makeText(getApplicationContext(), codes, Toast.LENGTH_LONG).show();
        }else{

            Toast.makeText(getApplicationContext(), "Não tem fotos tiradas ou luz pode não permitir o reconhecimento do topcode", Toast.LENGTH_LONG).show();

        }
        return  lista;
    }

    private boolean hasFinalCode(List<TopCode> lt){

        for (TopCode t : lt){

            if (t.getCode() == 107 ){
                return true;
            }
        }

        return false;
    }

    private List<Integer> instructionsRecognition(List<TopCode> listaTopCodes){

        String finalInstructions= "";
        List<Float> sortYCoordenate = new ArrayList<>();
        List<Integer> TopcodeInstructions = new ArrayList<>();
        if (listaTopCodes!= null) {
            for (TopCode t : listaTopCodes) {
                sortYCoordenate.add(t.getCenterY());

            }
            Collections.sort(sortYCoordenate);
            for (float f : sortYCoordenate){
                for (TopCode t:listaTopCodes){
                    if (f == t.getCenterY()){
                        TopcodeInstructions.add(t.getCode());
                    }
                }
            }


        }else{
            Toast.makeText(getApplicationContext(), "a lista está vazia", Toast.LENGTH_LONG).show();

        }


        return TopcodeInstructions;
    }





}
