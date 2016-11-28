package Base64;

public class Base64 {
    private static final String B64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    public String encode(byte[] bytes) {
        StringBuilder str = new StringBuilder((bytes.length * 4) / 3);
        int bin;

        for (int i = 0; i < bytes.length; i += 3) {
            bin = (bytes[i] & 0xFC) >> 2;
            str.append(B64.charAt(bin));
            bin = (bytes[i] & 0x03) << 4;

            if (i + 1 < bytes.length) {
                bin |= (bytes[i + 1] & 0xF0) >> 4;
                str.append(B64.charAt(bin));
                bin = (bytes[i + 1] & 0x0F) << 2;

                if (i + 2 < bytes.length)  {
                    bin |= (bytes[i + 2] & 0xC0) >> 6;
                    str.append(B64.charAt(bin));
                    bin = bytes[i + 2] & 0x3F;
                    str.append(B64.charAt(bin));
                } else  {
                    str.append(B64.charAt(bin));
                    str.append('=');
                }
            } else      {
                str.append(B64.charAt(bin));
                str.append("==");
            }
        }

        return str.toString();
    }

    public byte[] decode(String str) {
        byte[] binary;

       /* if ((str.length() % 4) != 0) {
            binary = new byte[(byte)0x00];
            return binary;
        }*/

        binary = new byte[((str.length() * 3) / 4) - (str.indexOf('=') > 0 ? (str.length() - str.indexOf('=')) : 0)];

        char[] chars = str.toCharArray();
        int j = 0;
        int b[] = new int[4];

        for (int i = 0; i < chars.length; i += 4) {
            b[0] = B64.indexOf(chars[i]);
            b[1] = B64.indexOf(chars[i + 1]);
            b[2] = B64.indexOf(chars[i + 2]);
            b[3] = B64.indexOf(chars[i + 3]);

            binary[j++] = (byte) ((b[0] << 2) | (b[1] >> 4));

            if (b[2] < 64)      {
                binary[j++] = (byte) ((b[1] << 4) | (b[2] >> 2));

                if (b[3] < 64)  {
                    binary[j++] = (byte) ((b[2] << 6) | b[3]);
                }
            }
        }

        return binary;
    }
}
