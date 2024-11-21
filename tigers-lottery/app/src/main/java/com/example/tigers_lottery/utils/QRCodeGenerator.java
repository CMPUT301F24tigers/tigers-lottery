package com.example.tigers_lottery.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeGenerator {
    public Bitmap generateQRCode(String eventId){
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(eventId, BarcodeFormat.QR_CODE, 300,300);
            Bitmap bitmap = Bitmap.createBitmap(300,300,Bitmap.Config.RGB_565);
            for(int x=0; x<300;x++){
                for(int y=0;y<300;y++){
                    bitmap.setPixel(x,y,bitMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e){
            e.printStackTrace();
            return null;
        }
    }
}
