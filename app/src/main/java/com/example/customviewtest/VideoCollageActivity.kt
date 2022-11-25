package com.example.customviewtest

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.example.customviewtest.databinding.ActivityVideoCollageBinding
import org.jcodec.api.android.AndroidSequenceEncoder
import org.jcodec.common.io.NIOUtils
import org.jcodec.common.io.SeekableByteChannel
import org.jcodec.common.model.Rational
import java.io.File


class VideoCollageActivity : AppCompatActivity() {

    private var path1 = "android.resource://" + packageName + "/" + R.raw.video1
    private var path2 = "android.resource://" + packageName + "/" + R.raw.video2
    private var path3 = "android.resource://" + packageName + "/" + R.raw.video3
    private var path4 = "android.resource://" + packageName + "/" + R.raw.video4

    private var bmp1: Bitmap? = null
    private var bmp2: Bitmap? = null
    private var bmp3: Bitmap? = null
    private var bmp4: Bitmap? = null

    private lateinit var m1: MediaMetadataRetriever
    private lateinit var m2: MediaMetadataRetriever
    private lateinit var m3: MediaMetadataRetriever
    private lateinit var m4: MediaMetadataRetriever

    private var maxTime = 0L;


    private lateinit var handler: Handler

    private var out: SeekableByteChannel? = null;


    private lateinit var binding: ActivityVideoCollageBinding

    private lateinit var vid1: ZoomableTextureView
    private lateinit var vid2: ZoomableTextureView
    private lateinit var vid3: ZoomableTextureView
    private lateinit var vid4: ZoomableTextureView

    val delay = (100 / 3).toLong()

    var time = delay

    private lateinit var encoder: AndroidSequenceEncoder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCollageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        path1 = "android.resource://" + packageName + "/" + R.raw.video1
        path2 = "android.resource://" + packageName + "/" + R.raw.video2
        path3 = "android.resource://" + packageName + "/" + R.raw.video3
        path4 = "android.resource://" + packageName + "/" + R.raw.video4

        vid1 = ZoomableTextureView(this, R.raw.video1).apply {
            id = R.id.video_1
        }
        vid2 = ZoomableTextureView(this, R.raw.video2).apply {
            id = R.id.video_2
        }
        vid3 = ZoomableTextureView(this, R.raw.video3).apply {
            id = R.id.video_3
        }
        vid4 = ZoomableTextureView(this, R.raw.video4).apply {
            id = R.id.video_4
        }

        binding.collageContainer.apply {
            addView(vid1)
            addView(vid2)
            addView(vid3)
            addView(vid4)
        }


        (vid1.layoutParams as ConstraintLayout.LayoutParams).apply {
            leftToLeft = binding.collageContainer.id
            topToTop = binding.collageContainer.id
            rightToLeft = vid2.id
            bottomToTop = vid3.id
        }

        (vid2.layoutParams as ConstraintLayout.LayoutParams).apply {
            leftToRight = vid1.id
            topToTop = binding.collageContainer.id
            rightToRight = binding.collageContainer.id
            bottomToTop = vid4.id
        }

        (vid3.layoutParams as ConstraintLayout.LayoutParams).apply {
            leftToLeft = binding.collageContainer.id
            topToBottom = vid1.id
            rightToLeft = vid4.id
            bottomToBottom = binding.collageContainer.id
        }

        (vid4.layoutParams as ConstraintLayout.LayoutParams).apply {
            leftToRight = vid3.id
            topToBottom = vid2.id
            rightToRight = binding.collageContainer.id
            bottomToBottom = binding.collageContainer.id
        }

        binding.nextButton.setOnClickListener {
            extractFrames()
        }

        MediaPlayer.create(this, Uri.parse(path1)).also {
            maxTime = maxOf(maxTime, it.duration.toLong())
        }

        MediaPlayer.create(this, Uri.parse(path2)).also {
            maxTime = maxOf(maxTime, it.duration.toLong())
        }

        MediaPlayer.create(this, Uri.parse(path3)).also {
            maxTime = maxOf(maxTime, it.duration.toLong())
        }

        MediaPlayer.create(this, Uri.parse(path4)).also {
            maxTime = maxOf(maxTime, it.duration.toLong())
        }

        m1 = MediaMetadataRetriever().apply {
            setDataSource(
                this@VideoCollageActivity,
                Uri.parse(path1)
            )
        }

        m2 = MediaMetadataRetriever().apply {
            setDataSource(
                this@VideoCollageActivity,
                Uri.parse(path2)
            )
        }
        m3 = MediaMetadataRetriever().apply {
            setDataSource(
                this@VideoCollageActivity,
                Uri.parse(path3)
            )
        }
        m4 = MediaMetadataRetriever().apply {
            setDataSource(
                this@VideoCollageActivity,
                Uri.parse(path4)
            )
        }

    }

    private fun extractFrames() {

        try {
            val path = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
                Environment.getExternalStorageDirectory().absolutePath + "/"
            else
                this.externalMediaDirs?.first()!!.absolutePath + "/"

            val file = File("$path/collage_export.mp4")

            if (file.exists()) {
                file.delete()
            }

            out = NIOUtils.writableFileChannel(file.path);
            encoder = AndroidSequenceEncoder(out, Rational.R(15, 1));


            Thread {

                Log.d("collagePOC", "before loop: $time $maxTime")

                while (time < maxTime) {

                    Log.d("collagePOC", "in loop: $time")

                    bmp1 = m1.getFrameAtTime(time)
                    bmp2 = m2.getFrameAtTime(time)
                    bmp3 = m3.getFrameAtTime(time)
                    bmp4 = m4.getFrameAtTime(time)

                    val w = (binding.collageContainer.width / 2) * 2
                    val h = (binding.collageContainer.height / 2) * 2

                    val bmp = Bitmap.createBitmap(
                        w,
                        h,
                        Bitmap.Config.ARGB_8888
                    )

                    val canvas = Canvas(bmp)

                    canvas.setMatrix(binding.collageContainer.matrix)

                    bmp1?.let { canvas.drawBitmap(it, vid1.matrix, Paint()) }
                    bmp2?.let { canvas.drawBitmap(it, vid2.matrix, Paint()) }
                    bmp3?.let { canvas.drawBitmap(it, vid3.matrix, Paint()) }
                    bmp4?.let { canvas.drawBitmap(it, vid4.matrix, Paint()) }

//                    encoder.encodeImage(bmp)

                    time += delay
                }

                encoder.finish()
                NIOUtils.closeQuietly(out);
                Looper.myLooper()?.quit()

                Log.d("collagePOC", " thread finished")
            }.start()

        } catch (e: Exception) {
            Log.d("collagePOC", "extraction failed $e")
            encoder.finish()
            NIOUtils.closeQuietly(out);
        }


    }

    override fun onDestroy() {
        binding.collageContainer.children.forEach {
            (it as ZoomableTextureView)
            it.mediaPlayer.stop()
        }
        super.onDestroy()
    }
}