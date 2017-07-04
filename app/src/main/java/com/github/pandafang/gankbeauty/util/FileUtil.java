package com.github.pandafang.gankbeauty.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by panda on 2017/6/25.
 */

public class FileUtil {

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        BufferedInputStream bis = new BufferedInputStream(in);
        try {

            OutputStream out = new FileOutputStream(dst);
            BufferedOutputStream bos = new BufferedOutputStream(out);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[4096];
                int len;
                while ((len = bis.read(buf)) > 0) {
                    bos.write(buf, 0, len);
                }
            } finally {
                bos.close();
            }
        } finally {
            bis.close();
        }
    }
}
