package com.example.opm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;

import java.io.FileOutputStream;
import java.io.IOException;

public class PrintHelper {

    public static void PrintHelper(Context context, Bitmap bitmap) {
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);

        String jobName = "Print Bitmap";
        PrintDocumentAdapter printDocumentAdapter = new PrintDocumentAdapter() {
            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                PdfDocument pdfDocument = new PdfDocument();
                Paint paint = new Paint();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                Canvas canvas = page.getCanvas();
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(bitmap, 0, 0, paint);

                pdfDocument.finishPage(page);

                try {
                    pdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
                } catch (IOException e) {
                    callback.onWriteFailed(e.getMessage());
                    return;
                } finally {
                    pdfDocument.close();
                    callback.onWriteFinished(pages);
                }
            }

            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }

                PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder(jobName);
                PrintAttributes.Builder attributesBuilder = new PrintAttributes.Builder();
                attributesBuilder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE);
                attributesBuilder.setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0));
                PrintDocumentInfo pdi = builder.build();
                callback.onLayoutFinished(pdi, true);
            }
        };

        printManager.print(jobName, printDocumentAdapter, new PrintAttributes.Builder().build());
    }
}