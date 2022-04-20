package jp.techacademy.yuuwa.takano.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.net.Uri
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    var mulist = mutableListOf<Uri>()
    var num = 0
    val PERMISSIONS_REQUEST_CODE = 100
    private var mTimer: Timer? = null
    private var mTimerSec = 0.0
    private var mHandler = Handler()
    private var playnum = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }


        play.setOnClickListener { //スライドショー
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // タイマーの作成
                    play.text = "停止"
                    next.isClickable = false
                    back.isClickable = false
                    if (playnum == 0){
                        next.isClickable = false
                        back.isClickable = false
                        playnum = 1
                        mTimer = Timer()
                        mTimer!!.schedule(object : TimerTask() {
                            override fun run() {
                                mTimerSec += 0.1
                                mHandler.post {
                                    Photo1.setImageURI(mulist[num])
                                    num++
                                    if (num == mulist.size) {
                                        num = 0
                                    }
                                }
                            }
                        }, 2000, 2000)

                    }else if (playnum == 1){    //停止
                        mTimer!!.cancel()
                        playnum = 0
                        play.text = "再生"
                        next.isClickable = true
                        back.isClickable = true
                    }
                }
                } else {
                    // 許可されていないので許可ダイアログを表示する
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
                }
            }


        next.setOnClickListener { //進む
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Photo1.setImageURI(mulist[num])

                    if (num == mulist.size-1) {
                        num = 0
                    }else {
                        num++
                    }
                } else {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
                }
            }

        }

        back.setOnClickListener { //戻る
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    if (num == 0) {
                        num = mulist.size-1
                        Photo1.setImageURI(mulist[num])
                    }else{
                        num--
                        Photo1.setImageURI(mulist[num])
                    }

                } else {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
                }
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {

        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }


    private fun getContentsInfo() {
        // 画像の情報を取得する
            val resolver = contentResolver
            val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目（null = 全項目）
                null, // フィルタ条件（null = フィルタなし）
                null, // フィルタ用パラメータ
                null // ソート (nullソートなし）
            )
            if (cursor!!.moveToFirst()) {
                do {
                    // 配列にフォルダの画像を挿入
                    val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(fieldIndex)
                    val  imageUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    mulist.add(num,imageUri)
                    num ++
                    }while (cursor.moveToNext())
                        cursor.close()
                    }
                num = 0
        }
    }
