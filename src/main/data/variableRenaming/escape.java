class C {

    String escape(String var0) {
        var0 = replace(var0, "&", "&amp;");
        var0 = replace(var0, "\"", "&quote;");
        var0 = replace(var0, "<", "&lt;");
        var0 = replace(var0, ">", "&gt;");
        return var0;
    }
}

