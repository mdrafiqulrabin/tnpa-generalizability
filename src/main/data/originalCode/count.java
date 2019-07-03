int count(String target, ArrayList<String> array) {
    int count = 0;
    for (String str: array) {
        if (target.equals(str)) {
            count++;
        }
    }
    return count;
}
