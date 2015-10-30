import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;

class VerSig {

    public static void main(String[] args) throws Exception{

        /* Verify a DSA signature */
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the filename: ");
        String inputFile = scanner.nextLine();
        System.out.print("Enter the signature: ");
        String signatureFile = scanner.nextLine();
        System.out.print("Enter the public key: ");
        String publicKey = scanner.nextLine();


            if (inputFile.length() < 4) {
            System.out.println("Usage: VerSig publickeyfile signaturefile datafile");
        }
        else try{

            /* import encoded public key */

            FileInputStream keyfis = new FileInputStream(publicKey);
            byte[] encKey = new byte[keyfis.available()];
            keyfis.read(encKey);

            keyfis.close();

            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);

            KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
            PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);

            /* input the signature bytes */
            FileInputStream sigfis = new FileInputStream(signatureFile);
            byte[] sigToVerify = new byte[sigfis.available()];
            sigfis.read(sigToVerify );

            sigfis.close();

            /* create a Signature object and initialize it with the public key */
            Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
            sig.initVerify(pubKey);

            /* Update and verify the data */

            FileInputStream datafis = new FileInputStream(inputFile);
            BufferedInputStream bufin = new BufferedInputStream(datafis);

            byte[] buffer = new byte[1024];
            int len;
            while (bufin.available() != 0) {
                len = bufin.read(buffer);
                sig.update(buffer, 0, len);
            };

            bufin.close();


            boolean verifies = sig.verify(sigToVerify);

            System.out.println("signature verifies: " + verifies);


        } catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
        };

    }

}