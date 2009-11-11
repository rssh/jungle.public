package ua.gradsoft.jungle.persistence.cluster_keys.impl;


/**
 * Quick and dirty base64 encoder
 * @author rssh
 */
public class Base64Encoder {

    public static String base64code = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                   "abcdefghijklmnopqrstuvwxyz" + "0123456789" + "+/";


    public static String base64Encode(byte[] bytes) {

        StringBuilder encoded = new StringBuilder();
        // process 3 bytes at a time, churning out 4 output bytes
        for (int i = 0; i < bytes.length; i += 3) {

            byte bi0 = 0;
            byte bi1 = 0;
            byte bi2 = 0;
            int nPadding=0;

            if (i+2 < bytes.length) {
                bi2=bytes[i+2];
                bi1=bytes[i+1];
                bi0=bytes[i];
            }else{
                if (i+1 < bytes.length) {
                  bi1=bytes[i+1];
                  bi0=bytes[i];
                  nPadding=1;
                }else{
                    bi0=bytes[i];
                    nPadding=2;
                }
            }

            int j = (bi0 << 16) + (bi1 << 8) + bi2;

            encoded.append(base64code.charAt(j>>18) & 0x3f);
            encoded.append(base64code.charAt(j>>12) & 0x3f);
            if (nPadding<2) {
              encoded.append(base64code.charAt(j>>6) & 0x3f);
            }else{
              encoded.append('=');
            }
            if (nPadding<1) {
              encoded.append(base64code.charAt(j) & 0x3f);
            }else{
              encoded.append('=');
            }

        }

        return encoded.toString();

    }


}

