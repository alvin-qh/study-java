package alvin.study.se.security.algorithms;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.spec.InvalidKeySpecException;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link RSAKeyLoader} 类型, 将指定格式的文本数据转为公私钥字节串数据
 * <p>
 * cspell: disable
 */
class RSAKeyLoaderTest {
    // 私钥文本数据
    private static final String PRIVATE_KEY = """
        -----BEGIN PRIVATE KEY-----
        MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAN2Vq1GNGOiCjdai
        OAYcUdgu6B1RYBj2JHd/LhqtY0DUqhLyRXDfdwmJtevxu/BQBSlqsLCW91sfp28Q
        5+i7T+AIVCwdR9CtIO/4y5JQwB7yPMoTipb6Mr7FBT1rTcZScoeSSV75DSlf+DqN
        dnuvX/EArkOjaRD5fnEr1yKlGAQrAgMBAAECgYAP6icC1XJ7iJztVHtXnQMX1s6f
        BHA1esBFwOGr0tY1GtEYSURJhhNDoRtq1dLcwLKONfZ4yG79oXliFcHCSmnDGD4Q
        6Zd6H4zMvtmHbzg649uSCUBoaFX1tvr/kKm4ZvV6zI9thb5PLY8LQZJl8heRezvq
        NcC71M4G8PTJtzZiWQJBAPLCDZH/u3dDY4Tb7KjfzYIsl2uVItVE5YrBvi1vY+OF
        jhcDBXx3W/LRF6fFMH4rky7nu5VJMe2swrQYC0WvzfcCQQDpq/HbQ4ejrVh6Vr/i
        TmRAO2MR/U3Qt8j4FxZm3GxLeMMzeLUenQpK7muwI9vxpwmjlE7bqAZWt8CwNwhc
        oL5tAkBlnZcimyK0vI+m6Iw68FvM9q93iBjpnwpat9jMmgj9D0W4GjqsrCXgEWhO
        gPtYhRL6GmRqDBaLP7rMuhfV1s5nAkAKYfl9JKMCQtGLng8onxMVR44/XmH2xDPJ
        0jzMlT66m8MQpmxlz9SFP9LJIM7FDgb/nbdjSzP85m1JZyiX9QyBAkAt6zL74Z2+
        ggMt8lfkYFCm4WAV6dGJENpvGsT0w1RnZo8VoWn4PIB75vicPC4lpbYtRFuvwARm
        BruhCPS5jFxj
        -----END PRIVATE KEY-----""";

    // 公钥文本数据
    private static final String PUBLIC_KEY = """
        -----BEGIN PUBLIC KEY-----
        MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDdlatRjRjogo3WojgGHFHYLugd
        UWAY9iR3fy4arWNA1KoS8kVw33cJibXr8bvwUAUparCwlvdbH6dvEOfou0/gCFQs
        HUfQrSDv+MuSUMAe8jzKE4qW+jK+xQU9a03GUnKHkkle+Q0pX/g6jXZ7r1/xAK5D
        o2kQ+X5xK9cipRgEKwIDAQAB
        -----END PUBLIC KEY-----""";

    /**
     * 测试 {@link RSAKeyLoader#decodePublicKey(String)} 方法, 将指定格式的公钥文本数据转为字节串
     */
    @Test
    void decodePublicKey_shouldDecodePublicKeyFromBase64String() throws InvalidKeySpecException, IOException {
        var keyData = RSAKeyLoader.decodePublicKey(PUBLIC_KEY);
        var keyStr = Base64.encodeBase64String(keyData);

        then(keyStr).isEqualTo("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDdlatRjRjogo"
            + "3WojgGHFHYLugdUWAY9iR3fy4arWNA1KoS8kVw33cJibXr8bvwUAUparCwlvdbH6"
            + "dvEOfou0/gCFQsHUfQrSDv+MuSUMAe8jzKE4qW+jK+xQU9a03GUnKHkkle+Q0pX/"
            + "g6jXZ7r1/xAK5Do2kQ+X5xK9cipRgEKwIDAQAB");
    }

    /**
     * 测试 {@link RSAKeyLoader#decodePrivateKey(String)} 方法, 将指定格式的私钥文本数据转为字节串
     */
    @Test
    void decodePrivateKey_shouldDecodePrivateKeyFromBase64String() throws InvalidKeySpecException, IOException {
        var keyData = RSAKeyLoader.decodePrivateKey(PRIVATE_KEY);
        var keyStr = Base64.encodeBase64String(keyData);

        then(keyStr).isEqualTo("MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAN"
            + "2Vq1GNGOiCjdaiOAYcUdgu6B1RYBj2JHd/LhqtY0DUqhLyRXDfdwmJtevxu/BQBS"
            + "lqsLCW91sfp28Q5+i7T+AIVCwdR9CtIO/4y5JQwB7yPMoTipb6Mr7FBT1rTcZSco"
            + "eSSV75DSlf+DqNdnuvX/EArkOjaRD5fnEr1yKlGAQrAgMBAAECgYAP6icC1XJ7iJ"
            + "ztVHtXnQMX1s6fBHA1esBFwOGr0tY1GtEYSURJhhNDoRtq1dLcwLKONfZ4yG79oX"
            + "liFcHCSmnDGD4Q6Zd6H4zMvtmHbzg649uSCUBoaFX1tvr/kKm4ZvV6zI9thb5PLY"
            + "8LQZJl8heRezvqNcC71M4G8PTJtzZiWQJBAPLCDZH/u3dDY4Tb7KjfzYIsl2uVIt"
            + "VE5YrBvi1vY+OFjhcDBXx3W/LRF6fFMH4rky7nu5VJMe2swrQYC0WvzfcCQQDpq/"
            + "HbQ4ejrVh6Vr/iTmRAO2MR/U3Qt8j4FxZm3GxLeMMzeLUenQpK7muwI9vxpwmjlE"
            + "7bqAZWt8CwNwhcoL5tAkBlnZcimyK0vI+m6Iw68FvM9q93iBjpnwpat9jMmgj9D0"
            + "W4GjqsrCXgEWhOgPtYhRL6GmRqDBaLP7rMuhfV1s5nAkAKYfl9JKMCQtGLng8onx"
            + "MVR44/XmH2xDPJ0jzMlT66m8MQpmxlz9SFP9LJIM7FDgb/nbdjSzP85m1JZyiX9Q"
            + "yBAkAt6zL74Z2+ggMt8lfkYFCm4WAV6dGJENpvGsT0w1RnZo8VoWn4PIB75vicPC"
            + "4lpbYtRFuvwARmBruhCPS5jFxj");
    }
}
