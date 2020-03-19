import java.util.Locale;

class Main {
    public static void main(String args[]) {
        // System.out.println(UUID.randomUUID().toString());
        Locale[] list = Locale.getAvailableLocales();
        for (int i = 0; i < list.length; i++) {
            System.out.println(list[i].getLanguage());
        }
    }
}