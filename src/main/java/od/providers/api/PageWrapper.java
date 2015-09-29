/**
 * 
 */
package od.providers.api;

import java.util.List;

import org.springframework.data.domain.Sort;

/**
 * @author ggilbert
 *
 */
public class PageWrapper<T> {
  
  @Override
  public String toString() {
    return "PageWrapper [content=" + content + ", page=" + page + "]";
  }
  
  public class PageInfo {
    @Override
    public String toString() {
      return "PageInfo [totalPages=" + totalPages + ", totalElements=" + totalElements + ", size=" + size + ", number=" + number + "]";
    }
    
    private Integer totalPages;
    private Integer totalElements;
    private Integer size;
    private Integer number;
    
    public Integer getTotalPages() {
      return totalPages;
    }
    public void setTotalPages(Integer totalPages) {
      this.totalPages = totalPages;
    }
    public Integer getTotalElements() {
      return totalElements;
    }
    public void setTotalElements(Integer totalElements) {
      this.totalElements = totalElements;
    }
    public Integer getSize() {
      return size;
    }
    public void setSize(Integer size) {
      this.size = size;
    }
    public Integer getNumber() {
      return number;
    }
    public void setNumber(Integer number) {
      this.number = number;
    }

  }
  
  private List<T> content;
  private PageInfo page;
  
  public List<T> getContent() {
    return content;
  }
  public void setContent(List<T> content) {
    this.content = content;
  }
  public PageInfo getPage() {
    return page;
  }
  public void setPage(PageInfo page) {
    this.page = page;
  }
 }