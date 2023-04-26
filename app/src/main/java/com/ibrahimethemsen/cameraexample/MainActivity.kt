package com.ibrahimethemsen.cameraexample

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.ibrahimethemsen.cameraexample.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var imageUri: Uri? = null

    //Take Picture
    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()){ isSuccess ->
        if (isSuccess){
            imageUri?.let {
                binding.imageView.setImageURI(it)
                imageWidthHeight(it)
                imageFileSize(it)
            }
        }else{
            println("bir hata oldu ")
        }
    }

    private val selectImageGallery = registerForActivityResult(ActivityResultContracts.GetContent()){uri ->
        uri?.let {
            binding.imageView.setImageURI(it)
            imageWidthHeight(it)
            imageFileSize(it)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        imageUri = createImageUri()!!
        binding.takePicture.setOnClickListener {
            takeImageResult.launch(imageUri)
        }
        binding.selectGallery.setOnClickListener {
            selectImageGallery.launch("image/*")
        }
    }

    private fun createImageUri(): Uri? {
        val image = File(applicationContext.filesDir, "camera_photo.png")
        return FileProvider.getUriForFile(
            applicationContext,
            "com.ibrahimethemsen.cameraexample",
            image
        )
    }
    private fun imageWidthHeight(selectUri: Uri){
        if (Build.VERSION.SDK_INT>=28){
            val source = ImageDecoder.createSource(contentResolver, selectUri)
            val bitmap2 = ImageDecoder.decodeBitmap(source)
            println("width 2 ${bitmap2.width}")
            println("height 2 ${bitmap2.height}")
        }else{
            val bitmap = MediaStore.Images.Media.getBitmap(
                contentResolver, selectUri
            )
            val width = bitmap.width
            val height = bitmap.height
            println("width $width")
            println("height $height")
        }
    }
    //Docs -> https://developer.android.com/training/secure-file-sharing/retrieve-info
    private fun imageFileSize(selectUri : Uri){
        contentResolver.query(selectUri, null, null, null, null)?.use {cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            val size = cursor.getLong(sizeIndex) //byte
            val sizeInMb = size.toDouble() / (1024 * 1024)
            cursor.close()
            println("name $nameIndex")
            println("size $sizeInMb")
        }
    }
}