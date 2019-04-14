package com.example.ruifeng.mytranslate.BaiduTranslate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 encoding related classes
 * 
 * @author 
 * 
 */
public class MD5 {
    // First initialize an array of characters to hold each hexadecimal character
    private static final char[] Hex_Digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f' };

    /**
     * Get the MD5 value of a string
     * 
     * @param input Input string
     * @return Enter the MD5 value of the string
     * 
     */
    public static String md5(String input) {
        if (input == null)
            return null;

        try {
            // Get an MD5 converter
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // Convert the input string to a byte array
            byte[] inputByteArray = null;
			try {
				inputByteArray = input.getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            // inputByteArray is an array of bytes from the input string conversion
            messageDigest.update(inputByteArray);
            // Convert and return the result, also a byte array containing 16 elements
            byte[] resultByteArray = messageDigest.digest();
            // Convert character array to string return
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * Get the MD5 value of the file
     * 
     * @param file
     * @return
     */
    public static String md5(File file) {
        try {
            if (!file.isFile()) {
                System.err.println("file" + file.getAbsolutePath() + "not exit file or is not right formal");
                return null;
            }

            FileInputStream in = new FileInputStream(file);

            String result = md5(in);

            in.close();

            return result;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String md5(InputStream in) {

        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                messagedigest.update(buffer, 0, read);
            }

            in.close();

            String result = byteArrayToHex(messagedigest.digest());

            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String byteArrayToHex(byte[] byteArray) {
        // New a character array, this is used to form the result string（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray = new char[byteArray.length * 2];
        // Traversing a byte array, passing a bitwise operation , converting it into a character array
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = Hex_Digits[b >>> 4 & 0xf];
            resultCharArray[index++] = Hex_Digits[b & 0xf];
        }

        // Combine character arrays into a string return
        return new String(resultCharArray);

    }

}
