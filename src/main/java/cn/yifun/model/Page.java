package cn.yifun.model;


import lombok.Builder;
import lombok.Data;

/**
 * 分页实体
 *
 * @author yifun
 */
@Data
@Builder
public class Page {

  private Integer pageNum;

  private Integer pageSize;

  private Integer total;

  private Boolean isPage = false;

}
