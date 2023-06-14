package com.adrian.recycash.ui.home.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.adrian.recycash.R
import com.adrian.recycash.databinding.ActivityScanBinding
import com.adrian.recycash.helper.createCustomTempFile
import com.adrian.recycash.helper.uriToFile
import com.google.android.material.snackbar.Snackbar
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private lateinit var tfliteInterpreter: Interpreter
    private val tfliteModel: MappedByteBuffer by lazy { loadModel() }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    R.string.error_permission,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        val toolbar = binding.toolbarScan
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        // take img from gallery/camera
        binding.btnCamera.setOnClickListener { startTakePhoto() }
        binding.btnGallery.setOnClickListener { startGallery() }

        val mappedByteBuffer = loadModel()
        val tfliteOptions = Interpreter.Options()

        tfliteInterpreter = Interpreter(mappedByteBuffer, tfliteOptions)

        binding.btnScan.setOnClickListener {
            showProgressBar(true)
            val inputBuffer = imgToByteBuffer(binding.imgBottle)
            Log.d(TAG, "inputBuffer: $inputBuffer")

            for (i in 0 until 10) {
                val floatValue = inputBuffer.getFloat(i * 4) // Each float occupies 4 bytes
                Log.d(TAG, "Input value at position $i: $floatValue")
            }
            val result = runInference(inputBuffer, tfliteInterpreter)

            if (result == "Bottle"){
                showProgressBar(false)
                Toast.makeText(this, getString(R.string.scan_success), Toast.LENGTH_SHORT).show()
                val intentScanOk = Intent(this@ScanActivity, PlasticTypeActivity::class.java)
                startActivity(intentScanOk)
                finish()
            } else {
                showProgressBar(false)
                Snackbar.make(binding.root, getString(R.string.scan_failed), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProgressBar(value: Boolean) {
        binding.progressBar.visibility = if (value) View.VISIBLE else View.GONE
    }

    private fun loadModel(): MappedByteBuffer {
        val assetManager = this.assets
        val modelDescriptor = assetManager.openFd("trained_model.tflite")
        val modelFileDescriptor = modelDescriptor.fileDescriptor
        val startOffset = modelDescriptor.startOffset
        val declaredLength = modelDescriptor.declaredLength

        val fileChannel = FileInputStream(modelFileDescriptor).channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            startOffset,
            declaredLength
        )
    }

    private fun imgToByteBuffer(imgBottle: ImageView): ByteBuffer {
        if (!::tfliteInterpreter.isInitialized) {
            tfliteInterpreter = Interpreter(tfliteModel)
        }
        val bitmap = (imgBottle.drawable as BitmapDrawable).bitmap
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, false)

        val inputShape = tfliteInterpreter.getInputTensor(0).shape()
        val inputSize = inputShape[1] * inputShape[2] * inputShape[3]

        val inputBuffer = ByteBuffer.allocateDirect(inputSize * 4)
        inputBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(resizedBitmap.width * resizedBitmap.height)
        resizedBitmap.getPixels(pixels, 0, resizedBitmap.width, 0, 0, resizedBitmap.width, resizedBitmap.height)
        for (pixel in pixels) {
            inputBuffer.putFloat((pixel shr 16 and 0xFF).toFloat() / 255.0f)
            inputBuffer.putFloat((pixel shr 8 and 0xFF).toFloat() / 255.0f)
            inputBuffer.putFloat((pixel and 0xFF).toFloat() / 255.0f)
        }
        inputBuffer.rewind()

        return inputBuffer
    }

    private fun runInference(inputBuffer: ByteBuffer, tfliteInterpreter: Interpreter): String {
        // Create an output buffer to store the prediction result.
        val outputShape = tfliteInterpreter.getOutputTensor(0).shape()
        val outputSize = outputShape[1]
        val outputBuffer = ByteBuffer.allocateDirect(outputSize * 4) // Assuming output size is 4 bytes.
        outputBuffer.order(ByteOrder.nativeOrder())

        // Run the inference using the input and output buffers.
        tfliteInterpreter.run(inputBuffer, outputBuffer)

        outputBuffer.rewind()
        val predictionLabel = outputBuffer.float
        Log.d(TAG, "prediction: $predictionLabel")

        // Continue with further processing or display the result.
        val threshold = 0.5 // Adjust the threshold based on your needs

        val label = if (predictionLabel >= threshold) {
            // Predicted as the positive class (e.g., a bottle)
            "Bottle"
        } else {
            // Predicted as the negative class (e.g., not a bottle)
            "Not a Bottle"
        }

        Log.d(TAG, "Prediction Label: $label")

        return label
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@ScanActivity,
                "com.adrian.recycash",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val myFile = File(currentPhotoPath)

                myFile.let { file ->
                    getFile = file
                    binding.imgBottle.setImageBitmap(BitmapFactory.decodeFile(file.path))
                }
            }
        }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImg = result.data?.data as Uri
                selectedImg.let { uri ->
                    val myFile = uriToFile(uri, this@ScanActivity)
                    getFile = myFile
                    binding.imgBottle.setImageURI(uri)
                }
            }
        }

    companion object {
        private const val TAG = "ScanActivity"

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
