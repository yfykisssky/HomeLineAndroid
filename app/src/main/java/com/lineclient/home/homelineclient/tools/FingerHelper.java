package com.lineclient.home.homelineclient.tools;

import android.content.Context;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.text.TextUtils;
import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Map;

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

    private static FingerprintManagerCompat manager;
    private static CancellationSignal mCancellationSignal;

    public static boolean checkUsed(Context context) {

        manager = FingerprintManagerCompat.from(context);
        if (manager.isHardwareDetected()) {
            if (manager.hasEnrolledFingerprints()) {
                return true;
            }
        }
        return false;
    }

    public static void startCheckWithRadom(Context context, final FingerHelperInterfaceUnLock fingerHelperInterfaceUnLock) {

        try {

            startCheck(context,null,null, new FingerHelperInterface() {
                @Override
                public void success(String data) {
                    fingerHelperInterfaceUnLock.success();
                }

                @Override
                public void failed() {
                    fingerHelperInterfaceUnLock.failed();
                }

                @Override
                public void error(String msg) {
                    fingerHelperInterfaceUnLock.error(msg);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void startCheck(Context context, final String aesPrivateKey, final String signData, final FingerHelperInterface fingerHelperInterface) {

        if (!checkUsed(context)) {
            fingerHelperInterface.failed();
        }

        if (mCancellationSignal == null || mCancellationSignal.isCanceled()) {
            mCancellationSignal = new CancellationSignal();
        }

        try {
            FingerprintManagerCompat.CryptoObject cryptoObject = null;
            if (!TextUtils.isEmpty(aesPrivateKey)) {
                Signature signature = Signature.getInstance("SHA256withECDSA");
                signature.initSign(SignatureUtils.getPrivateKey(aesPrivateKey));
                cryptoObject = new FingerprintManagerCompat.CryptoObject(signature);
            }
            manager.authenticate(cryptoObject, 0, mCancellationSignal, new FingerprintManagerCompat.AuthenticationCallback() {
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
                public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    String msg = null;
                    if (!TextUtils.isEmpty(aesPrivateKey)) {
                        try {
                            Signature signature = result.getCryptoObject().getSignature();
                            signature.update(signData.getBytes());
                            byte[] sigBytes = signature.sign();
                            msg = Base64.encodeToString(sigBytes, Base64.DEFAULT);
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
                    fingerHelperInterface.success(msg);

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
