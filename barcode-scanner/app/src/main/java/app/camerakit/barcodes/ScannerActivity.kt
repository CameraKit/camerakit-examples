package app.camerakit.barcodes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.camerakit.CameraKit
import com.camerakit.CameraKitFrame
import com.camerakit.CameraKitScanner
import com.camerakit.CameraKitView
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import java.nio.ByteBuffer

class ScannerActivity : Activity() {

    private val camera: CameraKitView by lazy {
        CameraKitView(this).apply {
            facing = CameraKit.FACING_BACK
            permissions = CameraKitView.PERMISSION_CAMERA
        }
    }

    private val zxingScanner = object : CameraKitScanner {
        private val hints: Map<DecodeHintType, Any> = mapOf(
            DecodeHintType.TRY_HARDER to true,
            DecodeHintType.POSSIBLE_FORMATS to arrayOf(BarcodeFormat.QR_CODE)
        )

        override fun scanFrame(scannerFrame: CameraKitFrame) {
            val source = PlanarYUVLuminanceSource(
                scannerFrame.frame,
                scannerFrame.width,
                scannerFrame.height,
                0,
                0,
                scannerFrame.width,
                scannerFrame.height,
                false
            )

            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            try {
                val result = QRCodeReader().decode(binaryBitmap, hints)
                val data = Intent().apply {
                    putExtra("barcode", result.text)
                }
                setResult(RESULT_OK, data)
                finish()
            } catch (e: Exception) {
                // nothing detected
            }

            scannerFrame.release()
        }
    }

    private val googleScanner = object : CameraKitScanner {
        private val detector: BarcodeDetector by lazy {
            BarcodeDetector.Builder(applicationContext)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build()
        }

        override fun scanFrame(scannerFrame: CameraKitFrame) {
            val frame = Frame.Builder()
                .setImageData(
                    ByteBuffer.wrap(scannerFrame.frame),
                    scannerFrame.width,
                    scannerFrame.height,
                    scannerFrame.format
                ).build()

            val results = detector.detect(frame)
            if (results.size() > 0) {
                for (i in 0..results.size()) {
                    val barcode = results.valueAt(i)
                    if (barcode != null) {
                        val code = barcode.rawValue
                        val data = Intent().apply {
                            putExtra("barcode", code)
                        }
                        setResult(RESULT_OK, data)
                        finish()
                        break
                    }
                }
            }

            scannerFrame.release()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(camera)

        // Need this if you want to use Google Vision API with Camera2 frames.
        CameraKitFrame.shouldConvertToNV21 = true

        val provider = intent.getStringExtra("provider")
        if ("zxing" == provider) {
            camera.setScanner(zxingScanner)
        } else if ("google" == provider) {
            camera.setScanner(googleScanner)
        } else {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        camera.onStart()
    }

    override fun onResume() {
        super.onResume()
        camera.onResume()
    }

    override fun onPause() {
        super.onPause()
        camera.onPause()
    }

    override fun onStop() {
        super.onStop()
        camera.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        camera.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}