package org.cascadebot.cascadebot;

import org.cascadebot.cascadebot.utils.CryptUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class CryptUtilsBenchmark {

    int hmacI = 0;
    int normalI = 0;

    List<String> testStrings = new ArrayList<>();

    @Setup
    public void setup() {
        Random random = new Random();
        for (int i = 0; i < 400; i++) {
            byte[] bytes = new byte[random.nextInt(2000)];
            random.nextBytes(bytes);
            testStrings.add(new String(bytes));
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void runHmacBenchmark() throws NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, ShortBufferException, InvalidKeyException {
        byte[] key = "eThWmZq4t7wbzbCbFbJbNcRfUjXn2r5u".getBytes();
        byte[] hashKey = "bJaNdRfU".getBytes();
        byte[] iv = "u8x/A?D*G-KaPdSg".getBytes();
        String testString = testStrings.get(hmacI % 400);
        CryptUtils.encryptString(key, iv, hashKey, testString);
        hmacI++;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void runNormalBenchmark() throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException {
        byte[] key = "eThWmZq4t7wbzbCbFbJbNcRfUjXn2r5u".getBytes();
        byte[] iv = "u8x/A?D*G-KaPdSg".getBytes();
        String testString = testStrings.get(normalI % 400);
        CryptUtils.encryptString(key, iv, testString);
        normalI++;
    }

}
