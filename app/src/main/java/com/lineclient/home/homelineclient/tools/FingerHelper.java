package com.lineclient.home.homelineclient.tools;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerHelper {

    public interface FingerHelperInterface {

        void success(String data);

        void failed();

        void error(String msg);

    }

    public interface FingerHelperInterfaceUnLock {

        void success();

        void failed();

        void error(String msg);

    }

    private static FingerprintManager manager;
    private static CancellationSignal mCancellationSignal;

    public static boolean checkUsed(Context context){

     /*   manager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);

        if (manager.isHardwareDetected()) {
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager.isKeyguardSecure()) {
                if (manager.hasEnrolledFingerprints()) {
                    return true;
                }
            }
        }*/
        return false;
    }

    public static void startCheckWithRadom(Context context, final FingerHelperInterfaceUnLock fingerHelperInterfaceUnLock) {

        try {

            final String signData = SignatureUtils.getRandomString(15);

            Map<String, Object> map = RSAHelper.RSAUtils.genKeyPair();
            final String publicKey = RSAHelper.RSAUtils.getPublicKey(map);
            String privateKey = RSAHelper.RSAUtils.getPrivateKey(map);

            startCheck(context, privateKey, signData, new FingerHelperInterface() {
                @Override
                public void success(String data) {

                    try {
                        Signature signature = Signature.getInstance("SHA256withECDSA");
                        signature.initVerify(SignatureUtils.getPublicKey(publicKey));
                        signature.update(signData.getBytes());
                        if (signature.verify(Base64.decode(data,Base64.DEFAULT))) {
                            fingerHelperInterfaceUnLock.success();
                        } else {
                            fingerHelperInterfaceUnLock.error("");
                        }
                    } catch (SignatureException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void failed() {
                    fingerHelperInterfaceUnLock.failed();
                }

                @Override
                public void error(String msg) {
                    fingerHelperInterfaceUnLock.error("");
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void startCheck(Context context,String aesPrivateKey, final String signData, final FingerHelperInterface fingerHelperInterface) {

        if(!checkUsed(context)){
            fingerHelperInterface.failed();
        }

        if (mCancellationSignal == null || mCancellationSignal.isCanceled()) {
            mCancellationSignal = new CancellationSignal();
        }

        try {

            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(SignatureUtils.getPrivateKey(aesPrivateKey));
            manager.authenticate(new FingerprintManager.CryptoObject(signature), mCancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    fingerHelperInterface.error(errString.toString());
                }

                @Override
                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                    super.onAuthenticationHelp(helpCode, helpString);
                }

                @Override
                public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);

                    try {
                        Signature signature = result.getCryptoObject().getSignature();
                        signature.update(signData.getBytes());
                        byte[] sigBytes = signature.sign();
                        fingerHelperInterface.success(Base64.encodeToString(sigBytes,Base64.DEFAULT));

                    /*    String signData="";
                        String signByte="";
                        Signature signature = Signature.getInstance("SHA256withECDSA");
                        signature.initVerify(SignatureUtils.getPublicKey(""));
                        signature.update(signData.getBytes());
                        if(signature.verify(new BASE64Decoder().decodeBuffer(signByte))){

                        }else{

                        }*/

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    mCancellationSignal.cancel();
                    fingerHelperInterface.failed();
                }
            }, null);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
