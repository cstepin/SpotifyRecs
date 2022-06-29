package com.example.spotifyrecs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;


public class ExportActivity extends AppCompatActivity {
    private Module mModule = null;
    private Bitmap mBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        try {
            mModule = LiteModuleLoader.load(ExportActivity.assetFilePath(getApplicationContext(), "model.ptl"));
        } catch (IOException e) {
            Log.e("ImageSegmentation", "Error reading assets", e);
            finish();
        }

        run(mModule);
    }

    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

    public static void run(Module mModule){
        /*
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(mBitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB);
        final float[] inputs = inputTensor.getDataAsFloatArray();
         */

       // Tensor input = Tensor.fromBlob(data, new long[]{1, data.length});
//
//        IValue input = IValue.from(Tensor.fromBlob(Tensor.allocateByteBuffer(1), new long[] {1}));
//        IValue output = mModule.forward(input);

        IValue input = IValue.from(Tensor.fromBlob(Tensor.allocateByteBuffer(1), new long[] {1}));
        IValue output = mModule.forward(input);

        //double outTensors = mModule.forward(IValue.from(String.valueOf(input))).toDouble();

        Log.i("In export activity", "everything runs correctly: " + output);
    }
}