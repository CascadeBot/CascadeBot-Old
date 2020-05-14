package org.cascadebot.cascadebot;

import org.cascadebot.cascadebot.utils.CryptUtils;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CryptUtilsTest {
    @Test
    public void testEncryptDecryptNoTamper() throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, ShortBufferException, InvalidKeyException, CryptUtils.EncryptionTamperedWithException, InvalidAlgorithmParameterException {
        byte[] key = "eThWmZq4t7wbzbCbFbJbNcRfUjXn2r5u".getBytes();
        byte[] hashKey = "bJaNdRfU".getBytes();
        String input = "This is a test!";

        CryptUtils.EncryptResults encryptResults = CryptUtils.encryptString(key, "u8x/A?D*G-KaPdSg".getBytes(), hashKey, input);

        String decrypt = CryptUtils.decryptString(key, "u8x/A?D*G-KaPdSg".getBytes(), hashKey, encryptResults.getEncrypted(), encryptResults.getCryptBytes());
        assertEquals(input, decrypt);
    }

    @Test
    public void testEncryptDecryptTamper() {
        assertThrows(CryptUtils.EncryptionTamperedWithException.class, () -> {
            byte[] key = "WnZr4u7xbAbCbFbJaNdRgUkXp2s5v8yb".getBytes();
            byte[] hashKey = "GbKaPdSg".getBytes();
            String input = "This is a test!";

            CryptUtils.EncryptResults encryptResults = CryptUtils.encryptString(key, "u8x/A?D*G-KaPdSg".getBytes(), hashKey, input);

            byte[] tampered = encryptResults.getEncrypted();
            tampered[9] ^= '0' ^ 9;

            CryptUtils.decryptString(key, "u8x/A?D*G-KaPdSg".getBytes(), hashKey, tampered, encryptResults.getEncrypted().length);
        });
    }
}
