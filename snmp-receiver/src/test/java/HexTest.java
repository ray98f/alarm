import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class HexTest {


    public static void main(String[] args) {
        String hexString = "ba:a3:b3:c7:d5:be:2c:4f:49:58:47:34:41:5b:30:2d:31:2d:31:33:5d:2d:31:30:47:45:3a:31".replace(
            ":", "");
        byte[] byteArray = new byte[hexString.length() / 2];
        for (int i = 0; i < byteArray.length; i++) {
            // 每两个十六进制字符对应一个字节
            int hex = Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
            // 强制类型转换为字节
            byteArray[i] = (byte) hex;
        }
        // 将字节数组转换为GBK编码的字符串
        String gbkString = new String(byteArray, Charset.forName("GBK"));
        System.out.println(gbkString.substring(gbkString.indexOf(",")+ 1) );
    }
}
