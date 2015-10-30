import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.*;
import java.util.Scanner;

class GenSig {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the filename: ");
        String test = scanner.nextLine();
        /* Generate a DSA signature */

        if (test.length() < 4) {
            System.out.println("Usage: GenSig nameOfFileToSign");
        }
        else try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(1024, random);
            KeyPair pair = keyGen.generateKeyPair();
            PrivateKey priv = pair.getPrivate();
            PublicKey pub = pair.getPublic();
            Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
            dsa.initSign(priv);
            FileInputStream fis = new FileInputStream(test);
            BufferedInputStream bufin = new BufferedInputStream(fis);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bufin.read(buffer)) >= 0) {
                dsa.update(buffer, 0, len);
            }
            bufin.close();
            byte[] realSig = dsa.sign();
            /* save the signature in a file */
            FileOutputStream sigfos = new FileOutputStream("sig");
            sigfos.write(realSig);
            sigfos.close();
            /* save the public key in a file */
            byte[] key = pub.getEncoded();
            FileOutputStream keyfos = new FileOutputStream("suepk");
            keyfos.write(key);
            keyfos.close();



        } catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
        }
    }
}