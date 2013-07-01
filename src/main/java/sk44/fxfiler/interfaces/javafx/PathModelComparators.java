/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.fxfiler.interfaces.javafx;

import java.util.Comparator;

/**
 *
 * @author sk
 */
enum PathModelComparators implements Comparator<PathModel> {

    BY_DEFAULT {
        @Override
        public int compare(PathModel o1, PathModel o2) {
            if (o1.isDirectory() && o2.isDirectory() == false) {
                return -1;
            }
            if (o1.isDirectory() == false && o2.isDirectory()) {
                return 1;
            }
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    };
}
