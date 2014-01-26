/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.fxfiler.interfaces.javafx;

/**
 *
 * @author sk
 */
class FileSizeFormatter {

    public static String format(long size) {
        // TODO テスト通ってないけど眠いのでまた今度
        if (size <= 999) {
            return size + "B";
        }
        if (size <= 999999) {
            return String.format("%.1f", (double) size / 1000) + "K";
        }

        return "TODO";
    }
}
