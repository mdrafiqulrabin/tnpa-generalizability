void done() {
    boolean done = false;
    while (!done) {
        if (remaining() <= 0) {
            done = true;
        }
    }
}
