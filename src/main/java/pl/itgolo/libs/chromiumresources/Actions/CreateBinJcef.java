package pl.itgolo.libs.chromiumresources.Actions;

import java.io.*;
import java.lang.reflect.Field;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The type Create bin jcef.
 */
public class CreateBinJcef {

    /**
     * The App dir.
     */
    File appDir;

    /**
     * The Bin dir.
     */
    File binDir;

    /**
     * The Locales dir.
     */
    File localesDir;

    /**
     * The Architecture.
     */
    String architecture;

    /**
     * The Cef locale files.
     */
    String[] cefLocaleFiles = new String[]{"am.pak", "ar.pak", "bg.pak", "bn.pak", "ca.pak", "cs.pak",
            "da.pak", "de.pak", "el.pak", "en-GB.pak", "en-US.pak", "es-419.pak", "es.pak", "et.pak", "fa.pak", "fi.pak",
            "fil.pak", "fr.pak", "gu.pak", "he.pak", "hi.pak", "hr.pak", "hu.pak", "id.pak", "it.pak", "ja.pak",
            "kn.pak", "ko.pak", "lt.pak", "lv.pak", "ml.pak", "mr.pak", "ms.pak", "nb.pak", "nl.pak", "pl.pak",
            "pt-BR.pak", "pt-PT.pak", "ro.pak", "ru.pak", "sk.pak", "sl.pak", "sr.pak", "sv.pak", "sw.pak", "ta.pak",
            "te.pak", "th.pak", "tr.pak", "uk.pak", "vi.pak", "zh-CN.pak", "zh-TW.pak"};

    /**
     * The Cef common files.
     */
    String[] cefCommonFiles = new String[]{"cef.pak", "cef_100_percent.pak", "cef_200_percent.pak",
            "cef_extensions.pak", "d3dcompiler_43.dll", "devtools_resources.pak", "icudtl.dat",
            "natives_blob.bin"};

    /**
     * The Cef architecture files.
     */
    String[] cefArchitectureFiles = new String[]{"d3dcompiler_47.dll",
            "jcef.dll", "jcef_helper.exe", "libcef.dll", "libEGL.dll", "libGLESv2.dll", "snapshot_blob.bin"};

    /**
     * Instantiates a new Create bin jcef.
     *
     * @param appDir the app dir
     */
    public CreateBinJcef(File appDir) {
        this.appDir = appDir;
        this.binDir = new File(appDir, "app/chromium");
        this.localesDir = new File(binDir, "locales");
        setArchitecture();
    }

    private void setArchitecture() {
        String arch = System.getProperty("os.arch");
        if (arch.contains("64")){
            this.architecture = "64";
        } else {
            this.architecture = "32";
        }
    }

    /**
     * Copy from resource.
     *
     * @throws IOException            the io exception
     * @throws NoSuchFieldException   the no such field exception
     * @throws IllegalAccessException the illegal access exception
     */
    public void copyFromResource() throws IOException, NoSuchFieldException, IllegalAccessException {
        extractOrRepairJcefCommonFiles();
        extractOrRepairJcefLocales();
        extractOrRepairJcefArchitectureFiles();
        addLibraryDir(binDir.getPath());
    }

    private void extractOrRepairJcefArchitectureFiles() throws IOException {
        if (!binDir.exists()){
            binDir.mkdirs();
        }
        for (String architectureLocalFileName : cefArchitectureFiles) {
            File architectureFile = new File(binDir.getPath() + File.separator + architectureLocalFileName);
            if (architectureFile.exists()) {
                FileInputStream ofis = new FileInputStream(architectureFile);
                InputStream nfis = CreateBinJcef.class.getResourceAsStream("/jcef_bin/copy_"+this.architecture+"_to_common/" + architectureLocalFileName);
                if (equalMD5(ofis, nfis)){
                    continue;
                }
                architectureFile.delete();
                architectureFile.createNewFile();
            } else {
                architectureFile.createNewFile();
            }
            InputStream is = CreateBinJcef.class.getResourceAsStream("/jcef_bin/copy_"+this.architecture+"_to_common/" + architectureLocalFileName);
            FileOutputStream fos = new FileOutputStream(architectureFile);
            int byteCount = 0;
            byte[] bytes = new byte[1024];
            while ((byteCount = is.read(bytes)) != -1) {
                fos.write(bytes, 0, byteCount);
            }
        }
    }

