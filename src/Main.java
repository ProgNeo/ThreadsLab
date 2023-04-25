import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final int N = 10;

    public static void main(String[] args) {
        List<Integer> buffer = new ArrayList<>(N);
        Thread producer = new Thread(new Producer(buffer));
        Thread consumer = new Thread(new Consumer(buffer));
        producer.start();
        consumer.start();
    }

    static class Producer implements Runnable {

        private final List<Integer> buffer;

        public Producer(List<Integer> buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i += 1) {
                synchronized (buffer) {
                    while (buffer.size() >= N) {
                        try {
                            buffer.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    int value = (int) ((Math.random() * (100 - 1)) + 1);
                    System.out.printf("Индекс: %d\nЗначение: %d\nПоток: %s\nОперация: случайное число\n\n", i, value, Thread.currentThread().getName());
                    buffer.add(value);
                    buffer.notifyAll();
                }
            }
        }
    }

    static class Consumer implements Runnable {

        private final List<Integer> buffer;

        public Consumer(List<Integer> buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i += 1) {
                synchronized (buffer) {
                    while (buffer.isEmpty()) {
                        try {
                            buffer.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    double value = Math.sin(buffer.remove(0) / Math.PI);
                    System.out.printf("Индекс: %d\nЗначение: %,.3f\nПоток: %s\nОперация: вычисление sin(x/Pi)\n\n", i, value, Thread.currentThread().getName());
                    buffer.notifyAll();
                }
            }
        }
    }
}