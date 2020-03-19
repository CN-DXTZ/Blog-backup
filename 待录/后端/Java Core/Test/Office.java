class Office {
    public static void main(String args[]) {

        Thread[] runner = new Thread[10];
        for (int i = 1; i <= 10; i++) {
            runner[i - 1] = new Producer("Producer-" + i);
            runner[i - 1].setPriority(1);
        }
        for (int i = 0; i < 10; i++) {
            runner[i].start();
        }
    }
}