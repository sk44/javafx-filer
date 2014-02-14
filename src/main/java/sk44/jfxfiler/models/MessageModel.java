/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.jfxfiler.models;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author sk
 */
public class MessageModel {

    /**
     メッセージの更新通知を受け取ってなんかする.
     */
    public interface Observer {

        /**
         メッセージの更新をなにかしら反映する.

         @param message メッセージ
         */
        void update(String message);
    }

    private enum Subject {

        // enum singleton.
        INSTANCE;

        private final List<Observer> observers = new ArrayList<>();

        public void notifyObservers(String message) {
            for (Observer o : observers) {
                o.update(message);
            }
        }

        public void addObserver(Observer o) {
            observers.add(o);
        }

        public void clearObserver() {
            observers.clear();
        }
    }

    private static final String INFO_PREFIX = "[INFO] ";
    private static final String WARN_PREFIX = "[WARN] ";
    private static final String ERROR_PREFIX = "[ERROR] ";

    public static void info(String message) {
        Subject.INSTANCE.notifyObservers(INFO_PREFIX + message);
    }

    public static void warn(String message) {
        Subject.INSTANCE.notifyObservers(WARN_PREFIX + message);
    }

    public static void error(String message) {
        Subject.INSTANCE.notifyObservers(ERROR_PREFIX + message);
    }

    public static void error(Throwable t) {
        Subject.INSTANCE.notifyObservers(ERROR_PREFIX + t.getLocalizedMessage());
        try {
            Subject.INSTANCE.notifyObservers(convertStackTraceToString(t));
        } catch (IOException ex) {
            // TODO うーむ
            Subject.INSTANCE.notifyObservers(t.toString());
            Subject.INSTANCE.notifyObservers(ERROR_PREFIX + "An error occured during getting stacktrace from exception.");
            Subject.INSTANCE.notifyObservers(ex.getLocalizedMessage());
        }
    }

    public static void addObserver(Observer observer) {
        Subject.INSTANCE.addObserver(observer);
    }

    public static void clearAllObservers() {
        Subject.INSTANCE.clearObserver();
    }

    private static String convertStackTraceToString(Throwable t) throws IOException {
        try (StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw)) {
            t.printStackTrace(pw);
            return sw.toString();
        }
    }

    private final StringProperty messageProperty = new SimpleStringProperty();

    public MessageModel(String initialMessage) {
        messageProperty.set(initialMessage);
    }

    public StringProperty messageProperty() {
        return messageProperty;
    }

    public void appendMessage(String message) {
        // TODO 行数上限を持つとかするとか
        messageProperty.set(messageProperty.get() + "\n" + message);
    }

}
