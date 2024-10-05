package common.actions;

/**
 * Класс для ввода команд и вывода результата
 *
 * @author petrovviacheslav
 */
public class Console {


    public static void println(String text) {
        System.out.println(text);
    }

    public static void printerror(String text) {
        System.out.println("$ " + text);
    }


    public static void printf(String text) {
        System.out.printf(text);
    }

}
