package com.andonov.cloud.availability.util;

import com.andonov.cloud.availability.exception.SecurityDecryptionException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Util {

    private static final String ENCRYPTION = "AES";
    private static final String CIPHER = "AES/CBC/PKCS5PADDING";

    public static void sleep(long interval, TimeUnit unit) {
        try {
            unit.sleep(interval);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static LocalDateTime currentUtcTime() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    public static boolean isDateAfter(LocalDateTime dateToCheck, Duration period) {
        return Objects.nonNull(dateToCheck) && Objects.nonNull(period) && currentUtcTime().isAfter(dateToCheck.plus(period));
    }

    public static byte[] charsToBytes(char[] chars) {
        final ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(chars));
        return Arrays.copyOf(byteBuffer.array(), byteBuffer.limit());
    }

    public static char[] bytesToChars(byte[] bytes) {
        final CharBuffer charBuffer = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes));
        return Arrays.copyOf(charBuffer.array(), charBuffer.limit());
    }

    public static byte[] encrypt(byte[] data, final String encKey, final String encVec) {
        return Base64.encodeBase64(getBytes(data, Cipher.ENCRYPT_MODE, encKey, encVec));
    }

    public static byte[] encrypt(char[] data, final String encKey, final String encVec) {
        return Base64.encodeBase64(getBytes(String.valueOf(data).getBytes(StandardCharsets.UTF_8), Cipher.ENCRYPT_MODE, encKey, encVec));
    }

    public static byte[] decrypt(byte[] encrypted, final String encKey, final String encVec) {
        return getBytes(Base64.decodeBase64(encrypted), Cipher.DECRYPT_MODE, encKey, encVec);
    }

    private static byte[] getBytes(byte[] bytes, int cipherMode, final String encKey, final String encVec) {
        try {
            IvParameterSpec iv = new IvParameterSpec(encVec.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKeySpec = new SecretKeySpec(encKey.getBytes(StandardCharsets.UTF_8), ENCRYPTION);

            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(cipherMode, secretKeySpec, iv);

            byte[] original = cipher.doFinal(bytes);
            return original.clone();
        } catch (Exception e) {
            throw new SecurityDecryptionException(e);
        }
    }

}