    /**
     * Add library dir.
     *
     * @param libraryPath the library path
     * @throws NoSuchFieldException   the no such field exception
     * @throws IllegalAccessException the illegal access exception
     */
    public void addLibraryDir(String libraryPath) throws NoSuchFieldException, IllegalAccessException {
        Field userPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        userPathsField.setAccessible(true);
        String[] paths = (String[]) userPathsField.get(null);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paths.length; i++) {
            if (libraryPath.equals(paths[i])) {
                continue;
            }
            sb.append(paths[i]).append(File.pathSeparatorChar);
        }
        sb.append(libraryPath);
        System.setProperty("java.library.path", sb.toString());
        final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
        sysPathsField.setAccessible(true);
        sysPathsField.set(null, null);
    }


    private void extractOrRepairJcefLocales() throws IOException {
        if (!localesDir.exists()){
            localesDir.mkdirs();
        }
        for (String localFileName : cefLocaleFiles) {
            File localeFile = new File(localesDir.getPath() + File.separator + localFileName);
            if (localeFile.exists()) {
                FileInputStream ofis = new FileInputStream(localeFile);
                InputStream nfis = CreateBinJcef.class.getResourceAsStream("/jcef_bin/jcef_common/locales/" + localFileName);
                if (equalMD5(ofis, nfis)){
                    continue;
                }
                localeFile.delete();
                localeFile.createNewFile();
            } else {
                localeFile.createNewFile();
            }
            InputStream is = CreateBinJcef.class.getResourceAsStream("/jcef_bin/jcef_common/locales/" + localFileName);
            FileOutputStream fos = new FileOutputStream(localeFile);
            int byteCount = 0;
            byte[] bytes = new byte[1024];
            while ((byteCount = is.read(bytes)) != -1) {
                fos.write(bytes, 0, byteCount);
            }

        }
    }

    private void extractOrRepairJcefCommonFiles() throws IOException {
        if (!binDir.exists()){
            binDir.mkdirs();
        }
        for (String cefFileName : cefCommonFiles) {
            File cefFile = new File(binDir + File.separator + cefFileName);
            if (cefFile.exists()) {
                FileInputStream ofis = new FileInputStream(cefFile);
                InputStream nfis = CreateBinJcef.class.getResourceAsStream("/jcef_bin/jcef_common/" + cefFileName);
                if (equalMD5(ofis, nfis)) {
                    continue;
                }
                cefFile.delete();
                cefFile.createNewFile();
            } else {
                cefFile.createNewFile();
            }
            InputStream is = CreateBinJcef.class.getResourceAsStream("/jcef_bin/jcef_common/" + cefFileName);
            FileOutputStream fos = new FileOutputStream(cefFile);
            int byteCount = 0;
            byte[] bytes = new byte[1024];
            while ((byteCount = is.read(bytes)) != -1) {
                fos.write(bytes, 0, byteCount);
            }
        }
    }

    /**
     * Byte array to hex string.
     *
     * @param byteArray the byte array
     * @return the string
     */
    public String byteArrayToHex(byte[] byteArray) {
        StringBuilder hex = new StringBuilder();
        for (int n = 0; n < byteArray.length; n++) {
            String stmp = (Integer.toHexString(byteArray[n] & 0XFF));
            if (stmp.length() == 1)
                hex.append('0');
            hex.append(stmp);
        }
        return hex.toString();
    }

    /**
     * Stream md 5 string.
     *
     * @param is the is
     * @return the string
     * @throws IOException              the io exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public String streamMD5(InputStream is) throws IOException, NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        try (DigestInputStream digestInputStream = new DigestInputStream(is, messageDigest)) {
            byte[] buffer = new byte[4 * 1024];
            while (digestInputStream.read(buffer) > 0) ;
            messageDigest = digestInputStream.getMessageDigest();
            byte[] resultByteArray = messageDigest.digest();
            return byteArrayToHex(resultByteArray);
        }
    }

    /**
     * Equal md 5 boolean.
     *
     * @param is0 the is 0
     * @param is1 the is 1
     * @return the boolean
     */
    public boolean equalMD5(InputStream is0, InputStream is1) {
        try {
            String md50 = streamMD5(is0);
            String md51 = streamMD5(is1);
            if (md50 == null || md51 == null) return false;
            return md50.equals(md51);
        } catch (Exception e) {
            return false;
        }
    }
}
