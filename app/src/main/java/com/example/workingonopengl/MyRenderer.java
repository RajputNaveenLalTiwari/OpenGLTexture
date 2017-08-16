package com.example.workingonopengl;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.Locale;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 2114 on 11-07-2017.
 */

public class MyRenderer implements GLSurfaceView.Renderer
{
    private static Context context;
    private int textures[] = new int[2];
    private Bitmap bitmap;
    private int bitmap_width,bitmap_height;
    private MyTextureOne myTextureOne;

    private EffectContext effectContext;
    private Effect effect;
    int view_port_width,view_port_height;

    static String appName = "My Application",imageName = "ForeGround";

    public MyRenderer(Context context)
    {
        super();
        this.context = context;
        bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.tom_and_jerry);
        bitmap_width = bitmap.getWidth();
        bitmap_height = bitmap.getHeight();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        view_port_width = width;
        view_port_height = height;
        GLES20.glViewport(0,0,width, height);
        GLES20.glClearColor(0,0,0,1);
        generateTexture();
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        if (effectContext == null)
        {
            effectContext = EffectContext.createWithCurrentGlContext();
        }

        if (effect != null)
        {
            effect.release();
        }

        documentaryEffect();
        myTextureOne.draw(textures[1]);

//        sendImage(view_port_width,view_port_height);
    }

    private void generateTexture()
    {
        GLES20.glGenTextures(2, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        myTextureOne = new MyTextureOne();
    }

    private void documentaryEffect()
    {
        EffectFactory effectFactory = effectContext.getFactory();
        effect = effectFactory.createEffect(EffectFactory.EFFECT_DOCUMENTARY);
        effect.apply(textures[0],bitmap_width,bitmap_height,textures[1]);


    }

    static void sendImage(int width, int height)
    {
        Bitmap resultBitmap =null;
        /*ByteBuffer rgbaBuf = ByteBuffer.allocateDirect(width * height * 4);
        rgbaBuf.position(0);
        long start = System.nanoTime();
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
                rgbaBuf);*/

        int[] array = new int[width * height];
        Buffer pixels = IntBuffer.wrap(array);
        pixels.flip();
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
                pixels);
        if (resultBitmap == null || resultBitmap.getWidth() != width || resultBitmap.getHeight() != height)
        {
            resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

        resultBitmap.setPixels(array, 0, width, 0, 0, width, height);

        Bitmap local_bitmap = null;
        Matrix matrix = new Matrix();
        matrix.preScale(1.0f, -1.0f);//Vertical_flip
//        matrix.preScale(-1.0f, 1.0f);//Horizontal_flip
        local_bitmap = Bitmap.createBitmap(resultBitmap,0,0,width,height,matrix,true);

        if (local_bitmap != null)
        {
            saveMyImage(local_bitmap);
        }
//        Buffer buffer =

//        long end = System.nanoTime();
//        Log.d("TryOpenGL", "glReadPixels: " + (end - start));
//        saveRgb2Bitmap(pixels, Environment.getExternalStorageDirectory().getAbsolutePath()
//                + "/gl_dump_" + width + "_" + height + ".png", width, height);
    }

    static void saveRgb2Bitmap(Buffer buf, String filename, int width, int height) {
        Log.d("TryOpenGL", "Creating " + filename);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(filename));
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bmp.copyPixelsFromBuffer(buf);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, bos);
            bmp.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static void saveMyImage(Bitmap toSave)
    {
        Bitmap bmImg = toSave;
        File filename;
        try {
            String path1 = android.os.Environment.getExternalStorageDirectory()
                    .toString();
            Log.i("in save()", "after mkdir");
            File file = new File(path1 + "/" + appName);
            if (!file.exists())
                file.mkdirs();
            filename = new File(file.getAbsolutePath() + "/" + imageName
                    + ".jpg");
            Log.i("in save()", "after file");
            FileOutputStream out = new FileOutputStream(filename);
            Log.i("in save()", "after outputstream");
            bmImg.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.i("in save()", "after outputstream closed");

            /*ContentValues image = getImageContent(filename);
            Uri result = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
            Toast.makeText(context,"File is Saved in  " + filename, Toast.LENGTH_SHORT).show();
            Log.e("URI", ""+filename.getAbsolutePath());*/


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static ContentValues getImageContent(File parent)
    {
        ContentValues image = new ContentValues();
        image.put(MediaStore.Images.Media.TITLE, appName);
        image.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
        image.put(MediaStore.Images.Media.DESCRIPTION, "App Image");
        image.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        image.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        image.put(MediaStore.Images.Media.ORIENTATION, 0);
        image.put(MediaStore.Images.ImageColumns.BUCKET_ID, parent.toString().toLowerCase(Locale.getDefault()).hashCode());
        image.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, parent.getName().toLowerCase(Locale.getDefault()));
        image.put(MediaStore.Images.Media.SIZE, parent.length());
        image.put(MediaStore.Images.Media.DATA, parent.getAbsolutePath());
        return image;
    }

}
