package cn.xsaf1207.model;

/**
 * 分页实体
 *
 * @author yifun
 */
public class Page {

  private Integer pageNum;

  private Integer pageSize;

  private Integer total;

  private Boolean isPage = false;

  public Page(Integer pageNum, Integer pageSize, Integer total, Boolean isPage) {
    this.pageNum = pageNum;
    this.pageSize = pageSize;
    this.total = total;
    this.isPage = isPage;
  }

  public Page() {
  }

  public Integer getPageNum() {
    return pageNum;
  }

  public void setPageNum(Integer pageNum) {
    this.pageNum = pageNum;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  public Boolean getIsPage() {
    return isPage;
  }

  public void setIsPage(Boolean isPage) {
    this.isPage = isPage;
  }


  public Page(Integer pageNum, Integer pageSize, Boolean isPage) {
    this.pageNum = pageNum;
    this.pageSize = pageSize;
    this.isPage = isPage;
  }
}
