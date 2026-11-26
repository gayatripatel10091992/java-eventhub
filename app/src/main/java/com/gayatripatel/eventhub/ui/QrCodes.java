package com.gayatripatel.eventhub.ui;

import android.graphics.Bitmap;
import android.util.LruCache;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.util.EnumMap;
import java.util.Map;

/**
 * Renders ticket QR codes with ZXing. Bitmaps are cached by payload in a small LRU sized in bytes, so scrolling
 * the ticket list doesn't regenerate or re-allocate them.
 */
final class QrCodes {
    private static final LruCache<String, Bitmap> CACHE = new LruCache<String, Bitmap>(4 * 1024 * 1024) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    private QrCodes() {
    }

    static Bitmap render(String payload, int sizePx) {
        String key = payload + "@" + sizePx;
        Bitmap cached = CACHE.get(key);
        if (cached != null) return cached;
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 0);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            BitMatrix m = new QRCodeWriter().encode(payload, BarcodeFormat.QR_CODE, sizePx, sizePx, hints);
            int[] pixels = new int[sizePx * sizePx];
            for (int y = 0; y < sizePx; y++) {
                int row = y * sizePx;
                for (int x = 0; x < sizePx; x++) pixels[row + x] = m.get(x, y) ? 0xFF111111 : 0xFFFFFFFF;
            }
            Bitmap bitmap = Bitmap.createBitmap(pixels, sizePx, sizePx, Bitmap.Config.RGB_565);
            CACHE.put(key, bitmap);
            return bitmap;
        } catch (WriterException e) {
            return null;
        }
    }
}
