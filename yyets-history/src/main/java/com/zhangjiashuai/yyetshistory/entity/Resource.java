package com.zhangjiashuai.yyetshistory.entity;

import lombok.Data;

import java.util.List;

@Data
public class Resource {
    private long id;
    private String name;
    private String nameEN;
    private String channel;
    private String area;
    private List<Season> seasons;

    @Data
    public static class Season implements Comparable<Season> {
        private int number;
        private String name;
        private List<Group> groups;

        @Override
        public int compareTo(Season that) {
            return Integer.compare(this.number, that.number);
        }
    }

    @Data
    public static class Group implements Comparable<Group> {
        private String name;
        private List<Item> items;

        @Override
        public int compareTo(Group that) {
            return this.name.compareTo(that.name);
        }
    }

    @Data
    public static class Item implements Comparable<Item> {
        private String name;
        private int episode;
        private String size;
        private List<Link> links;

        @Override
        public int compareTo(Item that) {
            return  Integer.compare(this.episode, that.episode);
        }
    }

    @Data
    public static class Link implements Comparable<Link> {
        private String way;
        private String address;

        @Override
        public int compareTo(Link that) {
            return this.way.compareTo(that.way);
        }
    }
}
