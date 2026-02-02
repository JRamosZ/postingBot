package com.smartecmx.postingbot.model.Responses;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MetaPageLongTokenResponse {
    private List<PageData> data;
    private Paging paging;

        @Getter
        @Setter
        @NoArgsConstructor
        public static class PageData {
            private String access_token;
            private String category;
            private List<CategoryItem> category_list;
            private String name;
            private String id;
            private List<String> tasks;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class CategoryItem {
            private String id;
            private String name;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Paging {
            private Cursors cursors;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Cursors {
            private String before;
            private String after;
        }
}
