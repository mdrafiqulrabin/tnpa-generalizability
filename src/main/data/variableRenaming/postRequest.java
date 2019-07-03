class C {

    void postRequest(HttpPost var0, HttpReadResult var1) throws ConnectionException {
        HttpClient var2 = HttpConnectionApacheCommon.getHttpClient(data.getSslMode());
        var0.setHeader("User-Agent", HttpConnection.USER_AGENT);
        if (getCredentialsPresent()) {
            var0.addHeader("Authorization", "Basic " + getCredentials());
        }
        HttpResponse var3 = var2.execute(var0);
        StatusLine var4 = var3.getStatusLine();
        var1.var4 = var4.toString();
        var1.setStatusCode(var4.getStatusCode());
        var1.strResponse = HttpConnectionApacheCommon.readHttpResponseToString(var3);
    }
}

