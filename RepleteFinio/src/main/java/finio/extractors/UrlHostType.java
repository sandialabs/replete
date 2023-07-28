package finio.extractors;

public enum UrlHostType {
    IP,         // http://124.62.12.180[:port]/path/to/resource.html
    NETSRV,     // http://bobsmachine[:port]/path/to/resource.html
    HIERARCHY   // http://[[abc.]www.]name.com[:port]/path/to/resource.html
}
