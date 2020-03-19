class Producer extends Thread {
    private int amount = 0;

    public Producer(String name) {
        super(name);
    }

    @Override
    public void run() {
        // yield();
        while (amount < 500000000) {
            if ((++amount % 100000000) == 0) {
                System.out.println(getName() + " Priority-" + getPriority() + " products " + amount);
            }
        }
    }
}