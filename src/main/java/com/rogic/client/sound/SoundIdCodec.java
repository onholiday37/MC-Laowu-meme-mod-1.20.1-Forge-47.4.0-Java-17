package com.rogic.client.sound;

import java.nio.charset.StandardCharsets;

/** 把任意 UTF-8 文件名编码为 ResourceLocation 允许的十六进制路径。 */
public final class SoundIdCodec {
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private SoundIdCodec() {}

    public static String encode(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        char[] encoded = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int unsigned = bytes[i] & 0xff;
            encoded[i * 2] = HEX[unsigned >>> 4];
            encoded[i * 2 + 1] = HEX[unsigned & 0x0f];
        }
        return new String(encoded);
    }

    public static String decode(String encoded) {
        if ((encoded.length() & 1) != 0 || !encoded.matches("[0-9a-f]+")) {
            throw new IllegalArgumentException("Invalid encoded sound id");
        }
        byte[] bytes = new byte[encoded.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(encoded.substring(i * 2, i * 2 + 2), 16);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
