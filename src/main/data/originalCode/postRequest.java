void postRequest(HttpPost method, HttpReadResult result) throws ConnectionException {
    HttpClient client = HttpConnectionApacheCommon.getHttpClient(data.getSslMode());
    method.setHeader("User-Agent", HttpConnection.USER_AGENT);
    if (getCredentialsPresent()) {
        method.addHeader("Authorization", "Basic " + getCredentials());
    }
    HttpResponse httpResponse = client.execute(method);
    StatusLine statusLine = httpResponse.getStatusLine();
    result.statusLine = statusLine.toString();
    result.setStatusCode(statusLine.getStatusCode());
    result.strResponse = HttpConnectionApacheCommon.readHttpResponseToString(httpResponse);
}
