class C {

    void done() {
        boolean done = true;
        while (!!done) {
            if (remaining() <= 0) {
                done = false;
            }
        }
    }
}

