package com.example.a119_saver;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "response", strict = false)
public class EmergencyListResponse {
    @Element(name = "header", required = false)
    private Header header;

    @Element(name = "body", required = false)
    private Body body;

    public Header getHeader() { return header; }
    public Body getBody() { return body; }

    @Root(name = "header", strict = false)
    public static class Header {
        @Element(name = "resultCode", required = false)
        private String resultCode;

        @Element(name = "resultMsg", required = false)
        private String resultMsg;

        public String getResultCode() { return resultCode; }
        public String getResultMsg() { return resultMsg; }
    }

    @Root(name = "body", strict = false)
    public static class Body {
        @ElementList(name = "items", entry = "item", inline = false, required = false)
        private List<EmergencyListItem> items;

        @Element(name = "numOfRows", required = false)
        private int numOfRows;

        @Element(name = "pageNo", required = false)
        private int pageNo;

        @Element(name = "totalCount", required = false)
        private int totalCount;

        public List<EmergencyListItem> getItems() { return items; }
        public int getNumOfRows() { return numOfRows; }
        public int getPageNo() { return pageNo; }
        public int getTotalCount() { return totalCount; }
    }
}