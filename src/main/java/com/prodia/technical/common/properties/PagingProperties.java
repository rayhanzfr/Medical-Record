package com.prodia.technical.common.properties;

import com.prodia.technical.common.model.request.SortByDirection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("prodia.paging")
public class PagingProperties {

  private Integer defaultPage = 1;

  private Integer defaultPageSize = 10;

  private SortByDirection defaultSortDirection = SortByDirection.ASC;

  private Integer maxPageSize = 100;

  private Query query = new Query();

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Query {

    private String pageKey = "page";

    private String pageSizeKey = "pageSize";

    private String sortByKey = "sortBy";

  }

}
