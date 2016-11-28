package SHA1;

public class SHA1 {

    /*
     * Bitwise rotate a 32-bit word to the left
     */
    private static int rol(int word, int count) {
        return (word << count) | (word >>> (32 - count));
    }

    /*
     * Take a string and return the base64 representation of its SHA-1.
     */
    public static String encode(byte[] bytes) {
        int[] blocks = new int[(((bytes.length + 8) >> 6) + 1) * 16];
        int i;

        for (i = 0; i < bytes.length; i++) {
            blocks[i >> 2] |= bytes[i] << (24 - (i % 4) * 8);
        }

        blocks[i >> 2] |= 0x80 << (24 - (i % 4) * 8);
        blocks[blocks.length - 1] = bytes.length * 8;

        int[] w = new int[80];

        int h0 =  1732584193;
        int h1 = -271733879;
        int h2 = -1732584194;
        int h3 =  271733878;
        int h4 = -1009589776;

        for (i = 0; i < blocks.length; i += 16) {
            int a = h0;
            int b = h1;
            int c = h2;
            int d = h3;
            int e = h4;

            for (int j = 0; j < 80; j++) {
                if (j < 16) {
                    w[j] = blocks[i + j];
                } else {
                    w[j] = (rol(w[j-3] ^ w[j-8] ^ w[j-14] ^ w[j-16], 1));
                }

                int t;

                if (j < 20) {
                    t = rol(a, 5) + e + w[j] + 1518500249 + ((b & c) | ((~b) & d));
                } else if (j < 40) {
                    t = rol(a, 5) + e + w[j] + 1859775393 + (b ^ c ^ d);
                } else if (j < 60) {
                    t = rol(a, 5) + e + w[j] + -1894007588 + ((b & c) | (b & d) | (c & d));
                } else {
                    t = rol(a, 5) + e + w[j] + -899497514 + (b ^ c ^ d);
                }

                e = d;
                d = c;
                c = rol(b, 30);
                b = a;
                a = t;
            }

            h0 = a + h0;
            h1 = b + h1;
            h2 = c + h2;
            h3 = d + h3;
            h4 = e + h4;
        }

        String hh = Integer.toHexString(h0) + Integer.toHexString(h1) + Integer.toHexString(h2) + Integer.toHexString(h3) + Integer.toHexString(h4);

        return hh;
    }
}