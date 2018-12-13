package app.camerakit.barcodes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    companion object {
        private const val ZXING_REQUEST_CODE = 313
        private const val GOOGLE_REQUEST_CODE = 519
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.zxingButton).setOnClickListener {
            val intent = Intent(this, ScannerActivity::class.java)
                .apply {
                    putExtra("provider", "zxing")
                }
            startActivityForResult(intent, ZXING_REQUEST_CODE)
        }

        findViewById<Button>(R.id.googleButton).setOnClickListener {
            val intent = Intent(this, ScannerActivity::class.java)
                .apply {
                    putExtra("provider", "zxing")
                }
            startActivityForResult(intent, GOOGLE_REQUEST_CODE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ZXING_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            findViewById<EditText>(R.id.zxingEditText).apply {
                setText(data?.getStringExtra("barcode"))
            }
        }
        if (requestCode == GOOGLE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            findViewById<EditText>(R.id.googleEditText).apply {
                setText(data?.getStringExtra("barcode"))
            }
        }
    }


}
