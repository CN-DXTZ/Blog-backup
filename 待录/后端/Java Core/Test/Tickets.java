class Tickets {
    public int amount = 0;
    public int order = 0;

    public void log(String desc) {
        System.out.println(Thread.currentThread().getName() + " : " + desc);
    }

    public void thread_wait() {
        try {
            wait();
        } catch (Exception e) {
        }
    }

    public synchronized void product() {
        if (order < amount) { // 有存票，生产票线程等待
            log(" wait");
            thread_wait();
        }
        int increase = (int) (Math.random() * 5);
        for (int i = 1; i < increase; i++)
            log(" products ticket-" + (++amount));
        notifyAll();
    }

    public synchronized void sell() {
        if (order >= amount) { // 无存票，售票线程等待
            log(" wait");
            thread_wait();
        }
        log(" sells ticket-" + (++order));
        notifyAll();
    }
}