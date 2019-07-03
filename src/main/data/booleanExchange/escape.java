class C {

    String escape(String txt) {
        txt = replace(txt, "&", "&amp;");
        txt = replace(txt, "\"", "&quote;");
        txt = replace(txt, "<", "&lt;");
        txt = replace(txt, ">", "&gt;");
        return txt;
    }
}

