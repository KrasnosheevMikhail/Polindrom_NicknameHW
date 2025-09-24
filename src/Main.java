import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static volatile AtomicInteger three = new AtomicInteger();
    public static volatile AtomicInteger four = new AtomicInteger();
    public static volatile AtomicInteger five = new AtomicInteger();

    public static void main(String[] args) {

        Random random = new Random();
        String[] texts = new String[100_000];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }
        new Thread(() -> {
            checkChars(texts, 3, three);
        }).start();
        new Thread(() -> {
            checkChars(texts, 4, four);
        }).start();
        new Thread(() -> {
            checkChars(texts, 5, five);
        }).start();

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void checkChars(String[] texts, int numOfChar, AtomicInteger value) {
        Thread thread1 = new Thread(() -> {
            palindrome(texts, numOfChar, value);
        });
        Thread thread2 = new Thread(() -> {
            oneChar(texts, numOfChar, value);
        });
        Thread thread3 = new Thread(() -> {
            increaseChar(texts, numOfChar, value);
        });

        thread1.start();
        thread2.start();
        thread3.start();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Красивых слов с длиной " + numOfChar + " : " + value + " шт");

    }

    public static void palindrome(String[] texts, int wordLength, AtomicInteger value) {
        StringBuilder string = new StringBuilder();
        String reverseStr = null;
        for (String str : texts) {
            if (str.length() == wordLength) {
                reverseStr = string.append(str).reverse().toString();
            } else continue;
            if (str.equals(reverseStr)) {
                value.getAndIncrement();
            }
        }
    }

    public static void oneChar(String[] texts, int wordLength, AtomicInteger value) {
        StringBuilder string = new StringBuilder();
        String reverseStr = null;

        for (String str : texts) {
            if (str.length() == wordLength) {
                reverseStr = string.append(str).reverse().toString();
            } else continue;
            if (str.chars().allMatch(x -> x == 'a')) value.getAndIncrement();
        }
    }

    public static void increaseChar(String[] texts, int wordLength, AtomicInteger value) {

        int prev = -1;
        StringBuilder string = new StringBuilder();
        String reverseStr = null;
        for (String str : texts) {
            if (str.length() == wordLength) {
                reverseStr = string.append(str).reverse().toString();
            } else continue;
            for (int i : str.getBytes()) {
                if (prev == -1) prev = i;
                else if (prev >= i) {
                    prev = -1;
                    break;
                }
            }
            if (prev != -1) value.getAndIncrement();
        }

    }
}