package org.cascadebot.cascadebot.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

public class CryptUtils {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static EncryptResults encryptString(byte[] key, byte[] iv, byte[] hashKey, String encrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, ShortBufferException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
        Mac mac = Mac.getInstance("HmacSHA512");

        SecretKeySpec hmacKey = new SecretKeySpec(hashKey, "HmacSHA512");
        mac.init(hmacKey);

        byte[] encryptBytes = encrypt.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = new byte[cipher.getOutputSize(encryptBytes.length + mac.getMacLength())];

        int cypherLength = cipher.update(encryptBytes, 0, encryptBytes.length, encrypted, 0);

        mac.update(encryptBytes);

        cypherLength += cipher.doFinal(mac.doFinal(), 0, mac.getMacLength(), encrypted, cypherLength);

        return new EncryptResults(encrypted, cypherLength);
    }

    public static String decryptString(byte[] key, byte[] iv, byte[] hashKey, byte[] decrypt, int cypherLength) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, EncryptionTamperedWithException, InvalidAlgorithmParameterException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));

        Mac hmac = Mac.getInstance("HmacSHA512");
        SecretKeySpec hmacKey = new SecretKeySpec(hashKey, "HmacSHA512");
        hmac.init(hmacKey);

        byte[] decrypted = cipher.doFinal(decrypt, 0, cypherLength);
        int decryptLength = decrypted.length - hmac.getMacLength();

        hmac.update(decrypted, 0, decryptLength);

        byte[] hash = new byte[hmac.getMacLength()];
        System.arraycopy(decrypted, decryptLength, hash, 0, hash.length);

        byte[] text = new byte[decryptLength];
        System.arraycopy(decrypted, 0, text, 0, decryptLength);
        String decryptedString = new String(text, StandardCharsets.UTF_8);

        byte[] mac = hmac.doFinal();

        if (!MessageDigest.isEqual(mac, hash)) {
            throw new EncryptionTamperedWithException("Encryption was tampered with! Check security.");
        }

        return decryptedString;

    }

    public static EncryptResults encryptString(byte[] key, byte[] iv, String encrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));

        byte[] encryptBytes = encrypt.getBytes();
        byte[] encrypted = new byte[cipher.getOutputSize(encryptBytes.length)];

        int cypherLength = cipher.update(encryptBytes, 0, encryptBytes.length, encrypted, 0);
        cypherLength += cipher.doFinal(encrypted, cypherLength);

        return new EncryptResults(encrypted, cypherLength);
    }

    public static String decryptString(byte[] key, byte[] iv, byte[] decrypt, int cypherLength) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));

        byte[] decrypted = new byte[cypherLength];
        int decryptLength = cipher.update(decrypt, 0, decrypt.length, decrypted, 0);
        cipher.doFinal(decrypted, decryptLength);

        return new String(decrypted);
    }

    public static class EncryptResults {

        private final byte[] encrypted;

        private final int cryptBytes;

        public EncryptResults(byte[] encrypted, int cryptBytes) {
            this.encrypted = encrypted;
            this.cryptBytes = cryptBytes;
        }

        public byte[] getEncrypted() {
            return encrypted;
        }

        public int getCryptBytes() {
            return cryptBytes;
        }
    }

    public static class EncryptionTamperedWithException extends Exception {
        public EncryptionTamperedWithException(String message) {
            super(message);
        }
    }

}
